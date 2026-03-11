package seedu.address.model.person;

import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's membership ID in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidMembershipId(int)}
 */
public class MembershipId {

    public static final int MIN_ID = 1000;
    public static final int MAX_ID = 9999;
    public static final int MAX_CAPACITY = MAX_ID - MIN_ID + 1;
    public static final String MESSAGE_CONSTRAINTS =
            "Membership ID must be a 4-digit integer from " + MIN_ID + " to " + MAX_ID;

     public final int value;

    /**
    * Constructs a {@code MembershipId}.
    *
    * @param id A valid membership ID.
    */
    public MembershipId(int id) {
        checkArgument(isValidMembershipId(id), MESSAGE_CONSTRAINTS);
        value = id;
    }

    /**
    * Returns true if a given integer is a valid membership ID.
    */
    public static boolean isValidMembershipId(int test) {
        return test >= MIN_ID && test <= MAX_ID;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof MembershipId)) {
            return false;
        }

        MembershipId otherMembershipId = (MembershipId) other;
        return value == otherMembershipId.value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }

}