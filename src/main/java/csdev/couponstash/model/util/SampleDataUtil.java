package csdev.couponstash.model.util;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import csdev.couponstash.model.CouponStash;
import csdev.couponstash.model.ReadOnlyCouponStash;
import csdev.couponstash.model.coupon.Coupon;
import csdev.couponstash.model.coupon.Email;
import csdev.couponstash.model.coupon.Name;
import csdev.couponstash.model.coupon.Phone;
import csdev.couponstash.model.tag.Tag;

/**
 * Contains utility methods for populating {@code CouponStash} with sample data.
 */
public class SampleDataUtil {
    public static Coupon[] getSampleCoupons() {
        return new Coupon[] {
            new Coupon(new Name("Alex Yeoh"), new Phone("87438807"), new Email("alexyeoh@example.com"),
                getTagSet("friends")),
            new Coupon(new Name("Bernice Yu"), new Phone("99272758"), new Email("berniceyu@example.com"),
                getTagSet("colleagues", "friends")),
            new Coupon(new Name("Charlotte Oliveiro"), new Phone("93210283"), new Email("charlotte@example.com"),
                getTagSet("neighbours")),
            new Coupon(new Name("David Li"), new Phone("91031282"), new Email("lidavid@example.com"),
                getTagSet("family")),
            new Coupon(new Name("Irfan Ibrahim"), new Phone("92492021"), new Email("irfan@example.com"),
                getTagSet("classmates")),
            new Coupon(new Name("Roy Balakrishnan"), new Phone("92624417"), new Email("royb@example.com"),
                getTagSet("colleagues"))
        };
    }

    public static ReadOnlyCouponStash getSampleCouponStash() {
        CouponStash sampleAb = new CouponStash();
        for (Coupon sampleCoupon : getSampleCoupons()) {
            sampleAb.addCoupon(sampleCoupon);
        }
        return sampleAb;
    }

    /**
     * Returns a tag set containing the list of strings given.
     */
    public static Set<Tag> getTagSet(String... strings) {
        return Arrays.stream(strings)
                .map(Tag::new)
                .collect(Collectors.toSet());
    }

}
