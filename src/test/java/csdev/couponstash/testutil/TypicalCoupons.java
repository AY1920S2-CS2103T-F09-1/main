package csdev.couponstash.testutil;

import static csdev.couponstash.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static csdev.couponstash.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static csdev.couponstash.logic.commands.CommandTestUtil.VALID_PHONE_AMY;
import static csdev.couponstash.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static csdev.couponstash.logic.commands.CommandTestUtil.VALID_TAG_FRIEND;
import static csdev.couponstash.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import csdev.couponstash.model.CouponStash;
import csdev.couponstash.model.coupon.Coupon;

/**
 * A utility class containing a list of {@code Coupon} objects to be used in tests.
 */
public class TypicalCoupons {

    public static final Coupon ALICE = new CouponBuilder().withName("Alice Pauline")
            .withPhone("94351253")
            .withTags("friends").build();
    public static final Coupon BENSON = new CouponBuilder().withName("Benson Meier")
            .withPhone("98765432")
            .withTags("owesMoney", "friends").build();
    public static final Coupon CARL = new CouponBuilder().withName("Carl Kurz")
            .withPhone("95352563").build();
    public static final Coupon DANIEL = new CouponBuilder().withName("Daniel Meier").withPhone("87652533")
            .withTags("friends").build();
    public static final Coupon ELLE = new CouponBuilder().withName("Elle Meyer").withPhone("9482224")
            .build();
    public static final Coupon FIONA = new CouponBuilder().withName("Fiona Kunz").withPhone("9482427")
            .build();
    public static final Coupon GEORGE = new CouponBuilder().withName("George Best").withPhone("9482442")
            .build();

    // Manually added
    public static final Coupon HOON = new CouponBuilder().withName("Hoon Meier").withPhone("8482424")
            .build();
    public static final Coupon IDA = new CouponBuilder().withName("Ida Mueller").withPhone("8482131")
            .build();

    // Manually added - Coupon's details found in {@code CommandTestUtil}
    public static final Coupon AMY = new CouponBuilder().withName(VALID_NAME_AMY).withPhone(VALID_PHONE_AMY)
            .withTags(VALID_TAG_FRIEND).build();
    public static final Coupon BOB = new CouponBuilder().withName(VALID_NAME_BOB).withPhone(VALID_PHONE_BOB)
            .withTags(VALID_TAG_HUSBAND, VALID_TAG_FRIEND)
            .build();

    public static final String KEYWORD_MATCHING_MEIER = "Meier"; // A keyword that matches MEIER

    private TypicalCoupons() {} // prevents instantiation

    /**
     * Returns an {@code CouponStash} with all the typical coupons.
     */
    public static CouponStash getTypicalCouponStash() {
        CouponStash ab = new CouponStash();
        for (Coupon coupon : getTypicalCoupons()) {
            ab.addCoupon(coupon);
        }
        return ab;
    }

    public static List<Coupon> getTypicalCoupons() {
        return new ArrayList<>(Arrays.asList(ALICE, BENSON, CARL, DANIEL, ELLE, FIONA, GEORGE));
    }
}
