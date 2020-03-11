package csdev.couponstash.logic.commands;

import static csdev.couponstash.testutil.Assert.assertThrows;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import csdev.couponstash.commons.core.GuiSettings;
import csdev.couponstash.logic.commands.exceptions.CommandException;
import csdev.couponstash.model.CouponStash;
import csdev.couponstash.model.Model;
import csdev.couponstash.model.ReadOnlyCouponStash;
import csdev.couponstash.model.ReadOnlyUserPrefs;
import csdev.couponstash.model.coupon.Coupon;
import csdev.couponstash.testutil.CouponBuilder;

import javafx.collections.ObservableList;

public class AddCommandTest {

    @Test
    public void constructor_nullCoupon_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddCommand(null));
    }

    @Test
    public void execute_couponAcceptedByModel_addSuccessful() throws Exception {
        ModelStubAcceptingCouponAdded modelStub = new ModelStubAcceptingCouponAdded();
        Coupon validCoupon = new CouponBuilder().build();

        CommandResult commandResult = new AddCommand(validCoupon).execute(modelStub);

        assertEquals(String.format(AddCommand.MESSAGE_SUCCESS, validCoupon), commandResult.getFeedbackToUser());
        assertEquals(Arrays.asList(validCoupon), modelStub.couponsAdded);
    }

    @Test
    public void execute_duplicateCoupon_throwsCommandException() {
        Coupon validCoupon = new CouponBuilder().build();
        AddCommand addCommand = new AddCommand(validCoupon);
        ModelStub modelStub = new ModelStubWithCoupon(validCoupon);

        assertThrows(CommandException.class, AddCommand.MESSAGE_DUPLICATE_COUPON, () -> addCommand.execute(modelStub));
    }

    @Test
    public void equals() {
        Coupon alice = new CouponBuilder().withName("Alice").build();
        Coupon bob = new CouponBuilder().withName("Bob").build();
        AddCommand addAliceCommand = new AddCommand(alice);
        AddCommand addBobCommand = new AddCommand(bob);

        // same object -> returns true
        assertTrue(addAliceCommand.equals(addAliceCommand));

        // same values -> returns true
        AddCommand addAliceCommandCopy = new AddCommand(alice);
        assertTrue(addAliceCommand.equals(addAliceCommandCopy));

        // different types -> returns false
        assertFalse(addAliceCommand.equals(1));

        // null -> returns false
        assertFalse(addAliceCommand.equals(null));

        // different coupon -> returns false
        assertFalse(addAliceCommand.equals(addBobCommand));
    }

    /**
     * A default model stub that have all of the methods failing.
     */
    private class ModelStub implements Model {
        @Override
        public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyUserPrefs getUserPrefs() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public GuiSettings getGuiSettings() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Path getCouponStashFilePath() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setCouponStashFilePath(Path couponStashFilePath) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addCoupon(Coupon coupon) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setCouponStash(ReadOnlyCouponStash newData) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyCouponStash getCouponStash() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasCoupon(Coupon coupon) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deleteCoupon(Coupon target) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setCoupon(Coupon target, Coupon editedCoupon) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Coupon> getFilteredCouponList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredCouponList(Predicate<Coupon> predicate) {
            throw new AssertionError("This method should not be called.");
        }
    }

    /**
     * A Model stub that contains a single coupon.
     */
    private class ModelStubWithCoupon extends ModelStub {
        private final Coupon coupon;

        ModelStubWithCoupon(Coupon coupon) {
            requireNonNull(coupon);
            this.coupon = coupon;
        }

        @Override
        public boolean hasCoupon(Coupon coupon) {
            requireNonNull(coupon);
            return this.coupon.isSameCoupon(coupon);
        }
    }

    /**
     * A Model stub that always accept the coupon being added.
     */
    private class ModelStubAcceptingCouponAdded extends ModelStub {
        final ArrayList<Coupon> couponsAdded = new ArrayList<>();

        @Override
        public boolean hasCoupon(Coupon coupon) {
            requireNonNull(coupon);
            return couponsAdded.stream().anyMatch(coupon::isSameCoupon);
        }

        @Override
        public void addCoupon(Coupon coupon) {
            requireNonNull(coupon);
            couponsAdded.add(coupon);
        }

        @Override
        public ReadOnlyCouponStash getCouponStash() {
            return new CouponStash();
        }
    }

}
