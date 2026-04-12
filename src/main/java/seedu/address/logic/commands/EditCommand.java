package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MEMBERSHIP_EXPIRY_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.ArrayList;
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

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the fields of the member identified "
            + "by membership ID.\n"
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: MEMBERSHIP_ID "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_ADDRESS + "ADDRESS] "
            + "[" + PREFIX_MEMBERSHIP_EXPIRY_DATE + "EXPIRY_DATE]\n"
            + "Example: " + COMMAND_WORD + " 1001 "
            + PREFIX_PHONE + "91234567 "
            + PREFIX_EMAIL + "johndoe@example.com";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Edited member: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_PERSON = "This member already exists in the address book.";
    public static final String MESSAGE_NO_CHANGES = "No changes made — the provided fields are identical";


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
        for (Person person : allPersons) {
            if (person.getMembershipId().equals(membershipId)) {
                personToEdit = person;
                break;
            }
        }
        if (personToEdit == null) {
            logger.warning("No member found with Membership ID: " + membershipId);
            throw new CommandException(String.format(Messages.MESSAGE_PERSON_NOT_FOUND, membershipId));
        }

        Person editedPerson = createEditedPerson(personToEdit, editPersonDescriptor);

        if (!hasMeaningfulChange(personToEdit, editedPerson, editPersonDescriptor)) {
            return new CommandResult(MESSAGE_NO_CHANGES);
        }
        List<String> changed = new ArrayList<>();
        List<String> unchanged = new ArrayList<>();
        classifyField("Name", editPersonDescriptor.getName(), personToEdit.getName(), changed, unchanged,
                EditCommand::isNameRawEqual);
        classifyField("Phone", editPersonDescriptor.getPhone(), personToEdit.getPhone(), changed, unchanged,
                Objects::equals);
        classifyField("Email", editPersonDescriptor.getEmail(), personToEdit.getEmail(), changed, unchanged,
                Objects::equals);
        classifyField("Address", editPersonDescriptor.getAddress(), personToEdit.getAddress(), changed, unchanged,
                EditCommand::isAddressRawEqual);
        classifyField("Membership Expiry Date", editPersonDescriptor.getMembershipExpiryDate(),
                personToEdit.getMembershipExpiryDate(), changed, unchanged, Objects::equals);

        if (!personToEdit.isSamePerson(editedPerson) && model.hasPerson(editedPerson)) {
            logger.warning("Duplicate member detected while editing Membership ID: " + membershipId
                    + "; edited member: " + editedPerson);
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        // Some fields are changed while other fields are unchanged
        if (!changed.isEmpty() && !unchanged.isEmpty()) {
            return new CommandResult(buildEditFeedback(editedPerson, changed, unchanged));
        }

        // All supplied fields are changed
        return new CommandResult(buildAllChangedFeedback(editedPerson, changed));
    }

    /**
     * Classifies a supplied field as changed or unchanged by comparing it with the original value.
     * Fields not supplied by the user are ignored.
     *
     * @param fieldName Name of the field being classified.
     * @param maybeNewValue New value supplied by the user, if present.
     * @param originalValue Original value from the person before editing.
     * @param changed List that collects field names detected as changed.
     * @param unchanged List that collects field names detected as unchanged.
     * @param <T> Type of the field value.
     */
    private static <T> void classifyField(String fieldName, Optional<T> maybeNewValue, T originalValue,
                                          List<String> changed, List<String> unchanged,
                                          java.util.function.BiPredicate<T, T> equalityChecker) {
        if (maybeNewValue.isEmpty()) {
            return;
        }
        if (equalityChecker.test(maybeNewValue.get(), originalValue)) {
            unchanged.add(fieldName);
        } else {
            changed.add(fieldName);
        }
    }

    private static boolean hasMeaningfulChange(Person originalPerson, Person editedPerson,
                                                EditPersonDescriptor descriptor) {
        if (!originalPerson.equals(editedPerson)) {
            return true;
        }

        // Name/Address equality is normalized; compare raw values so case-only edits are recognized.
        if (descriptor.getName().isPresent()
                && !isNameRawEqual(descriptor.getName().get(), originalPerson.getName())) {
            return true;
        }

        if (descriptor.getAddress().isPresent()
                && !isAddressRawEqual(descriptor.getAddress().get(), originalPerson.getAddress())) {
            return true;
        }

        return false;
    }

    private static boolean isNameRawEqual(Name first, Name second) {
        return first.fullName.equals(second.fullName);
    }

    private static boolean isAddressRawEqual(Address first, Address second) {
        return first.value.equals(second.value);
    }

    /**
     * Builds detailed edit feedback for the case where some supplied fields are changed
     * and some supplied fields are unchanged.
     *
     * @param editedPerson Edited person to be shown in the success message.
     * @param changed List of supplied field names that were changed.
     * @param unchanged List of supplied field names that were unchanged.
     * @return Formatted edit feedback message containing changed and unchanged fields.
     */
    private static String buildEditFeedback(Person editedPerson, List<String> changed, List<String> unchanged) {
        return String.format(MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson))
                + "\nChanged field(s): " + String.join(", ", changed)
                + "\nUnchanged field(s): " + String.join(", ", unchanged);
    }

    /**
     * Builds edit feedback for the case where all supplied fields are changed.
     *
     * @param editedPerson Edited person to be shown in the success message.
     * @param changed List of supplied field names that were changed.
     * @return Formatted edit feedback message containing changed fields only.
     */
    private static String buildAllChangedFeedback(Person editedPerson, List<String> changed) {
        return String.format(MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson))
                + "\nChanged field(s): " + String.join(", ", changed);
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
