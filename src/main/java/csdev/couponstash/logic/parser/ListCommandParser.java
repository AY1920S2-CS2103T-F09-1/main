package csdev.couponstash.logic.parser;

import static csdev.couponstash.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static csdev.couponstash.logic.parser.CliSyntax.PREFIX_ARCHIVE;
import static csdev.couponstash.logic.parser.CliSyntax.PREFIX_USAGE;
import static java.util.Objects.requireNonNull;

import csdev.couponstash.logic.commands.ListCommand;
import csdev.couponstash.logic.parser.exceptions.ParseException;

/**
 * Parses the given {@code String} of arguments in the context of the SortCommand
 * and returns an SortCommand object for execution.
 *
 * @throws ParseException if the user input does not conform the expected format
 */
public class ListCommandParser implements Parser<ListCommand> {
    @Override
    public ListCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultiMap = ArgumentTokenizer.tokenize(args, PREFIX_ARCHIVE, PREFIX_USAGE);

        if (args.trim().isEmpty()) {
            //Has no argument
            return new ListCommand();
        } else if (argMultiMap.getValue(PREFIX_ARCHIVE).isPresent()
                && !argMultiMap.getValue(PREFIX_USAGE).isPresent()) {
            // Only has PREFIX_ARCHIVE
            return new ListCommand(PREFIX_ARCHIVE);
        } else if (!argMultiMap.getValue(PREFIX_ARCHIVE).isPresent()
                && argMultiMap.getValue(PREFIX_USAGE).isPresent()) {
            System.out.println(!argMultiMap.getValue(PREFIX_ARCHIVE).isPresent());
            System.out.println(argMultiMap.getValue(PREFIX_USAGE).isPresent());
            // Only has PREFIX_USAGE
            return new ListCommand(PREFIX_USAGE);
        } else {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        }
    }
}
