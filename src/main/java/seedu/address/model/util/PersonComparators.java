package seedu.address.model.util;

import java.util.Comparator;

import seedu.address.model.person.Person;

/**
 * Provides comparators for Person fields.
 */
public class PersonComparators {
    public static final Comparator<Person> NAME_ASC =
            Comparator.comparing((Person p) -> p.getName().fullName, String.CASE_INSENSITIVE_ORDER)
                    .thenComparingInt(p -> p.getMembershipId().value);
    public static final Comparator<Person> NAME_DESC = NAME_ASC.reversed();

    public static final Comparator<Person> PHONE_ASC =
            Comparator.comparing((Person p) -> p.getPhone().value)
                    .thenComparingInt(p -> p.getMembershipId().value);
    public static final Comparator<Person> PHONE_DESC = PHONE_ASC.reversed();

    public static final Comparator<Person> EMAIL_ASC =
            Comparator.comparing((Person p) -> p.getEmail().value, String.CASE_INSENSITIVE_ORDER)
                    .thenComparingInt(p -> p.getMembershipId().value);
    public static final Comparator<Person> EMAIL_DESC = EMAIL_ASC.reversed();

    public static final Comparator<Person> ADDRESS_POSTAL_CODE_ASC =
            Comparator.comparingInt((Person p) -> Integer.parseInt(
                    p.getAddress().value.substring(p.getAddress().value.length() - 6)))
                    .thenComparingInt(p -> p.getMembershipId().value);
    public static final Comparator<Person> ADDRESS_POSTAL_CODE_DESC = ADDRESS_POSTAL_CODE_ASC.reversed();

    public static final Comparator<Person> EXPIRY_DATE_ASC =
            Comparator.comparing((Person p) -> p.getMembershipExpiryDate().value)
                    .thenComparingInt(p -> p.getMembershipId().value);
    public static final Comparator<Person> EXPIRY_DATE_DESC = EXPIRY_DATE_ASC.reversed();

    public static final Comparator<Person> ID_ASC =
            Comparator.comparingInt(p -> p.getMembershipId().value);
    public static final Comparator<Person> ID_DESC = ID_ASC.reversed();
}
