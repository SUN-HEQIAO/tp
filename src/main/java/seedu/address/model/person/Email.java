package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Locale;

/**
 * Represents a Person's email in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidEmail(String)}
 */
public class Email {

    public static final String MESSAGE_CONSTRAINTS =
            "Emails should be of the format local-part@domain and adhere to the following constraints:\n"
            + "1. The local-part should only contain alphanumeric characters and these special characters "
            + "(+_.-). The local-part may not start or end with special characters.\n"
            + "2. This is followed by a '@' and then a domain name made up of domain labels separated by periods. "
            + "The domain must contain at least one period (e.g., example.com).\n"
            + "3. The full email length must be at most 254 characters, the local-part at most 64 characters, "
            + "and each domain label at most 63 characters.\n"
            + "4. The top-level domain must not be numeric-only.\n"
            + "5. IP-literal domains in square brackets (e.g. [192.168.1.1]) are not allowed.\n"
            + "The domain name must:\n"
            + "    - end with a domain label at least 2 characters long\n"
            + "    - have each domain label start and end with alphanumeric characters\n"
            + "    - have each domain label consist of alphanumeric characters separated only by hyphens.";

    // alphanumeric and special characters
    private static final String SPECIAL_CHARACTERS = "+_.-";
    private static final int MAX_EMAIL_LENGTH = 254;
    private static final int MAX_LOCAL_PART_LENGTH = 64;
    private static final int MAX_DOMAIN_LABEL_LENGTH = 63;
    private static final String IPV4_LITERAL_REGEX = "^\\[(\\d{1,3}\\.){3}\\d{1,3}]$";
    private static final String ALPHANUMERIC_NO_UNDERSCORE = "[^\\W_]+"; // alphanumeric characters except underscore
    private static final String LOCAL_PART_REGEX = "^" + ALPHANUMERIC_NO_UNDERSCORE + "([" + SPECIAL_CHARACTERS + "]"
            + ALPHANUMERIC_NO_UNDERSCORE + ")*";
    private static final String DOMAIN_PART_REGEX = ALPHANUMERIC_NO_UNDERSCORE
            + "(-" + ALPHANUMERIC_NO_UNDERSCORE + ")*";
    private static final String DOMAIN_LAST_PART_REGEX = "(" + DOMAIN_PART_REGEX + "){2,}$"; // At least two chars
    private static final String DOMAIN_REGEX = "(" + DOMAIN_PART_REGEX + "\\.)*" + DOMAIN_LAST_PART_REGEX;

    public static final String VALIDATION_REGEX = LOCAL_PART_REGEX + "@" + DOMAIN_REGEX;

    public final String value;
    private final String normalizedValue;

    /**
     * Constructs an {@code Email}.
     *
     * @param email A valid email address.
     */
    public Email(String email) {
        requireNonNull(email);
        checkArgument(isValidEmail(email), MESSAGE_CONSTRAINTS);
        value = email;
        normalizedValue = email.toLowerCase(Locale.ROOT).trim();
    }

    /**
     * Returns if a given string is a valid email.
     */
    public static boolean isValidEmail(String test) {
        requireNonNull(test);
        return test.matches(VALIDATION_REGEX)
                && isWithinLengthLimits(test)
                && hasNoNumericOnlyTld(test)
                && !hasIpv4LiteralDomain(test)
                && hasPeriodInDomain(test);
    }

    private static boolean isWithinLengthLimits(String test) {
        if (test.length() > MAX_EMAIL_LENGTH) {
            return false;
        }

        int atIndex = test.indexOf('@');
        String localPart = test.substring(0, atIndex);
        String domainPart = test.substring(atIndex + 1);

        if (localPart.length() > MAX_LOCAL_PART_LENGTH) {
            return false;
        }

        String[] labels = domainPart.split("\\.");
        for (String label : labels) {
            if (label.length() > MAX_DOMAIN_LABEL_LENGTH) {
                return false;
            }
        }
        return true;
    }

    private static boolean hasNoNumericOnlyTld(String test) {
        int atIndex = test.indexOf('@');
        String domainPart = test.substring(atIndex + 1);
        String[] labels = domainPart.split("\\.");
        String tld = labels[labels.length - 1];
        return !tld.matches("\\d+");
    }

    private static boolean hasIpv4LiteralDomain(String test) {
        int atIndex = test.indexOf('@');
        String domainPart = test.substring(atIndex + 1);
        return domainPart.matches(IPV4_LITERAL_REGEX);
    }

    private static boolean hasPeriodInDomain(String test) {
        int atIndex = test.indexOf('@');
        String domainPart = test.substring(atIndex + 1);
        return domainPart.contains(".");
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
        if (!(other instanceof Email)) {
            return false;
        }

        Email otherEmail = (Email) other;
        return normalizedValue.equals(otherEmail.normalizedValue);
    }

    @Override
    public int hashCode() {
        return normalizedValue.hashCode();
    }

}
