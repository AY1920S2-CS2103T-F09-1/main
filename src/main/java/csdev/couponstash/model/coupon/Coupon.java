package csdev.couponstash.model.coupon;

import static csdev.couponstash.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import csdev.couponstash.model.coupon.savings.Savings;
import csdev.couponstash.model.tag.Tag;

/**
 * Represents a Coupon in the CouponStash.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Coupon {

    // Identity fields
    private final Name name;
    private final Phone phone;
    private final ExpiryDate expiryDate;
    private final Remind remind;

    // Savings field
    private final Savings savings;

    // Data fields
    private final Usage usage;
    private final Set<Tag> tags = new HashSet<>();



    /**
     * Every field must be present and not null.
     */
    public Coupon(Name name, Phone phone, Savings savings, ExpiryDate expiryDate, Usage usage, Set<Tag> tags) {
        requireAllNonNull(name, phone, savings, expiryDate, usage, tags);
        this.name = name;
        this.phone = phone;
        this.savings = savings;
        this.expiryDate = expiryDate;
        this.remind = new Remind();
        this.usage = usage;
        this.tags.addAll(tags);
    }

    public Remind getRemind() { 
        return remind; 
    }
    
    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
    }

    public Usage getUsage() {
        return usage;
    }

    /**
     * Gets the Savings associated with this Coupon.
     * @return Savings representing either the monetary
     *     amount saved, percentage amount saved, or
     *     unquantifiable items (Saveables).
     */
    public Savings getSavings() {
        return savings;
    }

    public ExpiryDate getExpiryDate() {
        return expiryDate;
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns true if both coupons of the same name have at least one other identity field that is the same.
     * This defines a weaker notion of equality between two coupons.
     */
    public boolean isSameCoupon(Coupon otherCoupon) {
        if (otherCoupon == this) {
            return true;
        }

        return otherCoupon != null
                && otherCoupon.getName().equals(getName())
                && (otherCoupon.getPhone().equals(getPhone())
                        || otherCoupon.getSavings().equals(getSavings())
                        || otherCoupon.getExpiryDate().equals(getExpiryDate()));
    }

    /**
     * Returns true if both coupons have the same identity and data fields.
     * This defines a stronger notion of equality between two coupons.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Coupon)) {
            return false;
        }

        Coupon otherCoupon = (Coupon) other;

        // Loop through the tags to check for equality
        if (getTags().size() != otherCoupon.getTags().size()) {
            return false;
        } else {
            for (Tag tag : getTags()) {
                if (!otherCoupon.getTags().contains(tag)) {
                    return false;
                }
            }
        }

        return otherCoupon.getName().equals(getName())
                && otherCoupon.getPhone().equals(getPhone())
                && otherCoupon.getSavings().equals(getSavings())
                && otherCoupon.getExpiryDate().equals(getExpiryDate())
                && otherCoupon.getUsage().equals(getUsage());
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, savings, expiryDate, usage, tags);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getName())
                .append(" Phone: ")
                .append(getPhone())
                .append(" Savings: ")
                .append(getSavings())
                .append(" Expiry Date: ")
                .append(getExpiryDate())
                .append(" Usage: ")
                .append(getUsage())
                .append(" Tags: ");
        getTags().forEach(builder::append);
        return builder.toString();
    }

}
