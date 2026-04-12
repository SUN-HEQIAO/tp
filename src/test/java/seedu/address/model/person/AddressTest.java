package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class AddressTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Address(null));
    }

    @Test
    public void constructor_invalidAddress_throwsIllegalArgumentException() {
        String invalidAddress = "";
        assertThrows(IllegalArgumentException.class, () -> new Address(invalidAddress));
    }

    @Test
    public void isValidAddress() {
        // null address
        assertThrows(NullPointerException.class, () -> Address.isValidAddress(null));

        // invalid addresses
        assertFalse(Address.isValidAddress("")); // empty string
        assertFalse(Address.isValidAddress(" ")); // spaces only
        assertFalse(Address.isValidAddress("Blk 123 12345")); // only 5 digits
        assertFalse(Address.isValidAddress("Blk 123 1234567")); // 7 digits
        assertFalse(Address.isValidAddress("Blk 123 123456 A")); // digits not at the end
        assertFalse(Address.isValidAddress("-")); // one character (no postal code)

        // valid addresses
        assertTrue(Address.isValidAddress("123456")); // exactly 6 digits only
        assertTrue(Address.isValidAddress("Blk 456, Den Road, #01-355 123456"));
        assertTrue(Address.isValidAddress("Leng Inc; 1234 Market St; 654321")); // long address
    }

    @Test
    public void equals() {
        Address address = new Address("Valid Address 123456");

        // same values -> returns true
        assertTrue(address.equals(new Address("Valid Address 123456")));

        // same object -> returns true
        assertTrue(address.equals(address));

        // null -> returns false
        assertFalse(address.equals(null));

        // different types -> returns false
        assertFalse(address.equals(5.0f));

        // different values -> returns false
        assertFalse(address.equals(new Address("Other Valid Address 654321")));

        // same address with and without commas -> different by equals(), but same by logical equality
        assertFalse(new Address("123 Main St, City 123456").equals(new Address("123 Main St City 123456")));
        assertTrue(new Address("123 Main St, City 123456").isSameLogicalAddress(new Address("123 Main St City 123456")));

        // same address with different comma placement -> different by equals(), but same by logical equality
        assertFalse(new Address("Blk 456, Den Road, #01-355, 123456")
                .equals(new Address("Blk 456 Den Road #01-355 123456")));
        assertTrue(new Address("Blk 456, Den Road, #01-355, 123456")
                .isSameLogicalAddress(new Address("Blk 456 Den Road #01-355 123456")));

        // same address with extra spaces and commas -> different by equals(), but same by logical equality
        assertFalse(new Address("123  Main St,  City 756452").equals(new Address("123 Main St City 756452")));
        assertTrue(new Address("123  Main St,  City 756452").isSameLogicalAddress(new Address("123 Main St City 756452")));
    }
}
