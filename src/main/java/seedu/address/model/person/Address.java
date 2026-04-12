package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Locale;

/**
 * Represents a Person's address in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidAddress(String)}
 */
public class Address {

    public static final String MESSAGE_CONSTRAINTS =
            "Address must not be blank and must end with a valid 6-digit postal code";
    public static final String MESSAGE_POSTAL_CODE_CONSTRAINTS =
            "Address must be a 6-digit postal code only";

    /*
     * The first character of the address must not be a whitespace,
     * otherwise " " (a blank string) becomes a valid input.
     * The address can contain any characters but must end with exactly a 6-digit number.
     */
    public static final String VALIDATION_REGEX = ".*\\b\\d{6}";
    public static final String POSTAL_CODE_VALIDATION_REGEX = "\\d{6}";

    public final String value;
    private final String normalizedValue;

    /**
     * Constructs an {@code Address}.
     *
     * @param address A valid address.
     */
    public Address(String address) {
        requireNonNull(address);
        String trimmedAddress = address.trim()
                .replaceAll("\\s*,\\s*", ", ")
                .replaceAll("\\s+", " ");
        checkArgument(isValidAddress(trimmedAddress), MESSAGE_CONSTRAINTS);
        value = trimmedAddress;
        // Normalize: lowercase, then remove commas
        normalizedValue = trimmedAddress.toLowerCase(Locale.ROOT)
                .replaceAll(",", " ")
                .replaceAll("\\s+", " ");
    }

    /**
     * Returns true if a given string is a valid address.
     */
    public static boolean isValidAddress(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    /**
     * Returns true if a given string is a valid 6-digit postal code.
     */
    public static boolean isValidPostalCode(String test) {
        return test.matches(POSTAL_CODE_VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Address)) {
            return false;
        }

        Address otherAddress = (Address) other;
        return value.equals(otherAddress.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    /**
     * Returns true if both addresses are logically the same (case-insensitive, ignoring commas and whitespace).
     * Used for duplicate detection.
     */
    public boolean isSameLogicalAddress(Address other) {
        return normalizedValue.equals(other.normalizedValue);
    }

}
