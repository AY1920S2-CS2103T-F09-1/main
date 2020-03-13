package csdev.couponstash.model.coupon;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static csdev.couponstash.commons.util.AppUtil.checkArgument;
import static java.util.Objects.requireNonNull;

/**
 * Represents a Coupon's expiry date in the CouponStash.
 * Guarantees: immutable; is valid as declared in {@link #isValidDate(String)}
 */
public class Remind {

    public static final String MESSAGE_CONSTRAINTS =
            "Remind date should be  in the D-M-YYYY format.";
    public static final String VALIDATION_REGEX = "\\d{1,2}-\\d{1,2}-\\d{4}";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("d-M-yyyy");
    private LocalDate date;
    private String value;
    private boolean remindFlag;

    /**
     * Constructs a {@code Remind}.
     */
    public Remind() {
        value = "";
        date = null;
        remindFlag = false;
    }

    public void setRemind(LocalDate date) {
        String temp = date.format(DateTimeFormatter.ofPattern("dd-M-yyyy"));
        if (isValidDate(temp)) {
            value = temp;
            this.date = getDate();
            remindFlag = true;
        }
    }
    public boolean getRemindFlag() {
        return this.remindFlag;
    }
    /**
     * Returns true if a given string is a valid date.
     */
    public static boolean isValidDate(String test) {
        return test.matches(VALIDATION_REGEX);
    }
    /**
     * Change String date to  Date date in d-M-yyyy format
     * @return date
     */
    public LocalDate getDate() {
        return LocalDate.parse(value, DATE_FORMATTER);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Remind// instanceof handles nulls
                && value.equals(((Remind) other).value)); // state check
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
