package csdev.couponstash.logic.commands;

import static csdev.couponstash.logic.commands.CommandTestUtil.assertCommandSuccess;
import static csdev.couponstash.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import csdev.couponstash.logic.commands.exceptions.CommandException;
import csdev.couponstash.model.CouponStash;
import csdev.couponstash.model.Model;
import csdev.couponstash.model.ModelManager;
import csdev.couponstash.model.UserPrefs;
import csdev.couponstash.model.coupon.Coupon;

import csdev.couponstash.testutil.CouponBuilder;
import csdev.couponstash.testutil.EditCouponDescriptorBuilder;
import csdev.couponstash.testutil.TypicalCoupons;
import csdev.couponstash.testutil.TypicalIndexes;

class UndoCommandTest {

    @Test
    void execute_undoAdd_success() {
        Model model = new ModelManager(
                TypicalCoupons.getTypicalCouponStash(),
                new UserPrefs()
        );
        Coupon validCoupon = new CouponBuilder().build();
        model.addCoupon(validCoupon);

        ModelManager expectedModel = new ModelManager(
                TypicalCoupons.getTypicalCouponStash(),
                new UserPrefs()
        );

        String expectedMessage = UndoCommand.MESSAGE_SUCCESS;

        assertCommandSuccess(new UndoCommand(), model, expectedMessage, expectedModel);
    }

    @Test
    void execute_undoDelete_success() {
        Model model = new ModelManager(
                TypicalCoupons.getTypicalCouponStash(),
                new UserPrefs()
        );
        Coupon couponToDelete = model
                .getFilteredCouponList()
                .get(TypicalIndexes.INDEX_FIRST_COUPON.getZeroBased());

        model.deleteCoupon(couponToDelete);

        ModelManager expectedModel = new ModelManager(
                TypicalCoupons.getTypicalCouponStash(),
                new UserPrefs()
        );

        String expectedMessage = UndoCommand.MESSAGE_SUCCESS;

        assertCommandSuccess(new UndoCommand(), model, expectedMessage, expectedModel);
    }

    @Test
    void execute_undoEdit_success() {
        Model model = new ModelManager(
                TypicalCoupons.getTypicalCouponStash(),
                new UserPrefs()
        );

        Coupon editedCoupon = new CouponBuilder().build();
        EditCommand.EditCouponDescriptor descriptor =
                new EditCouponDescriptorBuilder(editedCoupon).build();

        model.setCoupon(model.getFilteredCouponList().get(0), editedCoupon);

        ModelManager expectedModel = new ModelManager(
                TypicalCoupons.getTypicalCouponStash(),
                new UserPrefs()
        );

        String expectedMessage = UndoCommand.MESSAGE_SUCCESS;

        assertCommandSuccess(new UndoCommand(), model, expectedMessage, expectedModel);
    }

    @Test
    void execute_undoClear_success() {
        Model model = new ModelManager(
                TypicalCoupons.getTypicalCouponStash(),
                new UserPrefs()
        );

        model.setCouponStash(new CouponStash());

        ModelManager expectedModel = new ModelManager(
                TypicalCoupons.getTypicalCouponStash(),
                new UserPrefs()
        );

        String expectedMessage = UndoCommand.MESSAGE_SUCCESS;

        assertCommandSuccess(new UndoCommand(), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_noStateToUndoTo_throwsCommandException() {
        Model model = new ModelManager(
                TypicalCoupons.getTypicalCouponStash(),
                new UserPrefs()
        );

        UndoCommand undoCommand = new UndoCommand();

        assertThrows(
                CommandException.class, UndoCommand.MESSAGE_NO_STATE_TO_UNDO_TO, () -> undoCommand.execute(model));
    }
}
