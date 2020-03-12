package csdev.couponstash.logic.parser;

import static csdev.couponstash.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static csdev.couponstash.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static csdev.couponstash.logic.commands.CommandTestUtil.INVALID_PHONE_DESC;
import static csdev.couponstash.logic.commands.CommandTestUtil.INVALID_TAG_DESC;
import static csdev.couponstash.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static csdev.couponstash.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static csdev.couponstash.logic.commands.CommandTestUtil.PHONE_DESC_BOB;
import static csdev.couponstash.logic.commands.CommandTestUtil.TAG_DESC_FRIEND;
import static csdev.couponstash.logic.commands.CommandTestUtil.TAG_DESC_HUSBAND;
import static csdev.couponstash.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static csdev.couponstash.logic.commands.CommandTestUtil.VALID_PHONE_AMY;
import static csdev.couponstash.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static csdev.couponstash.logic.commands.CommandTestUtil.VALID_TAG_FRIEND;
import static csdev.couponstash.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static csdev.couponstash.logic.parser.CliSyntax.PREFIX_TAG;

import org.junit.jupiter.api.Test;

import csdev.couponstash.commons.core.index.Index;
import csdev.couponstash.logic.commands.EditCommand;
import csdev.couponstash.logic.commands.EditCommand.EditCouponDescriptor;
import csdev.couponstash.model.coupon.Name;
import csdev.couponstash.model.coupon.Phone;
import csdev.couponstash.model.tag.Tag;
import csdev.couponstash.testutil.EditCouponDescriptorBuilder;
import csdev.couponstash.testutil.TypicalIndexes;

public class EditCommandParserTest {

