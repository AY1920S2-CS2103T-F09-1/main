package csdev.couponstash.model.coupon;

import static csdev.couponstash.testutil.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class LimitTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Limit(null));
    }

    @Test
    public void constructor_invalidLimit_throwsIllegalArgumentException() {
        String invalidLimit = "asdf";
        assertThrows(IllegalArgumentException.class, () -> new Limit(invalidLimit));
    }

    @Test
    public void isValidLimit() {
        // null limit
        assertThrows(NullPointerException.class, () -> Limit.isValidLimit(null));

        // invalid limit
        assertFalse(Limit.isValidLimit(" ")); // spaces only
        assertFalse(Limit.isValidLimit("^")); // only non-alphanumeric characters
        assertFalse(Limit.isValidLimit("peter*")); // contains non-alphanumeric characters

        // valid limit
        assertTrue(Limit.isValidLimit("12345")); // positive numbers only
        assertTrue(Limit.isValidLimit("-12345")); // negative numbers only
        assertTrue(Limit.isValidLimit("")); // empty string
        assertTrue(Limit.isValidLimit("Infinity")); // Infinity
    }
}
