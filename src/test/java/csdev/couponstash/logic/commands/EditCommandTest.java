package csdev.couponstash.logic.commands;

import static csdev.couponstash.logic.commands.CommandTestUtil.assertCommandFailure;
import static csdev.couponstash.logic.commands.CommandTestUtil.assertCommandSuccess;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import csdev.couponstash.commons.core.Messages;
import csdev.couponstash.commons.core.index.Index;
import csdev.couponstash.logic.commands.EditCommand.EditCouponDescriptor;
import csdev.couponstash.model.CouponStash;
import csdev.couponstash.model.Model;
import csdev.couponstash.model.ModelManager;
import csdev.couponstash.model.UserPrefs;
import csdev.couponstash.model.coupon.Coupon;
import csdev.couponstash.testutil.CouponBuilder;
import csdev.couponstash.testutil.EditCouponDescriptorBuilder;
import csdev.couponstash.testutil.TypicalCoupons;
import csdev.couponstash.testutil.TypicalIndexes;

/**
 * Contains integration tests (interaction with the Model, UndoCommand and RedoCommand) and unit tests for EditCommand.
 */
public class EditCommandTest {

    private Model model = new ModelManager(TypicalCoupons.getTypicalCouponStash(), new UserPrefs());

    @Test
    public void execute_allFieldsSpecifiedUnfilteredList_success() {
        Coupon editedCoupon = new CouponBuilder().build();
        EditCouponDescriptor descriptor = new EditCouponDescriptorBuilder(editedCoupon).build();
        EditCommand editCommand = new EditCommand(TypicalIndexes.INDEX_FIRST_COUPON, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_COUPON_SUCCESS, editedCoupon);

        Model expectedModel = new ModelManager(new CouponStash(model.getCouponStash()), new UserPrefs());
        expectedModel.setCoupon(model.getFilteredCouponList().get(0), editedCoupon);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_someFieldsSpecifiedUnfilteredList_success() {
        Index indexLastCoupon = Index.fromOneBased(model.getFilteredCouponList().size());
        Coupon lastCoupon = model.getFilteredCouponList().get(indexLastCoupon.getZeroBased());

        CouponBuilder couponInList = new CouponBuilder(lastCoupon);
        Coupon editedCoupon = couponInList.withName(CommandTestUtil.VALID_NAME_BOB)
                .withPhone(CommandTestUtil.VALID_PHONE_BOB)
                .withTags(CommandTestUtil.VALID_TAG_HUSBAND).build();

        EditCouponDescriptor descriptor = new EditCouponDescriptorBuilder().withName(CommandTestUtil.VALID_NAME_BOB)
                .withPhone(CommandTestUtil.VALID_PHONE_BOB).withTags(CommandTestUtil.VALID_TAG_HUSBAND).build();
        EditCommand editCommand = new EditCommand(indexLastCoupon, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_COUPON_SUCCESS, editedCoupon);

        Model expectedModel = new ModelManager(new CouponStash(model.getCouponStash()), new UserPrefs());
        expectedModel.setCoupon(lastCoupon, editedCoupon);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_noFieldSpecifiedUnfilteredList_success() {
        EditCommand editCommand =
                new EditCommand(TypicalIndexes.INDEX_FIRST_COUPON, new EditCommand.EditCouponDescriptor());
        Coupon editedCoupon = model.getFilteredCouponList().get(TypicalIndexes.INDEX_FIRST_COUPON.getZeroBased());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_COUPON_SUCCESS, editedCoupon);

        Model expectedModel = new ModelManager(new CouponStash(model.getCouponStash()), new UserPrefs());

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filteredList_success() {
        CommandTestUtil.showCouponAtIndex(model, TypicalIndexes.INDEX_FIRST_COUPON);

        Coupon couponInFilteredList = model.getFilteredCouponList()
                .get(TypicalIndexes.INDEX_FIRST_COUPON.getZeroBased());
        Coupon editedCoupon = new CouponBuilder(couponInFilteredList).withName(CommandTestUtil.VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(TypicalIndexes.INDEX_FIRST_COUPON,
                new EditCouponDescriptorBuilder().withName(CommandTestUtil.VALID_NAME_BOB).build());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_COUPON_SUCCESS, editedCoupon);

        Model expectedModel = new ModelManager(new CouponStash(model.getCouponStash()), new UserPrefs());
        expectedModel.setCoupon(model.getFilteredCouponList().get(0), editedCoupon);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_duplicateCouponUnfilteredList_failure() {
        Coupon firstCoupon = model.getFilteredCouponList().get(TypicalIndexes.INDEX_FIRST_COUPON.getZeroBased());
        EditCommand.EditCouponDescriptor descriptor = new EditCouponDescriptorBuilder(firstCoupon).build();
        EditCommand editCommand = new EditCommand(TypicalIndexes.INDEX_SECOND_COUPON, descriptor);

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_COUPON);
    }

    @Test
    public void execute_duplicateCouponFilteredList_failure() {
        CommandTestUtil.showCouponAtIndex(model, TypicalIndexes.INDEX_FIRST_COUPON);

        // edit coupon in filtered list into a duplicate in CouponStash
        Coupon couponInList = model.getCouponStash().getCouponList()
                .get(TypicalIndexes.INDEX_SECOND_COUPON.getZeroBased());
        EditCommand editCommand = new EditCommand(TypicalIndexes.INDEX_FIRST_COUPON,
                new EditCouponDescriptorBuilder(couponInList).build());

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_COUPON);
    }

    @Test
    public void execute_invalidCouponIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredCouponList().size() + 1);
        EditCommand.EditCouponDescriptor descriptor = new EditCouponDescriptorBuilder()
                .withName(CommandTestUtil.VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(outOfBoundIndex, descriptor);

        assertCommandFailure(editCommand, model, Messages.MESSAGE_INVALID_COUPON_DISPLAYED_INDEX);
    }

    /**
     * Edit filtered list where index is larger than size of filtered list,
     * but smaller than size of CouponStash
     */
    @Test
    public void execute_invalidCouponIndexFilteredList_failure() {
        CommandTestUtil.showCouponAtIndex(model, TypicalIndexes.INDEX_FIRST_COUPON);
        Index outOfBoundIndex = TypicalIndexes.INDEX_SECOND_COUPON;
        // ensures that outOfBoundIndex is still in bounds of CouponStash list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getCouponStash().getCouponList().size());

        EditCommand editCommand = new EditCommand(outOfBoundIndex,
                new EditCouponDescriptorBuilder().withName(CommandTestUtil.VALID_NAME_BOB).build());

        assertCommandFailure(editCommand, model, Messages.MESSAGE_INVALID_COUPON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        final EditCommand standardCommand =
                new EditCommand(TypicalIndexes.INDEX_FIRST_COUPON, CommandTestUtil.DESC_AMY);

        // same values -> returns true
        EditCouponDescriptor copyDescriptor = new EditCommand.EditCouponDescriptor(CommandTestUtil.DESC_AMY);
        EditCommand commandWithSameValues = new EditCommand(TypicalIndexes.INDEX_FIRST_COUPON, copyDescriptor);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(
                new EditCommand(TypicalIndexes.INDEX_SECOND_COUPON, CommandTestUtil.DESC_AMY)));

        // different descriptor -> returns false
        assertFalse(standardCommand.equals(
                new EditCommand(TypicalIndexes.INDEX_FIRST_COUPON, CommandTestUtil.DESC_BOB)));
    }

}