    private static final String TAG_EMPTY = " " + PREFIX_TAG;

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE);

    private EditCommandParser parser = new EditCommandParser();

    @Test
    public void parse_missingParts_failure() {
        // no index specified
        CommandParserTestUtil.assertParseFailure(parser, VALID_NAME_AMY, MESSAGE_INVALID_FORMAT);

        // no field specified
        CommandParserTestUtil.assertParseFailure(parser, "1", EditCommand.MESSAGE_NOT_EDITED);

        // no index and no field specified
        CommandParserTestUtil.assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidPreamble_failure() {
        // negative index
        CommandParserTestUtil.assertParseFailure(parser, "-5" + NAME_DESC_AMY, MESSAGE_INVALID_FORMAT);

        // zero index
        CommandParserTestUtil.assertParseFailure(parser, "0" + NAME_DESC_AMY, MESSAGE_INVALID_FORMAT);

        // invalid arguments being parsed as preamble
        CommandParserTestUtil.assertParseFailure(parser, "1 some random string", MESSAGE_INVALID_FORMAT);

        // invalid prefix being parsed as preamble
        CommandParserTestUtil.assertParseFailure(parser, "1 i/ string", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidValue_failure() {
        CommandParserTestUtil.assertParseFailure(parser, "1" + INVALID_NAME_DESC,
                Name.MESSAGE_CONSTRAINTS); // invalid name
        CommandParserTestUtil.assertParseFailure(parser, "1" + INVALID_PHONE_DESC,
                Phone.MESSAGE_CONSTRAINTS); // invalid phone
        CommandParserTestUtil.assertParseFailure(parser, "1" + INVALID_TAG_DESC,
                Tag.MESSAGE_CONSTRAINTS); // invalid tag

        // valid phone followed by invalid phone. The test case for invalid phone followed by valid phone
        // is tested at {@code parse_invalidValueFollowedByValidValue_success()}
        CommandParserTestUtil.assertParseFailure(parser, "1" + PHONE_DESC_BOB + INVALID_PHONE_DESC,
                Phone.MESSAGE_CONSTRAINTS);

        // while parsing {@code PREFIX_TAG} alone will reset the tags of the {@code Coupon} being edited,
        // parsing it together with a valid tag results in error
        CommandParserTestUtil.assertParseFailure(parser, "1" + TAG_DESC_FRIEND + TAG_DESC_HUSBAND + TAG_EMPTY,
                Tag.MESSAGE_CONSTRAINTS);
        CommandParserTestUtil.assertParseFailure(parser, "1" + TAG_DESC_FRIEND + TAG_EMPTY + TAG_DESC_HUSBAND,
                Tag.MESSAGE_CONSTRAINTS);
        CommandParserTestUtil.assertParseFailure(parser, "1" + TAG_EMPTY + TAG_DESC_FRIEND + TAG_DESC_HUSBAND,
                Tag.MESSAGE_CONSTRAINTS);

        // multiple invalid values, but only the first invalid value is captured
        CommandParserTestUtil.assertParseFailure(parser,
                "1" + INVALID_NAME_DESC + VALID_PHONE_AMY, Name.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_allFieldsSpecified_success() {
        Index targetIndex = TypicalIndexes.INDEX_SECOND_COUPON;
        String userInput = targetIndex.getOneBased() + PHONE_DESC_BOB + TAG_DESC_HUSBAND
                + NAME_DESC_AMY + TAG_DESC_FRIEND;

        EditCommand.EditCouponDescriptor descriptor = new EditCouponDescriptorBuilder().withName(VALID_NAME_AMY)
                .withPhone(VALID_PHONE_BOB)
                .withTags(VALID_TAG_HUSBAND, VALID_TAG_FRIEND).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        CommandParserTestUtil.assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_someFieldsSpecified_success() {
        Index targetIndex = TypicalIndexes.INDEX_FIRST_COUPON;
        String userInput = targetIndex.getOneBased() + PHONE_DESC_BOB;

        EditCouponDescriptor descriptor = new EditCouponDescriptorBuilder().withPhone(VALID_PHONE_BOB)
                .build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        CommandParserTestUtil.assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_oneFieldSpecified_success() {
        // name
        Index targetIndex = TypicalIndexes.INDEX_THIRD_COUPON;
        String userInput = targetIndex.getOneBased() + NAME_DESC_AMY;
        EditCommand.EditCouponDescriptor descriptor =
                new EditCouponDescriptorBuilder().withName(VALID_NAME_AMY).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);
        CommandParserTestUtil.assertParseSuccess(parser, userInput, expectedCommand);

        // phone
        userInput = targetIndex.getOneBased() + PHONE_DESC_AMY;
        descriptor = new EditCouponDescriptorBuilder().withPhone(VALID_PHONE_AMY).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        CommandParserTestUtil.assertParseSuccess(parser, userInput, expectedCommand);

        // tags
        userInput = targetIndex.getOneBased() + TAG_DESC_FRIEND;
        descriptor = new EditCouponDescriptorBuilder().withTags(VALID_TAG_FRIEND).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        CommandParserTestUtil.assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_multipleRepeatedFields_acceptsLast() {
        Index targetIndex = TypicalIndexes.INDEX_FIRST_COUPON;
        String userInput = targetIndex.getOneBased() + PHONE_DESC_AMY
                + TAG_DESC_FRIEND + PHONE_DESC_AMY + TAG_DESC_FRIEND
                + PHONE_DESC_BOB + TAG_DESC_HUSBAND;

        EditCouponDescriptor descriptor = new EditCouponDescriptorBuilder().withPhone(VALID_PHONE_BOB)
                .withTags(VALID_TAG_FRIEND, VALID_TAG_HUSBAND)
                .build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        CommandParserTestUtil.assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_invalidValueFollowedByValidValue_success() {
        // no other valid values specified
        Index targetIndex = TypicalIndexes.INDEX_FIRST_COUPON;
        String userInput = targetIndex.getOneBased() + INVALID_PHONE_DESC + PHONE_DESC_BOB;
        EditCouponDescriptor descriptor = new EditCouponDescriptorBuilder().withPhone(VALID_PHONE_BOB).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);
        CommandParserTestUtil.assertParseSuccess(parser, userInput, expectedCommand);

        // other valid values specified
        userInput = targetIndex.getOneBased() + INVALID_PHONE_DESC
                + PHONE_DESC_BOB;
        descriptor = new EditCouponDescriptorBuilder().withPhone(VALID_PHONE_BOB)
                .build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        CommandParserTestUtil.assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_resetTags_success() {
        Index targetIndex = TypicalIndexes.INDEX_THIRD_COUPON;
        String userInput = targetIndex.getOneBased() + TAG_EMPTY;

        EditCommand.EditCouponDescriptor descriptor = new EditCouponDescriptorBuilder().withTags().build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        CommandParserTestUtil.assertParseSuccess(parser, userInput, expectedCommand);
    }
}
