package csdev.couponstash.logic.parser;

import static csdev.couponstash.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static csdev.couponstash.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import csdev.couponstash.logic.commands.AddCommand;
import csdev.couponstash.logic.commands.ClearCommand;
import csdev.couponstash.logic.commands.Command;
import csdev.couponstash.logic.commands.DeleteCommand;
import csdev.couponstash.logic.commands.EditCommand;
import csdev.couponstash.logic.commands.ExitCommand;
import csdev.couponstash.logic.commands.ExpiringCommand;
import csdev.couponstash.logic.commands.FindCommand;
import csdev.couponstash.logic.commands.HelpCommand;
import csdev.couponstash.logic.commands.ListCommand;
import csdev.couponstash.logic.commands.RedoCommand;
import csdev.couponstash.logic.commands.RemindCommand;
import csdev.couponstash.logic.commands.SavedCommand;
import csdev.couponstash.logic.commands.UndoCommand;
import csdev.couponstash.logic.commands.UsedCommand;
import csdev.couponstash.logic.parser.exceptions.ParseException;

/**
 * Parses user input.
 */
public class CouponStashParser {

    /**
     * Used for initial separation of command word and args.
     */
    private static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");

    private final String moneySymbol;

    /**
     * Constructor for a CouponStashParser. Requires the
     * money symbol set in UserPrefs as this will be
     * used in the parsing of many commands like
     * AddCommandParser and EditCommandParser.
     * @param moneySymbol String representing the money symbol.
     */
    public CouponStashParser(String moneySymbol) {
        this.moneySymbol = moneySymbol;
    }

    /**
     * Parses user input into command for execution.
     *
     * @param userInput full user input string
     * @return the command based on the user input
     * @throws ParseException if the user input does not conform the expected format
     */
    public Command parseCommand(String userInput) throws ParseException {
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        final String commandWord = matcher.group("commandWord");
        final String arguments = matcher.group("arguments");
        switch (commandWord) {

        case AddCommand.COMMAND_WORD:
            return new AddCommandParser(this.moneySymbol).parse(arguments);

        case EditCommand.COMMAND_WORD:
            return new EditCommandParser(this.moneySymbol).parse(arguments);

        case DeleteCommand.COMMAND_WORD:
            return new DeleteCommandParser().parse(arguments);

        case ClearCommand.COMMAND_WORD:
            return new ClearCommand();

        case FindCommand.COMMAND_WORD:
            return new FindCommandParser().parse(arguments);

        case ExpiringCommand.COMMAND_WORD:
            return new ExpiringCommandParser().parse(arguments);

        case UsedCommand.COMMAND_WORD:
            return new UsedCommandParser().parse(arguments);

        case ListCommand.COMMAND_WORD:
            return new ListCommand();

        case ExitCommand.COMMAND_WORD:
            return new ExitCommand();

        case HelpCommand.COMMAND_WORD:
            return new HelpCommand();

        case SavedCommand.COMMAND_WORD:
            return new SavedCommandParser().parse(arguments);


        case UndoCommand.COMMAND_WORD:
            return new UndoCommand();

        case RedoCommand.COMMAND_WORD:
            return new RedoCommand();

        case RemindCommand.COMMAND_WORD:
            return new RemindCommandParser().parse(arguments);

        default:
            throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
        }
    }

}
