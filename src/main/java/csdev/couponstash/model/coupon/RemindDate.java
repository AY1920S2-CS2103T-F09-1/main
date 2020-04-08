package csdev.couponstash.model.coupon;

import static csdev.couponstash.commons.util.AppUtil.checkArgument;
import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import csdev.couponstash.commons.util.DateUtil;

/**
 * Represents a Coupon's remind date in the CouponStash.
 * Guarantees: immutable; is valid as declared in {@link #isValidRemindDate(String, String)}
 */
public class RemindDate {

    public static final String MESSAGE_CONSTRAINTS =
            "Remind Dates should not be a date after the Expiry date "
                    + "(in the D-M-YYYY format).";
    public static final String VALIDATION_REGEX = "\\d{1,2}-\\d{1,2}-\\d{4}";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("d-M-yyyy");

    private LocalDate date;
    private boolean remindFlag;
    private String value;

    /**
     * Constructs a {@code RemindDate}.
     *
     * @param remindDate A valid remind date.
     */
    public RemindDate(String remindDate) {
        requireNonNull(remindDate);

        // Check if date is valid
        checkArgument(DateUtil.isValidDate(remindDate), MESSAGE_CONSTRAINTS);

        value = remindDate;
        this.date = getDate();
        remindFlag = true;
    }

    public RemindDate() {
        value = "";
        date = null;
        remindFlag = false;
    }

    public RemindDate(ExpiryDate expiryDate) {
        value = expiryDate.value;
        date = getDate().minusDays(3);
        setRemindDate(date);
    }

    /**
     * Private constructor to facilitate copying of a new RemindDate
     */
    private RemindDate(LocalDate date, boolean remindFlag, String value) {
        this.date = date;
        this.remindFlag = remindFlag;
        this.value = value;
    }

    /**
     * Returns true if a given string reminddate and string expirydate are a valid remind date.
     */
    public static boolean isValidRemindDate(String remindTest, String expiryTest) {
        LocalDate remindDate = LocalDate.parse(remindTest, DATE_FORMATTER);
        LocalDate expiryDate = LocalDate.parse(expiryTest, DATE_FORMATTER);

        return remindTest.matches(VALIDATION_REGEX)
                && !(remindDate.isAfter(expiryDate));
    }

    /**
     * Set remindDate to a coupon
     */
    public void setRemindDate(LocalDate date) {
        this.date = date;
        remindFlag = true;
        value = date.format(DATE_FORMATTER);
    }

    /**
     * Returns remind date
     */
    public LocalDate getDate() {
        return LocalDate.parse(value, DATE_FORMATTER);
    }

    /**
     * Make a new copy of current RemindDate
     * @return a copy of the current RemindDate
     */
    public RemindDate copy() {
        return new RemindDate(date, remindFlag, value);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof RemindDate// instanceof handles nulls
                && value.equals(((RemindDate) other).value)); // state check
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
