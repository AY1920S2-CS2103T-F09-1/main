package csdev.couponstash.model.coupon;

import static csdev.couponstash.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import csdev.couponstash.model.coupon.savings.PureMonetarySavings;
import csdev.couponstash.model.coupon.savings.Savings;
import csdev.couponstash.model.tag.Tag;

/**
 * Represents a Coupon in the CouponStash.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Coupon {

    // Identity fields
    // ("immutable" properties of Coupon that will never
    // change unless edited)
    private final Name name;
    private final Phone phone;
    private final ExpiryDate expiryDate;
    private final StartDate startDate;
    private final Savings savingsForEachUse;
    private final Set<Tag> tags = new HashSet<>();
    private final Limit limit;

    // Data fields
    // ("mutable" properties of Coupon that will change,
    // for implementation of certain commands)
    private final Usage usage;
    private final Remind remind;
    private final PureMonetarySavings totalSavings;

    /**
     * Standard constructor for a new Coupon (when
     * a Coupon is added for the first time, with 0
     * total savings and no reminder).
     * Every field must be present and not null.
     * @param name The Name of this Coupon.
     * @param phone Phone number for this Coupon.
     * @param savingsForEachUse How much Savings saved
     *                          when this Coupon is used.
     * @param expiryDate The ExpiryDate for this Coupon.
     * @param startDate The StartDate for this Coupon.
     * @param usage The Usage for this Coupon.
     * @param limit The usage Limit for this Coupon.
     * @param tags The List of tags for this Coupon.
     */
    public Coupon(Name name, Phone phone, Savings savingsForEachUse, ExpiryDate expiryDate, StartDate startDate,
                  Usage usage, Limit limit, Set<Tag> tags) {

        this(name, phone, savingsForEachUse, expiryDate, startDate, usage,
                limit, tags, new PureMonetarySavings(), new Remind());
    }

    /**
     * Constructor for a Coupon, given every required field.
     * Each field should not be null, otherwise a
     * NullPointerException will be thrown!
     * @param name The Name of this Coupon.
     * @param phone Phone number for this Coupon.
     * @param savingsForEachUse How much Savings saved
     *                          when this Coupon is used.
     * @param expiryDate The ExpiryDate for this Coupon.
     * @param startDate The StartDate for this Coupon.
     * @param usage The Usage for this Coupon.
     * @param limit The usage Limit for this Coupon.
     * @param tags The List of tags for this Coupon.
     * @param totalSavings PureMonetarySavings representing
     *                     the total savings accumulated.
     * @param remind Remind representing a reminder for
     *               this Coupon.
     */
    public Coupon(
            Name name,
            Phone phone,
            Savings savingsForEachUse,
            ExpiryDate expiryDate,
            StartDate startDate,
            Usage usage,
            Limit limit,
            Set<Tag> tags,
            PureMonetarySavings totalSavings,
            Remind remind) {

        requireAllNonNull(name, phone, savingsForEachUse, expiryDate, usage, limit, tags, totalSavings, remind);
        this.name = name;
        this.phone = phone;
        this.savingsForEachUse = savingsForEachUse;
        this.totalSavings = totalSavings;
        this.expiryDate = expiryDate;
        this.startDate = startDate;
        this.remind = remind;
        this.usage = usage;
        this.limit = limit;
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

    /**
     * Gets the Savings per use associated with this Coupon.
     * @return Savings representing either the monetary
     *     amount saved, percentage amount saved, or
     *     unquantifiable items (Saveables) that will
     *     be earned for every use of this Coupon.
     */
    public Savings getSavingsForEachUse() {
        return savingsForEachUse;
    }

    /**
     * Gets the total Savings stored with this Coupon as
     * a PureMonetarySavings. This total Savings will be
     * increased whenever the Coupon is marked as used.
     * @return PureMonetarySavings representing total
     *     amount of money saved from using this
     *     Coupon, as well as unquantifiable
     *     items (Saveables) earned.
     */
    public PureMonetarySavings getTotalSavings() {
        return totalSavings;
    }

    public ExpiryDate getExpiryDate() {
        return expiryDate;
    }

    public StartDate getStartDate() {
        return startDate;
    }

    public Usage getUsage() {
        return usage;
    }

    public Limit getLimit() {
        return limit;
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Adds a certain PureMonetarySavings to the totalSavings
     * field of this Coupon, that keeps track of how much has
     * been saved by using this Coupon (for SavedCommand).
     * @param pms The PureMonetarySavings to be added.
     * @return A new Coupon with total savings modified.
     */
    public Coupon addToTotalSavings(PureMonetarySavings pms) {
        return new Coupon(this.name, this.phone, this.savingsForEachUse,
                this.expiryDate, this.startDate, this.usage, this.limit,
                this.tags, this.totalSavings.add(pms), this.remind);
    }

    /**
     * Returns true if both coupons have the same name, and all
     * of the fields of phone, savings for each use, expiry date or
     * start date is the same.
     * This defines a weaker notion of equality between two coupons.
     */
    public boolean isSameCoupon(Coupon otherCoupon) {
        if (otherCoupon == this) {
            return true;
        }

        return otherCoupon != null
                && otherCoupon.getName().equals(getName())
                && otherCoupon.getPhone().equals(getPhone())
                && otherCoupon.getExpiryDate().equals(getExpiryDate())
                && otherCoupon.getSavingsForEachUse().equals(getSavingsForEachUse());
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
                && otherCoupon.getSavingsForEachUse().equals(getSavingsForEachUse())
                && otherCoupon.getExpiryDate().equals(getExpiryDate())
                && otherCoupon.getStartDate().equals(getStartDate())
                && otherCoupon.getUsage().equals(getUsage())
                && otherCoupon.getLimit().equals(getLimit())
                && otherCoupon.getTags().equals(getTags())
                && otherCoupon.getTotalSavings().equals(getTotalSavings());
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, savingsForEachUse, expiryDate, startDate, usage, limit, tags, totalSavings);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getName())
                .append(" Phone: ")
                .append(getPhone())
                .append(" Savings: ")
                .append(getSavingsForEachUse())
                .append(" Expiry Date: ")
                .append(getExpiryDate())
                .append(" Start Date: ")
                .append(getStartDate())
                .append(" Usage: ")
                .append(getUsage())
                .append(" Limit: ")
                .append(getLimit())
                .append(" Tags: ");
        getTags().forEach(builder::append);
        return builder.toString();
    }

}
