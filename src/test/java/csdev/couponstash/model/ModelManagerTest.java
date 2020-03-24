package csdev.couponstash.model;

import static csdev.couponstash.model.Model.PREDICATE_SHOW_ALL_ACTIVE_COUPONS;
import static csdev.couponstash.testutil.Assert.assertThrows;
import static csdev.couponstash.testutil.TypicalCoupons.ALICE;
import static csdev.couponstash.testutil.TypicalCoupons.BENSON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import csdev.couponstash.commons.core.GuiSettings;
import csdev.couponstash.model.coupon.NameContainsKeywordsPredicate;
import csdev.couponstash.testutil.CouponStashBuilder;
import csdev.couponstash.testutil.TypicalCoupons;

public class ModelManagerTest {

    private ModelManager modelManager = new ModelManager();

    @Test
    public void constructor() {
        assertEquals(new UserPrefs(), modelManager.getUserPrefs());
        assertEquals(new GuiSettings(), modelManager.getGuiSettings());
        assertEquals(new CouponStash(), new CouponStash(modelManager.getCouponStash()));
    }

    @Test
    public void setUserPrefs_nullUserPrefs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setUserPrefs(null));
    }

    @Test
    public void setUserPrefs_validUserPrefs_copiesUserPrefs() {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setCouponStashFilePath(Paths.get("address/book/file/path"));
        userPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        modelManager.setUserPrefs(userPrefs);
        assertEquals(userPrefs, modelManager.getUserPrefs());

        // Modifying userPrefs should not modify modelManager's userPrefs
        UserPrefs oldUserPrefs = new UserPrefs(userPrefs);
        userPrefs.setCouponStashFilePath(Paths.get("new/address/book/file/path"));
        assertEquals(oldUserPrefs, modelManager.getUserPrefs());
    }

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setGuiSettings(null));
    }

    @Test
    public void setGuiSettings_validGuiSettings_setsGuiSettings() {
        GuiSettings guiSettings = new GuiSettings(1, 2, 3, 4);
        modelManager.setGuiSettings(guiSettings);
        assertEquals(guiSettings, modelManager.getGuiSettings());
    }

    @Test
    public void setCouponStashFilePath_nullPath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setCouponStashFilePath(null));
    }

    @Test
    public void setCouponStashFilePath_validPath_setsCouponStashFilePath() {
        Path path = Paths.get("address/book/file/path");
        modelManager.setCouponStashFilePath(path);
        assertEquals(path, modelManager.getCouponStashFilePath());
    }

    @Test
    public void hasCoupon_nullCoupon_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.hasCoupon(null));
    }

    @Test
    public void hasCoupon_couponNotInStash_returnsFalse() {
        assertFalse(modelManager.hasCoupon(ALICE));
    }

    @Test
    public void hasCoupon_couponInStash_returnsTrue() {
        modelManager.addCoupon(ALICE, "");
        assertTrue(modelManager.hasCoupon(ALICE));
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getFilteredCouponList().remove(0));
    }

    @Test
    public void getFilteredPersonList_archiveExpiredCoupons_returnsTrue() {
        CouponStash couponStash = new CouponStash(TypicalCoupons.getTypicalCouponStash());
        int numberOfActiveCoupons = couponStash.getCouponList()
                .filtered(coupon -> coupon.getExpiryDate().date.isAfter(LocalDate.now())).size();
        modelManager = new ModelManager(couponStash, new UserPrefs());

        assertTrue(numberOfActiveCoupons == modelManager.getFilteredCouponList().size());
    }

    @Test
    public void equals() {
        CouponStash couponStash = new CouponStashBuilder().withCoupon(ALICE).withCoupon(BENSON).build();
        CouponStash differentCouponStash = new CouponStash();
        UserPrefs userPrefs = new UserPrefs();

        // same values -> returns true
        modelManager = new ModelManager(couponStash, userPrefs);
        ModelManager modelManagerCopy = new ModelManager(couponStash, userPrefs);
        assertTrue(modelManager.equals(modelManagerCopy));

        // same object -> returns true
        assertTrue(modelManager.equals(modelManager));

        // null -> returns false
        assertFalse(modelManager.equals(null));

        // different types -> returns false
        assertFalse(modelManager.equals(5));

        // different couponStash -> returns false
        assertFalse(modelManager.equals(new ModelManager(differentCouponStash, userPrefs)));

        // different filteredList -> returns false
        String[] keywords = ALICE.getName().fullName.split("\\s+");
        modelManager.updateFilteredCouponList(new NameContainsKeywordsPredicate(Arrays.asList(keywords)));
        assertFalse(modelManager.equals(new ModelManager(couponStash, userPrefs)));

        // resets modelManager to initial state for upcoming tests
        modelManager.updateFilteredCouponList(PREDICATE_SHOW_ALL_ACTIVE_COUPONS);

        // different userPrefs -> returns false
        UserPrefs differentUserPrefs = new UserPrefs();
        differentUserPrefs.setCouponStashFilePath(Paths.get("differentFilePath"));
        assertFalse(modelManager.equals(new ModelManager(couponStash, differentUserPrefs)));
    }
}
