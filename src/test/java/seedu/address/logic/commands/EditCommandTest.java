package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_MEMBERSHIP_EXPIRY_DATE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.MembershipId;
import seedu.address.model.person.Person;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for EditCommand.
 */
public class EditCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_allFieldsSpecifiedUnfilteredList_success() {
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder().withMembershipId(personToEdit.getMembershipId().value).build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(editedPerson).build();
        EditCommand editCommand = new EditCommand(personToEdit.getMembershipId(), descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson))
            + "\nChanged field(s): Name, Phone, Email, Membership Expiry Date"
            + "\nUnchanged field(s): Address";

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(model.getFilteredPersonList().get(0), editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_someFieldsSpecifiedUnfilteredList_success() {
        Index indexLastPerson = Index.fromOneBased(model.getFilteredPersonList().size());
        Person lastPerson = model.getFilteredPersonList().get(indexLastPerson.getZeroBased());

        PersonBuilder personInList = new PersonBuilder(lastPerson);
        Person editedPerson = personInList.withName(VALID_NAME_BOB).withPhone(VALID_PHONE_BOB)
                .build();

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB)
                .withPhone(VALID_PHONE_BOB).build();
        EditCommand editCommand = new EditCommand(lastPerson.getMembershipId(), descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson))
            + "\nChanged field(s): Name, Phone";

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(lastPerson, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_membershipExpiryDateSpecifiedUnfilteredList_success() {
        Index indexLastPerson = Index.fromOneBased(model.getFilteredPersonList().size());
        Person lastPerson = model.getFilteredPersonList().get(indexLastPerson.getZeroBased());
        Person editedPerson = new PersonBuilder(lastPerson)
                .withMembershipExpiryDate(VALID_MEMBERSHIP_EXPIRY_DATE_BOB)
                .build();

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withMembershipExpiryDate(VALID_MEMBERSHIP_EXPIRY_DATE_BOB)
                .build();
        EditCommand editCommand = new EditCommand(lastPerson.getMembershipId(), descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson))
            + "\nChanged field(s): Membership Expiry Date";

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(lastPerson, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_noFieldSpecifiedUnfilteredList_success() {
        MembershipId targetId = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased()).getMembershipId();
        EditCommand editCommand = new EditCommand(targetId, new EditPersonDescriptor());

        String expectedMessage = EditCommand.MESSAGE_NO_CHANGES;

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personInFilteredList = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(personInFilteredList).withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(personInFilteredList.getMembershipId(),
                new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson))
            + "\nChanged field(s): Name";

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(model.getFilteredPersonList().get(0), editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_membershipExpiryDateFilteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personInFilteredList = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(personInFilteredList)
                .withMembershipExpiryDate(VALID_MEMBERSHIP_EXPIRY_DATE_BOB)
                .build();

        EditCommand editCommand = new EditCommand(personInFilteredList.getMembershipId(),
                new EditPersonDescriptorBuilder()
                        .withMembershipExpiryDate(VALID_MEMBERSHIP_EXPIRY_DATE_BOB)
                        .build());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson))
            + "\nChanged field(s): Membership Expiry Date";

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(model.getFilteredPersonList().get(0), editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_identicalFieldsSpecifiedUnfilteredList_noChanges() {
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(personToEdit).build();
        EditCommand editCommand = new EditCommand(personToEdit.getMembershipId(), descriptor);

        String expectedMessage = EditCommand.MESSAGE_NO_CHANGES;

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_someFieldsChangedSomeFieldsUnchanged_success() {
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        String samePhone = personToEdit.getPhone().value; // unchanged
        String newEmail = "newemail@example.com"; // changed

        Person editedPerson = new PersonBuilder(personToEdit)
                .withEmail(newEmail)
                .build();

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withPhone(samePhone)
                .withEmail(newEmail)
                .build();
        EditCommand editCommand = new EditCommand(personToEdit.getMembershipId(), descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson))
                + "\nChanged field(s): Email"
                + "\nUnchanged field(s): Phone";

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(personToEdit, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_duplicatePersonUnfilteredList_failure() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(firstPerson).build();
        MembershipId secondPersonId = model.getFilteredPersonList().get(INDEX_SECOND_PERSON
                .getZeroBased()).getMembershipId();
        EditCommand editCommand = new EditCommand(secondPersonId, descriptor);

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_duplicatePersonFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        // edit person in filtered list into a duplicate in address book
        Person personInList = model.getAddressBook().getPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        MembershipId firstFilteredId = model.getFilteredPersonList().get(INDEX_FIRST_PERSON
                .getZeroBased()).getMembershipId();
        EditCommand editCommand = new EditCommand(firstFilteredId,
                new EditPersonDescriptorBuilder(personInList).build());

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_PERSON);
    }


    @Test
    public void execute_nonExistentMembershipIdUnfilteredList_failure() {
        MembershipId nonExistentId = getValidButNonExistentMembershipId(model);
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(nonExistentId, descriptor);

        assertCommandFailure(editCommand, model, String.format(Messages.MESSAGE_PERSON_NOT_FOUND, nonExistentId));
    }

    @Test
    public void execute_nonExistentMembershipIdFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        MembershipId nonExistentId = getValidButNonExistentMembershipId(model);
        EditCommand editCommand = new EditCommand(nonExistentId,
                new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build());

        assertCommandFailure(editCommand, model, String.format(Messages.MESSAGE_PERSON_NOT_FOUND, nonExistentId));
    }

    @Test
    public void equals() {
        MembershipId firstId = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased()).getMembershipId();
        MembershipId secondId = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased()).getMembershipId();
        final EditCommand standardCommand = new EditCommand(firstId, DESC_AMY);

        // same values -> returns true
        EditPersonDescriptor copyDescriptor = new EditPersonDescriptor(DESC_AMY);
        EditCommand commandWithSameValues = new EditCommand(firstId, copyDescriptor);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different membership ID -> returns false
        assertFalse(standardCommand.equals(new EditCommand(secondId, DESC_AMY)));

        // different descriptor -> returns false
        assertFalse(standardCommand.equals(new EditCommand(firstId, DESC_BOB)));
    }

    @Test
    public void toStringMethod() {
        Index index = Index.fromOneBased(1);
        MembershipId membershipId = new MembershipId(MembershipId.MIN_ID);
        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();
        EditCommand editCommand = new EditCommand(membershipId, editPersonDescriptor);
        String expected = EditCommand.class.getCanonicalName() + "{membershipId=" + membershipId
                + ", editPersonDescriptor="
                + editPersonDescriptor + "}";
        assertEquals(expected, editCommand.toString());
    }


    @Test
    public void execute_nameCapitalizationOnlyEdit_success() {
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        String originalName = personToEdit.getName().fullName;
        String capitalizedName = originalName.toUpperCase();

        // Ensure this remains a capitalization-only edit.
        if (capitalizedName.equals(originalName)) {
            capitalizedName = Character.toUpperCase(originalName.charAt(0)) + originalName.substring(1).toLowerCase();
        }

        Person editedPerson = new PersonBuilder(personToEdit)
                .withName(capitalizedName)
                .build();

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withName(capitalizedName)
                .build();
        EditCommand editCommand = new EditCommand(personToEdit.getMembershipId(), descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson))
                + "\nChanged field(s): Name";

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(personToEdit, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_addressCapitalizationOnlyEdit_success() {
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        String originalAddress = personToEdit.getAddress().value;
        String capitalizedAddress = originalAddress.toUpperCase();

        // Ensure this remains a capitalization-only edit.
        if (capitalizedAddress.equals(originalAddress)) {
            capitalizedAddress = Character.toUpperCase(originalAddress.charAt(0))
                    + originalAddress.substring(1).toLowerCase();
        }

        Person editedPerson = new PersonBuilder(personToEdit)
                .withAddress(capitalizedAddress)
                .build();

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withAddress(capitalizedAddress)
                .build();
        EditCommand editCommand = new EditCommand(personToEdit.getMembershipId(), descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson))
                + "\nChanged field(s): Address";

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(personToEdit, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    /**
     * Returns a membership ID that is valid by constraints but not present in the model.
     */
    private MembershipId getValidButNonExistentMembershipId(Model model) {
        Set<Integer> usedIds = new HashSet<>();
        for (Person person : model.getAddressBook().getPersonList()) {
            usedIds.add(person.getMembershipId().value);
        }

        for (int id = MembershipId.MIN_ID; id <= MembershipId.MAX_ID; id++) {
            if (!usedIds.contains(id)) {
                return new MembershipId(id);
            }
        }

        throw new AssertionError("Test fixture exhausted all membership IDs.");
    }
}

