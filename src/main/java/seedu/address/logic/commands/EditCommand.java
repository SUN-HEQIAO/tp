package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MEMBERSHIP_EXPIRY_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.MembershipExpiryDate;
import seedu.address.model.person.MembershipId;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;

/**
 * Edits the details of an existing person in the address book.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the person identified "
            + "by the membership ID of the person.\n"
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: MEMBERSHIP_ID (must be a 4-digit positive integer from "
            + MembershipId.MIN_ID + " to " + MembershipId.MAX_ID + ") "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_ADDRESS + "ADDRESS] "
            + "[" + PREFIX_MEMBERSHIP_EXPIRY_DATE + "EXPIRY_DATE] \n"
            + "Example: " + COMMAND_WORD + " 1001 "
            + PREFIX_PHONE + "91234567 "
            + PREFIX_EMAIL + "johndoe@example.com";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Edited person: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book.";
    public static final String MESSAGE_ALL_FIELDS_IDENTICAL = "No changes were made because all fields are identical.";
    public static final String MESSAGE_UNCHANGED_FIELDS = " Unchanged fields: ";

    private static final Logger logger = LogsCenter.getLogger(EditCommand.class);

    private final MembershipId membershipId;
    private final EditPersonDescriptor editPersonDescriptor;

    /**
     * @param membershipId membership ID of the person in the filtered person list to edit
     * @param editPersonDescriptor details to edit the person with
     */
    public EditCommand(MembershipId membershipId, EditPersonDescriptor editPersonDescriptor) {
        requireNonNull(membershipId);
        requireNonNull(editPersonDescriptor);

        this.membershipId = membershipId;
        this.editPersonDescriptor = new EditPersonDescriptor(editPersonDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        assert membershipId != null : "Target membership ID should not be null";
        assert editPersonDescriptor != null : "Edit descriptor should not be null";

        logger.info("Executing EditCommand for Membership ID: " + membershipId
                + " with descriptor: " + editPersonDescriptor);
        List<Person> allPersons = model.getAddressBook().getPersonList();

        Person personToEdit = null;
        logger.warning("No person found with Membership ID: " + membershipId);
        for (Person person : allPersons) {
            if (person.getMembershipId().equals(membershipId)) {
                personToEdit = person;
                break;
            }
        }
        if (personToEdit == null) {
            throw new CommandException(String.format(Messages.MESSAGE_PERSON_NOT_FOUND, membershipId));
        }

        // Check if all fields provided are identical to existing values
        if (areAllFieldsIdentical(personToEdit)) {
            logger.info("All provided fields are identical to existing values for Membership ID: " + membershipId);
            return new CommandResult(MESSAGE_ALL_FIELDS_IDENTICAL);
        }

        Person editedPerson = createEditedPerson(personToEdit, editPersonDescriptor);

        if (!personToEdit.isSamePerson(editedPerson) && model.hasPerson(editedPerson)) {
            logger.warning("Duplicate person detected while editing Membership ID: " + membershipId
                    + "; edited person: " + editedPerson);
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        // Start to build message with unchanged fields information
        // Message will be empty if there are no unchanged fields
        String unchangedMsg = buildUnchangedFieldsMessage(personToEdit);

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        String resultMsg = String.format(MESSAGE_EDIT_PERSON_SUCCESS,
                Messages.format(editedPerson)) + unchangedMsg;
        return new CommandResult(resultMsg);
    }

    /**
     * Checks if all provided fields in the descriptor are identical to the existing person's values.
     */
    private boolean areAllFieldsIdentical(Person personToEdit) {
        if (!editPersonDescriptor.isAnyFieldEdited()) {
            return false; // No fields were provided, don't bother checking if they're identical.
        }

        // Assume all fields are identical until we find one that is different
        boolean allIdentical = true;

        if (editPersonDescriptor.getName().isPresent()) {
            if (!editPersonDescriptor.getName().get().equals(personToEdit.getName())) {
                allIdentical = false;
            }
        }

        if (editPersonDescriptor.getPhone().isPresent()) {
            if (!editPersonDescriptor.getPhone().get().equals(personToEdit.getPhone())) {
                allIdentical = false;
            }
        }

        if (editPersonDescriptor.getEmail().isPresent()) {
            if (!editPersonDescriptor.getEmail().get().equals(personToEdit.getEmail())) {
                allIdentical = false;
            }
        }

        if (editPersonDescriptor.getAddress().isPresent()) {
            if (!editPersonDescriptor.getAddress().get().equals(personToEdit.getAddress())) {
                allIdentical = false;
            }
        }

        if (editPersonDescriptor.getMembershipExpiryDate().isPresent()) {
            if (!editPersonDescriptor.getMembershipExpiryDate().get().equals(personToEdit.getMembershipExpiryDate())) {
                allIdentical = false;
            }
        }

        return allIdentical;
    }

    /**
     * Builds a message indicating which fields were not changed during editing.
     * Only returns a message if there are unchanged fields.
     */
    private String buildUnchangedFieldsMessage(Person personToEdit) {
        StringBuilder unchangedFields = new StringBuilder();

        if (editPersonDescriptor.getName().isPresent()
                && editPersonDescriptor.getName().get().equals(personToEdit.getName())) {
            unchangedFields.append("Name, ");
        }

        if (editPersonDescriptor.getPhone().isPresent()
                && editPersonDescriptor.getPhone().get().equals(personToEdit.getPhone())) {
            unchangedFields.append("Phone, ");
        }

        if (editPersonDescriptor.getEmail().isPresent()
                && editPersonDescriptor.getEmail().get().equals(personToEdit.getEmail())) {
            unchangedFields.append("Email, ");
        }

        if (editPersonDescriptor.getAddress().isPresent()
                && editPersonDescriptor.getAddress().get().equals(personToEdit.getAddress())) {
            unchangedFields.append("Address, ");
        }

        if (editPersonDescriptor.getMembershipExpiryDate().isPresent()
                && editPersonDescriptor.getMembershipExpiryDate().get()
                        .equals(personToEdit.getMembershipExpiryDate())) {
            unchangedFields.append("Membership Expiry Date, ");
        }

        // Return message if there are unchanged fields
        if (unchangedFields.length() > 0) {
            unchangedFields.setLength(unchangedFields.length() - 2);
            return MESSAGE_UNCHANGED_FIELDS + unchangedFields.toString();
        }

        return "";
    }

    /**
     * Creates and returns a {@code Person} with the details of {@code personToEdit}
     * edited with {@code editPersonDescriptor}.
     */
    private static Person createEditedPerson(Person personToEdit, EditPersonDescriptor editPersonDescriptor) {
        assert personToEdit != null;

        Name updatedName = editPersonDescriptor.getName().orElse(personToEdit.getName());
        Phone updatedPhone = editPersonDescriptor.getPhone().orElse(personToEdit.getPhone());
        Email updatedEmail = editPersonDescriptor.getEmail().orElse(personToEdit.getEmail());
        Address updatedAddress = editPersonDescriptor.getAddress().orElse(personToEdit.getAddress());
        // Membership ID cannot be edited - preserve original
        MembershipId membershipId = personToEdit.getMembershipId();
        MembershipExpiryDate updatedMembershipExpiryDate = editPersonDescriptor.getMembershipExpiryDate()
                .orElse(personToEdit.getMembershipExpiryDate());

        return new Person(updatedName, updatedPhone, updatedEmail, updatedAddress, membershipId,
                updatedMembershipExpiryDate);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCommand)) {
            return false;
        }

        EditCommand otherEditCommand = (EditCommand) other;
        return membershipId.equals(otherEditCommand.membershipId)
                && editPersonDescriptor.equals(otherEditCommand.editPersonDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("membershipId", membershipId)
                .add("editPersonDescriptor", editPersonDescriptor)
                .toString();
    }

    /**
     * Stores the details to edit the person with. Each non-empty field value will replace the
     * corresponding field value of the person.
     */
    public static class EditPersonDescriptor {
        private Name name;
        private Phone phone;
        private Email email;
        private Address address;
        private MembershipExpiryDate membershipExpiryDate;

        public EditPersonDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditPersonDescriptor(EditPersonDescriptor toCopy) {
            setName(toCopy.name);
            setPhone(toCopy.phone);
            setEmail(toCopy.email);
            setAddress(toCopy.address);
            setMembershipExpiryDate(toCopy.membershipExpiryDate);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, phone, email, address, membershipExpiryDate);
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

        public void setAddress(Address address) {
            this.address = address;
        }

        public Optional<Address> getAddress() {
            return Optional.ofNullable(address);
        }

        public void setMembershipExpiryDate(MembershipExpiryDate membershipExpiryDate) {
            this.membershipExpiryDate = membershipExpiryDate;
        }

        public Optional<MembershipExpiryDate> getMembershipExpiryDate() {
            return Optional.ofNullable(membershipExpiryDate);
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditPersonDescriptor)) {
                return false;
            }

            EditPersonDescriptor otherEditPersonDescriptor = (EditPersonDescriptor) other;
            return Objects.equals(name, otherEditPersonDescriptor.name)
                    && Objects.equals(phone, otherEditPersonDescriptor.phone)
                    && Objects.equals(email, otherEditPersonDescriptor.email)
                    && Objects.equals(address, otherEditPersonDescriptor.address)
                    && Objects.equals(membershipExpiryDate, otherEditPersonDescriptor.membershipExpiryDate);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("name", name)
                    .add("phone", phone)
                    .add("email", email)
                    .add("address", address)
                    .add("membershipExpiryDate", membershipExpiryDate)
                    .toString();
        }
    }
}
