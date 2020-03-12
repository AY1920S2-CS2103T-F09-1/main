package csdev.couponstash.logic.parser;

import static csdev.couponstash.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static csdev.couponstash.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static csdev.couponstash.logic.commands.CommandTestUtil.INVALID_PHONE_DESC;
import static csdev.couponstash.logic.commands.CommandTestUtil.INVALID_TAG_DESC;
import static csdev.couponstash.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static csdev.couponstash.logic.commands.CommandTestUtil.NAME_DESC_BOB;
import static csdev.couponstash.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static csdev.couponstash.logic.commands.CommandTestUtil.PHONE_DESC_BOB;
import static csdev.couponstash.logic.commands.CommandTestUtil.PREAMBLE_NON_EMPTY;
import static csdev.couponstash.logic.commands.CommandTestUtil.PREAMBLE_WHITESPACE;
import static csdev.couponstash.logic.commands.CommandTestUtil.TAG_DESC_FRIEND;
import static csdev.couponstash.logic.commands.CommandTestUtil.TAG_DESC_HUSBAND;
import static csdev.couponstash.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static csdev.couponstash.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static csdev.couponstash.logic.commands.CommandTestUtil.VALID_TAG_FRIEND;
import static csdev.couponstash.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static csdev.couponstash.logic.parser.CommandParserTestUtil.assertParseFailure;
import static csdev.couponstash.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import csdev.couponstash.logic.commands.AddCommand;
import csdev.couponstash.model.coupon.Coupon;
import csdev.couponstash.model.coupon.Name;
import csdev.couponstash.model.coupon.Phone;
import csdev.couponstash.model.tag.Tag;
import csdev.couponstash.testutil.CouponBuilder;
import csdev.couponstash.testutil.TypicalCoupons;

public class AddCommandParserTest {
    private AddCommandParser parser = new AddCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        Coupon expectedCoupon = new CouponBuilder(TypicalCoupons.BOB).withTags(VALID_TAG_FRIEND).build();

        // whitespace only preamble
        assertParseSuccess(parser, PREAMBLE_WHITESPACE + NAME_DESC_BOB + PHONE_DESC_BOB
                + TAG_DESC_FRIEND, new AddCommand(expectedCoupon));

        // multiple names - last name accepted
        assertParseSuccess(parser, NAME_DESC_AMY + NAME_DESC_BOB + PHONE_DESC_BOB
                + TAG_DESC_FRIEND, new AddCommand(expectedCoupon));

        // multiple phones - last phone accepted
        assertParseSuccess(parser, NAME_DESC_BOB + PHONE_DESC_AMY + PHONE_DESC_BOB
                + TAG_DESC_FRIEND, new AddCommand(expectedCoupon));

        // multiple tags - all accepted
        Coupon expectedCouponMultipleTags = new CouponBuilder(TypicalCoupons.BOB)
                .withTags(VALID_TAG_FRIEND, VALID_TAG_HUSBAND)
                .build();
        assertParseSuccess(parser, NAME_DESC_BOB + PHONE_DESC_BOB
                + TAG_DESC_HUSBAND + TAG_DESC_FRIEND, new AddCommand(expectedCouponMultipleTags));
    }

    @Test
    public void parse_optionalFieldsMissing_success() {
        // zero tags
        Coupon expectedCoupon = new CouponBuilder(TypicalCoupons.AMY).withTags().build();
        assertParseSuccess(parser, NAME_DESC_AMY + PHONE_DESC_AMY,
                new AddCommand(expectedCoupon));
    }

    @Test
    public void parse_compulsoryFieldMissing_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE);

        // missing name prefix
        assertParseFailure(parser, VALID_NAME_BOB + PHONE_DESC_BOB,
                expectedMessage);

        // missing phone prefix
        assertParseFailure(parser, NAME_DESC_BOB + VALID_PHONE_BOB,
                expectedMessage);

        // all prefixes missing
        assertParseFailure(parser, VALID_NAME_BOB + VALID_PHONE_BOB,
                expectedMessage);
    }

    @Test
    public void parse_invalidValue_failure() {
        // invalid name
        assertParseFailure(parser, INVALID_NAME_DESC + PHONE_DESC_BOB
                + TAG_DESC_HUSBAND + TAG_DESC_FRIEND, Name.MESSAGE_CONSTRAINTS);

        // invalid phone
        assertParseFailure(parser, NAME_DESC_BOB + INVALID_PHONE_DESC
                + TAG_DESC_HUSBAND + TAG_DESC_FRIEND, Phone.MESSAGE_CONSTRAINTS);

        // invalid tag
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB
                + INVALID_TAG_DESC + VALID_TAG_FRIEND, Tag.MESSAGE_CONSTRAINTS);

        // two invalid values, only first invalid value reported
        assertParseFailure(parser, INVALID_NAME_DESC + PHONE_DESC_BOB,
                Name.MESSAGE_CONSTRAINTS);

        // non-empty preamble
        assertParseFailure(parser, PREAMBLE_NON_EMPTY + NAME_DESC_BOB + PHONE_DESC_BOB
                + TAG_DESC_HUSBAND + TAG_DESC_FRIEND,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
    }
}
