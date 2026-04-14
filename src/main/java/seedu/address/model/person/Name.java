package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Locale;

/**
 * Represents a Person's name in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidName(String)}
 */
public class Name {

    public static final int MIN_LENGTH = 1;
    public static final int MAX_LENGTH = 70;

    public static final String MESSAGE_CONSTRAINTS =
        "Only valid names that are " + MIN_LENGTH + " - " + MAX_LENGTH + " characters long, start with a letter, "
        + "and may only contain letters (including international characters), "
        + "spaces, apostrophes ('), hyphens (-), forward slashes (/) and periods (.)\n"
        + "e.g. John Doe, Mary-Jane, O'Brien, Dr. Lim, Thor s/o Odin, 小明\n"
        + "Invalid names, such as the following, will be rejected as they contain incorrect use of punctuation.\n"
        + "e.g. Mary-, Mary  -  Jane, O', 'O, Dr., .Dr, Thor s/";

    public static final String MESSAGE_FIND_CONSTRAINTS =
        "Name keywords for find must be non-empty and may contain letters and "
        + "name punctuation such as ('), (-), (/), (.)\n"
        + "Tip: You may use either the first, middle, or last name (in full) to find a member.";

    public static final String VALIDATION_REGEX = "^[\\p{L}]+(?:[.'/ -]\\s*[\\p{L}]+)*$";

    public final String fullName;
    private final String normalizedFullName;

    /**
     * Constructs a {@code Name}.
     *
     * @param name A valid name.
     */
    public Name(String name) {
        requireNonNull(name);
        String trimmedName = name.trim().replaceAll("\\s+", " ");
        checkArgument(isValidName(trimmedName), MESSAGE_CONSTRAINTS);
        this.fullName = trimmedName;
        this.normalizedFullName = normalize(trimmedName);
    }

    /**
     * Returns true if a given string is a valid name.
     */
    public static boolean isValidName(String test) {
        requireNonNull(test);
        String trimmedTest = test.trim().replaceAll("\\s+", " ");
        return trimmedTest.length() >= MIN_LENGTH
                && trimmedTest.length() <= MAX_LENGTH
                && trimmedTest.matches(VALIDATION_REGEX);
    }

    private static String normalize(String name) {
        return name.toLowerCase(Locale.ROOT);
    }

    @Override
    public String toString() {
        return fullName;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Name)) {
            return false;
        }

        Name otherName = (Name) other;
        return fullName.equals(otherName.fullName);
    }

    @Override
    public int hashCode() {
        return fullName.hashCode();
    }

    public String getNormalizedFullName() {
        return normalizedFullName;
    }
}
