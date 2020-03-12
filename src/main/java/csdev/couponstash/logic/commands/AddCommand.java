package csdev.couponstash.logic.commands;

import static csdev.couponstash.logic.parser.CliSyntax.PREFIX_NAME;
import static csdev.couponstash.logic.parser.CliSyntax.PREFIX_PHONE;
import static csdev.couponstash.logic.parser.CliSyntax.PREFIX_TAG;

import static java.util.Objects.requireNonNull;

import csdev.couponstash.logic.commands.exceptions.CommandException;
import csdev.couponstash.model.Model;
import csdev.couponstash.model.coupon.Coupon;

/**
 * Adds a coupon to CouponStash.
 */
public class AddCommand extends Command {

    public static final String COMMAND_WORD = "add";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a coupon to CouponStash. "
            + "Parameters: "
            + PREFIX_NAME + "NAME "
            + PREFIX_PHONE + "PHONE "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_NAME + "John Doe "
            + PREFIX_PHONE + "98765432 "
            + PREFIX_TAG + "friends "
            + PREFIX_TAG + "owesMoney";

    public static final String MESSAGE_SUCCESS = "New coupon added: %1$s";
    public static final String MESSAGE_DUPLICATE_COUPON = "This coupon already exists in CouponStash";

    private final Coupon toAdd;

    /**
     * Creates an AddCommand to add the specified {@code Coupon}
     */
    public AddCommand(Coupon coupon) {
        requireNonNull(coupon);
        toAdd = coupon;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (model.hasCoupon(toAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_COUPON);
        }

        model.addCoupon(toAdd);
        return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof AddCommand // instanceof handles nulls
                && toAdd.equals(((AddCommand) other).toAdd));
    }
}
