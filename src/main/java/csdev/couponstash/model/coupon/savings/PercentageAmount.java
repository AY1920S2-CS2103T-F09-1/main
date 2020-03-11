package csdev.couponstash.model.coupon.savings;

import csdev.couponstash.model.coupon.Email;

import static csdev.couponstash.commons.util.AppUtil.checkArgument;

/**
 * Represents a percentage amount that
 * can be saved by a coupon.
 * Immutable.
 */
public class PercentageAmount implements Comparable<PercentageAmount> {
    public static final String MESSAGE_CONSTRAINTS =
            "Percentage should be between 0% and 100%";

    private final double percentage;

    public PercentageAmount(double percent) {
        checkArgument(PercentageAmount.isValidPercentage(percent),
                PercentageAmount.MESSAGE_CONSTRAINTS);
        this.percentage = percent;
    }

    /**
     * Gets the value of this PercentageAmount
     * as a double between 0 and 100.
     * @return Double representing PercentageAmount.
     */
    public double getValue() {
        return this.percentage;
    }

    /**
     * Checks if this double is suitable for use
     * in the PercentageAmount (should be between
     * 0 and 100 inclusive).
     * @param percentage The double to be checked.
     * @return True, if the double is between 0
     *     and 100 inclusive. False, if not.
     */
    public static boolean isValidPercentage(double percentage) {
        return percentage >= 0 && percentage <= 100;
    }

    @Override
    public int compareTo(PercentageAmount p) {
        return (int) Math.signum(this.percentage - p.percentage);
    }

    @Override
    public boolean equals(Object o) {
        return o == this
                || (o instanceof PercentageAmount
                && this.percentage == ((PercentageAmount) o).percentage);
    }

    @Override
    public int hashCode() {
        return Double.hashCode(this.percentage);
    }

    @Override
    public String toString() {
        return String.format("%.2f", this.percentage);
    }
}
