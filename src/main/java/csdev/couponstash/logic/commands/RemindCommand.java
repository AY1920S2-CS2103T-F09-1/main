package csdev.couponstash.logic.commands;

import static csdev.couponstash.commons.util.CollectionUtil.requireAllNonNull;
import static csdev.couponstash.logic.parser.CliSyntax.PREFIX_REMIND;
import java.time.LocalDate; //add space
import java.util.List;
import csdev.couponstash.commons.core.Messages;// add space above
import csdev.couponstash.commons.core.index.Index;
import csdev.couponstash.logic.commands.exceptions.CommandException;
import csdev.couponstash.model.Model;
import csdev.couponstash.model.coupon.Coupon;

/** --13
 * This class represents the remind command in Coupon Stash
 * The remind command allows the user to set date to be reminded
 * for a coupon. Upon the date of the reminder, Coupon Stash will
 * notify the user in a form of a pop when Coupon Stash is launched.
 */

public class RemindCommand extends Command {

    public static final String COMMAND_WORD = "remind";
    private static String messageSuccess = "";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": set a reminder of a coupon, identified by the index number "
            + "used in coupon listing. "
            + "Existing reminder will be overwritten by the input.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + PREFIX_REMIND + " [Date] or [String]\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_REMIND + " 25-12-2020"
            + "\n" + "Example: " + COMMAND_WORD + " 2 "
            + PREFIX_REMIND + " 2 days";

    private static final String MESSAGE_ARGUMENTS = "Reminder has been set on %2$s for Coupon %1$s";

    private final Index index;
    private LocalDate remindDate;
    private String input;

    /**
     * @param index of the coupon in the coupon lists to edit
     * @param input details to remind the coupon on
     */
    // remove line 
    public RemindCommand(Index index, String input) {
        requireAllNonNull(index, input);

        this.index = index;
        this.input = input;
        this.remindDate = LocalDate.now();
    }
    //remove line
    /**
     * Executes the RemindCommand with a given Model representing
     * the current state of the Coupon Stash application
     * @param model {@code Model} which the command should operate on.
     * @return Returns the CommandResult that encompasses the
     *     message that should be shown to the user, as well as
     *     any other external actions that should occur.
     * @throws CommandException is thrown either when
     *     the remind date is set after the coupon's expiry date or the coupon
     *     index is out of range (Coupon does not exist).
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {

        List<Coupon> lastShownList = model.getFilteredCouponList();

        // index is out of range
        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_COUPON_DISPLAYED_INDEX);
        }

        Coupon couponToEdit = lastShownList.get(index.getZeroBased());

        // if "days before scenario", straightaway calculate the remindDate;
        if (input.contains("days before")) {

            int daysBefore = Integer.parseInt(input.replaceAll("[^0-9]", ""));

            remindDate = (couponToEdit.getExpiryDate().date).minusDays(daysBefore);
            couponToEdit.getRemind().setRemind(remindDate);

            messageSuccess = "Reminder has been set to remind on "
                    + couponToEdit.getRemind().getDate().toString()
                    + " (" + daysBefore + " days before coupon's expiry: "
                    + couponToEdit.getExpiryDate().value + ")";
        } else {
            LocalDate tempDate = LocalDate.parse(input);

            //check if input's date is not after the coupon's expiry date
            if (tempDate.isAfter(couponToEdit.getExpiryDate().date)) {
                throw new CommandException(Messages.MESSAGE_REMIND_DATE_EXCEED_EXPIRY_DATE);
            } else {
                remindDate = tempDate;
                couponToEdit.getRemind().setRemind(remindDate);
                messageSuccess = "Reminder has been set to remind on "
                        + couponToEdit.getRemind().getDate().toString()
                        + " (Coupon's expiry : "
                        + couponToEdit.getExpiryDate().value + ")";
            }
        }
        if (couponToEdit.getRemind().getRemindFlag()) {
            return new CommandResult(messageSuccess);
        }
        throw new CommandException(String.format(MESSAGE_ARGUMENTS, index.getOneBased(), input));
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof RemindCommand)) {
            return false;
        }

        // state check
        RemindCommand e = (RemindCommand) other;
        return index.equals(e.index)
                && input.equals(e.input);
    }
}
