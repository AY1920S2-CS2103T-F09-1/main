package csdev.couponstash.testutil;

import java.util.HashSet;
import java.util.Set;

import csdev.couponstash.model.coupon.Coupon;
import csdev.couponstash.model.coupon.Name;
import csdev.couponstash.model.coupon.Phone;
import csdev.couponstash.model.coupon.savings.MonetaryAmount;
import csdev.couponstash.model.coupon.savings.Savings;
import csdev.couponstash.model.tag.Tag;
import csdev.couponstash.model.util.SampleDataUtil;

/**
 * A utility class to help with building Coupon objects.
 */
public class CouponBuilder {

    public static final String DEFAULT_NAME = "Alice Pauline";
    public static final String DEFAULT_PHONE = "85355255";
    public static final Savings DEFAULT_SAVINGS = new Savings(new MonetaryAmount(32.5));

    private Name name;
    private Phone phone;
    private Savings savings;
    private Set<Tag> tags;

    public CouponBuilder() {
        name = new Name(DEFAULT_NAME);
        phone = new Phone(DEFAULT_PHONE);
        savings = new Savings(DEFAULT_SAVINGS);
        tags = new HashSet<>();
    }

    /**
     * Initializes the CouponBuilder with the data of {@code couponToCopy}.
     */
    public CouponBuilder(Coupon couponToCopy) {
        name = couponToCopy.getName();
        phone = couponToCopy.getPhone();
        savings = new Savings(couponToCopy.getSavings());
        tags = new HashSet<>(couponToCopy.getTags());
    }

    /**
     * Sets the {@code Name} of the {@code Coupon} that we are building.
     */
    public CouponBuilder withName(String name) {
        this.name = new Name(name);
        return this;
    }

    /**
     * Parses the {@code tags} into a {@code Set<Tag>} and set it to the {@code Coupon} that we are building.
     */
    public CouponBuilder withTags(String ... tags) {
        this.tags = SampleDataUtil.getTagSet(tags);
        return this;
    }

    /**
     * Sets the {@code Phone} of the {@code Coupon} that we are building.
     */
    public CouponBuilder withPhone(String phone) {
        this.phone = new Phone(phone);
        return this;
    }

    /**
     * Sets the {@code Savings} of the {@code Coupon} that we are building.
     * @param sv The Savings to set.
     * @return This CouponBuilder (mutated).
     */
    public CouponBuilder withSavings(Savings sv) {
        this.savings = sv;
        return this;
    }

    public Coupon build() {
        return new Coupon(name, phone, savings, tags);
    }

}
