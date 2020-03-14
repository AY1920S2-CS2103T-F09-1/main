package csdev.couponstash.logic.commands;

import static java.util.Objects.requireNonNull;

import csdev.couponstash.logic.commands.exceptions.CommandException;
import csdev.couponstash.model.Model;

/**
 * Redo previously undone command.
 */
public class RedoCommand extends Command {

    public static final String COMMAND_WORD = "redo";

    public static final String MESSAGE_SUCCESS = "Previous undo undone";
    public static final String MESSAGE_NO_STATE_TO_REDO_TO = "Nothing to redo";

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (!model.canRedoCouponStash()) {
            throw new CommandException(MESSAGE_NO_STATE_TO_REDO_TO);
        }

        model.redoCouponStash();
        return new CommandResult(MESSAGE_SUCCESS);
    }
}

