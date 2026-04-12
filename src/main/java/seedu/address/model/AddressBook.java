package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.List;

import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.person.MembershipId;
import seedu.address.model.person.Person;
import seedu.address.model.person.UniquePersonList;

/**
 * Wraps all data at the address-book level
 * Duplicates are not allowed (by .isSamePerson comparison)
 */
public class AddressBook implements ReadOnlyAddressBook {

    private final UniquePersonList persons;
    private int nextMembershipId;

    /*
     * The 'unusual' code block below is a non-static initialization block, sometimes used to avoid duplication
     * between constructors. See https://docs.oracle.com/javase/tutorial/java/javaOO/initial.html
     *
     * Note that non-static init blocks are not recommended to use. There are other ways to avoid duplication
     *   among constructors.
     */
    //    {
    //        persons = new UniquePersonList();
    //        nextMembershipId = MembershipId.MIN_ID;
    //    }

    /**
     * Constructs an empty {@code AddressBook}.
     */
    public AddressBook() {
        persons = new UniquePersonList();
        nextMembershipId = MembershipId.MIN_ID;
    }

    /**
     * Creates an AddressBook using the Persons in the {@code toBeCopied}
     */
    public AddressBook(ReadOnlyAddressBook toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// list overwrite operations

    /**
     * Replaces the contents of the person list with {@code persons}.
     * {@code persons} must not contain duplicate persons.
     */
    public void setPersons(List<Person> persons) {
        this.persons.setPersons(persons);
    }

    /**
     * Resets the existing data of this {@code AddressBook} with {@code newData}.
     */
    public void resetData(ReadOnlyAddressBook newData) {
        requireNonNull(newData);

        setPersons(newData.getPersonList());
        if (newData instanceof AddressBook) {
            setNextMembershipId(((AddressBook) newData).peekNextMembershipId());
        } else {
            updateNextMembershipId();
        }
    }

    /**
     * Updates the next membership ID based on the current persons in the address book.
     * Sets it to the maximum existing ID + 1, or MIN_ID if no persons exist.
     */
    public void updateNextMembershipId() {
        if (getPersonList().isEmpty()) {
            nextMembershipId = MembershipId.MIN_ID;
        } else {
            int maxId = Integer.MIN_VALUE;
            for (Person person : getPersonList()) {
                maxId = Math.max(maxId, person.getMembershipId().value);
            }
            nextMembershipId = maxId + 1;
        }
    }

    /**
     * Gets the next available membership ID and increments the counter.
     */
    public int getNextMembershipId() {
        return nextMembershipId++;
    }

    /**
     * Returns true if another membership ID can still be generated.
     */
    public boolean canGenerateMembershipId() {
        return nextMembershipId <= MembershipId.MAX_ID;
    }

    /**
     * Returns the next membership ID without incrementing.
     */
    public int peekNextMembershipId() {
        return nextMembershipId;
    }

    /**
     * Sets the next membership ID to use.
     */
    public void setNextMembershipId(int nextMembershipId) {
        if (nextMembershipId < MembershipId.MIN_ID || nextMembershipId > MembershipId.MAX_ID + 1) {
            throw new IllegalArgumentException("Next membership ID out of valid range.");
        }
        this.nextMembershipId = nextMembershipId;
    }

    //// person-level operations

    /**
     * Returns true if a person with the same identity as {@code person} exists in the address book.
     */
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return persons.contains(person);
    }

    /**
     * Adds a person to the address book.
     * The person must not already exist in the address book.
     */
    public void addPerson(Person p) {
        persons.add(p);
        if (p.getMembershipId().value >= nextMembershipId) {
            nextMembershipId = p.getMembershipId().value + 1;
        }
    }

    /**
     * Replaces the given person {@code target} in the list with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the address book.
     */
    public void setPerson(Person target, Person editedPerson) {
        requireNonNull(editedPerson);

        persons.setPerson(target, editedPerson);
    }

    /**
     * Removes {@code key} from this {@code AddressBook}.
     * {@code key} must exist in the address book.
     */
    public void removePerson(Person key) {
        persons.remove(key);
    }

    //// util methods

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("persons", persons)
                .toString();
    }

    @Override
    public ObservableList<Person> getPersonList() {
        return persons.asUnmodifiableObservableList();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddressBook)) {
            return false;
        }

        AddressBook otherAddressBook = (AddressBook) other;
        return persons.equals(otherAddressBook.persons)
                && nextMembershipId == otherAddressBook.nextMembershipId;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(persons, nextMembershipId);
    }
}
