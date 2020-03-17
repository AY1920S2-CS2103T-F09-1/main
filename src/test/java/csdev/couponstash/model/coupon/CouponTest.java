package csdev.couponstash.model.coupon;

import static csdev.couponstash.testutil.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import csdev.couponstash.logic.commands.CommandTestUtil;
import csdev.couponstash.testutil.CouponBuilder;
import csdev.couponstash.testutil.TypicalCoupons;

public class CouponTest {

    @Test
    public void asObservableList_modifyList_throwsUnsupportedOperationException() {
        Coupon coupon = new CouponBuilder().build();
        assertThrows(UnsupportedOperationException.class, () -> coupon.getTags().remove(0));
    }

    @Test
    public void isSameCoupon() {
        // same object -> returns true
        assertTrue(TypicalCoupons.ALICE.isSameCoupon(TypicalCoupons.ALICE));

        // null -> returns false
        assertFalse(TypicalCoupons.ALICE.isSameCoupon(null));

        // different name -> returns false
        Coupon editedAlice = new CouponBuilder(TypicalCoupons.ALICE).withName(CommandTestUtil.VALID_NAME_BOB).build();
        assertFalse(TypicalCoupons.ALICE.isSameCoupon(editedAlice));

        //different phone -> returns false
        editedAlice = new CouponBuilder(TypicalCoupons.ALICE).withPhone(CommandTestUtil.VALID_PHONE_BOB).build();
        assertFalse(TypicalCoupons.ALICE.isSameCoupon(editedAlice));

        //different expiry date -> returns false
        editedAlice = new CouponBuilder(TypicalCoupons.ALICE).withExpiryDate(CommandTestUtil.VALID_EXPIRY_DATE_BOB)
                .build();
        assertFalse(TypicalCoupons.ALICE.isSameCoupon(editedAlice));

        //different savings -> returns false
        editedAlice = new CouponBuilder(TypicalCoupons.ALICE).withSavings(CommandTestUtil.VALID_SAVINGS_BOB).build();
        assertFalse(TypicalCoupons.ALICE.isSameCoupon(editedAlice));

        //different tags -> returns true
        editedAlice = new CouponBuilder(TypicalCoupons.ALICE)
                .withTags(CommandTestUtil.VALID_TAG_HUSBAND).build();
        assertTrue(TypicalCoupons.ALICE.isSameCoupon(editedAlice));
    }

    @Test
    public void equals() {
        // same values -> returns true
        Coupon aliceCopy = new CouponBuilder(TypicalCoupons.ALICE).build();
        assertTrue(TypicalCoupons.ALICE.equals(aliceCopy));

        // same object -> returns true
        assertTrue(TypicalCoupons.ALICE.equals(TypicalCoupons.ALICE));

        // null -> returns false
        assertFalse(TypicalCoupons.ALICE.equals(null));

        // different type -> returns false
        assertFalse(TypicalCoupons.ALICE.equals(5));

        // different coupon -> returns false
        assertFalse(TypicalCoupons.ALICE.equals(TypicalCoupons.BOB));

        // different name -> returns false
        Coupon editedAlice = new CouponBuilder(TypicalCoupons.ALICE).withName(CommandTestUtil.VALID_NAME_BOB).build();
        assertFalse(TypicalCoupons.ALICE.equals(editedAlice));

        // different phone -> returns false
        editedAlice = new CouponBuilder(TypicalCoupons.ALICE).withPhone(CommandTestUtil.VALID_PHONE_BOB).build();
        assertFalse(TypicalCoupons.ALICE.equals(editedAlice));

        // different expiry date -> returns false
        editedAlice = new CouponBuilder(TypicalCoupons.ALICE).withExpiryDate(CommandTestUtil.VALID_EXPIRY_DATE_BOB)
                .build();
        assertFalse(TypicalCoupons.ALICE.equals(editedAlice));

        // different start date -> returns false
        editedAlice = new CouponBuilder(TypicalCoupons.ALICE).withStartDate(CommandTestUtil.VALID_START_DATE_BOB)
                .build();
        assertFalse(TypicalCoupons.ALICE.equals(editedAlice));

        // different tags -> returns false
        editedAlice = new CouponBuilder(TypicalCoupons.ALICE).withTags(CommandTestUtil.VALID_TAG_HUSBAND).build();
        assertFalse(TypicalCoupons.ALICE.equals(editedAlice));
    }
}
