package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Represents a Person's membership expiry date in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidExpiryDate(String)}
 */
public class MembershipExpiryDate {

    public static final String MESSAGE_CONSTRAINTS =
            "Membership expiry date must be in the format YYYY-MM-DD and and must be today or a future date";
    public static final String MESSAGE_FIND_CONSTRAINTS =
            "Membership expiry date must be in the format YYYY-MM-DD and must be a valid date";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd")
            .withResolverStyle(ResolverStyle.STRICT);

    public final LocalDate value;

    /**
     * Constructs a {@code MembershipExpiryDate}.
     *
     * @param expiryDate A valid expiry date string in YYYY-MM-DD format.
     */
    public MembershipExpiryDate(String expiryDate) {
        requireNonNull(expiryDate);
        checkArgument(isValidExpiryDate(expiryDate), MESSAGE_CONSTRAINTS);
        this.value = LocalDate.parse(expiryDate, DATE_FORMATTER);
    }

    /**
     * Constructor for loading from JSON, bypasses validation.
     */
    public MembershipExpiryDate(LocalDate expiryDate) {
        this.value = expiryDate;
    }

    /**
     * Returns true if a given string is a valid expiry date.
     */
    public static boolean isValidExpiryDate(String test) {
        try {
            LocalDate parsedDate = LocalDate.parse(test, DATE_FORMATTER);
            return !parsedDate.isBefore(LocalDate.now());
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return value.format(DATE_FORMATTER);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof MembershipExpiryDate)) {
            return false;
        }

        MembershipExpiryDate otherExpiryDate = (MembershipExpiryDate) other;
        return value.equals(otherExpiryDate.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
