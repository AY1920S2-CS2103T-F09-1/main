package csdev.couponstash.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import csdev.couponstash.commons.core.Messages;
import csdev.couponstash.commons.core.index.Index;
import csdev.couponstash.commons.util.CollectionUtil;
import csdev.couponstash.logic.commands.exceptions.CommandException;
import csdev.couponstash.logic.parser.CliSyntax;
import csdev.couponstash.model.Model;
import csdev.couponstash.model.coupon.Coupon;
import csdev.couponstash.model.coupon.Email;
import csdev.couponstash.model.coupon.Name;
import csdev.couponstash.model.coupon.Phone;
import csdev.couponstash.model.tag.Tag;

/**
 * Edits the details of an existing coupon in the address book.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the coupon identified "
            + "by the index number used in the displayed coupon list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + CliSyntax.PREFIX_NAME + "NAME] "
            + "[" + CliSyntax.PREFIX_PHONE + "PHONE] "
            + "[" + CliSyntax.PREFIX_EMAIL + "EMAIL] "
            + "[" + CliSyntax.PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + CliSyntax.PREFIX_PHONE + "91234567 "
            + CliSyntax.PREFIX_EMAIL + "johndoe@example.com";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Edited Coupon: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_PERSON = "This coupon already exists in the address book.";

    private final Index index;
    private final EditPersonDescriptor editPersonDescriptor;

    /**
     * @param index of the coupon in the filtered coupon list to edit
     * @param editPersonDescriptor details to edit the coupon with
     */
    public EditCommand(Index index, EditPersonDescriptor editPersonDescriptor) {
        requireNonNull(index);
        requireNonNull(editPersonDescriptor);

        this.index = index;
        this.editPersonDescriptor = new EditPersonDescriptor(editPersonDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Coupon> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Coupon couponToEdit = lastShownList.get(index.getZeroBased());
        Coupon editedCoupon = createEditedPerson(couponToEdit, editPersonDescriptor);

        if (!couponToEdit.isSamePerson(editedCoupon) && model.hasPerson(editedCoupon)) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        model.setPerson(couponToEdit, editedCoupon);
        model.updateFilteredPersonList(Model.PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_EDIT_PERSON_SUCCESS, editedCoupon));
    }

    /**
     * Creates and returns a {@code Coupon} with the details of {@code couponToEdit}
     * edited with {@code editPersonDescriptor}.
     */
    private static Coupon createEditedPerson(Coupon couponToEdit, EditPersonDescriptor editPersonDescriptor) {
        assert couponToEdit != null;

        Name updatedName = editPersonDescriptor.getName().orElse(couponToEdit.getName());
        Phone updatedPhone = editPersonDescriptor.getPhone().orElse(couponToEdit.getPhone());
        Email updatedEmail = editPersonDescriptor.getEmail().orElse(couponToEdit.getEmail());
        Set<Tag> updatedTags = editPersonDescriptor.getTags().orElse(couponToEdit.getTags());

        return new Coupon(updatedName, updatedPhone, updatedEmail, updatedTags);
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCommand)) {
            return false;
        }

        // state check
        EditCommand e = (EditCommand) other;
        return index.equals(e.index)
                && editPersonDescriptor.equals(e.editPersonDescriptor);
    }

    /**
     * Stores the details to edit the coupon with. Each non-empty field value will replace the
     * corresponding field value of the coupon.
     */
    public static class EditPersonDescriptor {
        private Name name;
        private Phone phone;
        private Email email;
        private Set<Tag> tags;

        public EditPersonDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditPersonDescriptor(EditPersonDescriptor toCopy) {
            setName(toCopy.name);
            setPhone(toCopy.phone);
            setEmail(toCopy.email);
            setTags(toCopy.tags);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, phone, email, tags);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
        }

        public Optional<Phone> getPhone() {
            return Optional.ofNullable(phone);
        }

        public void setEmail(Email email) {
            this.email = email;
        }

        public Optional<Email> getEmail() {
            return Optional.ofNullable(email);
        }

        /**
         * Sets {@code tags} to this object's {@code tags}.
         * A defensive copy of {@code tags} is used internally.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = (tags != null) ? new HashSet<>(tags) : null;
        }

        /**
         * Returns an unmodifiable tag set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Tag>> getTags() {
            return (tags != null) ? Optional.of(Collections.unmodifiableSet(tags)) : Optional.empty();
        }

        @Override
        public boolean equals(Object other) {
            // short circuit if same object
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditPersonDescriptor)) {
                return false;
            }

            // state check
            EditPersonDescriptor e = (EditPersonDescriptor) other;

            return getName().equals(e.getName())
                    && getPhone().equals(e.getPhone())
                    && getEmail().equals(e.getEmail())
                    && getTags().equals(e.getTags());
        }
    }
}