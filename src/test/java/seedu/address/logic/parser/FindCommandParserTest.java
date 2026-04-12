package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.PREAMBLE_NON_EMPTY;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FindCommand;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.EmailContainsKeywordsPredicate;
import seedu.address.model.person.ExpiryDateContainsKeywordsPredicate;
import seedu.address.model.person.MembershipExpiryDate;
import seedu.address.model.person.MembershipId;
import seedu.address.model.person.MembershipIdContainsPredicate;
import seedu.address.model.person.Name;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Phone;
import seedu.address.model.person.PhoneContainsKeywordsPredicate;
import seedu.address.model.person.PostalCodeContainsKeywordsPredicate;

public class FindCommandParserTest {

    private final FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_nonEmptyPreamble_throwsParseException() {
        assertParseFailure(parser, PREAMBLE_NON_EMPTY + " n/Alice",
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_nameKeywords_returnsFindCommand() {
        FindCommand expectedSingle =
                new FindCommand(new NameContainsKeywordsPredicate(List.of("Alice")));
        assertParseSuccess(parser, " n/Alice", expectedSingle);

        FindCommand expectedMultiple =
                new FindCommand(new NameContainsKeywordsPredicate(List.of("Alice", "Bob")));
        assertParseSuccess(parser, " n/Alice Bob", expectedMultiple);
    }

    @Test
    public void parse_multipleKeywordsWithExtraSpaces_returnsFindCommand() {
        FindCommand expectedFindCommand =
            new FindCommand(new NameContainsKeywordsPredicate(List.of("Alice", "Bob")));
        // multiple spaces between keywords
        assertParseSuccess(parser, " n/Alice    Bob", expectedFindCommand);
        // leading and trailing whitespace
        assertParseSuccess(parser, " n/   Alice Bob   ", expectedFindCommand);
    }

    @Test
    public void parse_nameKeywordsWithIrregularWhitespace_returnsFindCommand() {
        FindCommand expectedFindCommand =
                new FindCommand(new NameContainsKeywordsPredicate(List.of("Alice", "Bob")));

        // many spaces
        assertParseSuccess(parser, " n/" + "Alice    Bob", expectedFindCommand);

        // leading/trailing
        assertParseSuccess(parser, " n/" + "   Alice Bob   ", expectedFindCommand);

        // tab
        assertParseSuccess(parser, " n/" + "Alice" + "\t" + "Bob", expectedFindCommand);

        // newline
        assertParseSuccess(parser, " n/" + "Alice" + "\n" + "Bob", expectedFindCommand);

    }


    @Test
    public void parse_phoneKeywords_returnsFindCommand() {
        FindCommand expectedFindCommand =
            new FindCommand(new PhoneContainsKeywordsPredicate(List.of("91234567", "98765432")));
        assertParseSuccess(parser, " p/91234567 98765432", expectedFindCommand);
    }

    @Test
    public void parse_multiplePrefixes_throwsParseException() {
        assertParseFailure(parser, " n/Alice p/98765432",
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_postalCodeKeywords_returnsFindCommand() {
        FindCommand expectedFindCommandSingle =
            new FindCommand(new PostalCodeContainsKeywordsPredicate(List.of("123456")));
        assertParseSuccess(parser, " a/123456", expectedFindCommandSingle);

        FindCommand expectedFindCommandMultiple =
            new FindCommand(new PostalCodeContainsKeywordsPredicate(List.of("123456", "654321")));
        assertParseSuccess(parser, " a/123456 654321", expectedFindCommandMultiple);
    }

    @Test
    public void parse_expiryDateKeywords_returnsFindCommand() {
        FindCommand expectedFindCommandSingle =
            new FindCommand(new ExpiryDateContainsKeywordsPredicate(List.of("2026-12-31")));
        assertParseSuccess(parser, " m/2026-12-31", expectedFindCommandSingle);

        FindCommand expectedFindCommandMultiple =
            new FindCommand(new ExpiryDateContainsKeywordsPredicate(List.of("2026-12-31", "2027-01-01")));
        assertParseSuccess(parser, " m/2026-12-31 2027-01-01", expectedFindCommandMultiple);

        FindCommand expectedFindCommandExpired =
            new FindCommand(new ExpiryDateContainsKeywordsPredicate(List.of("2020-01-01")));
        assertParseSuccess(parser, " m/2020-01-01", expectedFindCommandExpired);
    }

    @Test
    public void parse_emailKeywords_returnsFindCommand() {
        FindCommand expectedFindCommandSingle =
            new FindCommand(new EmailContainsKeywordsPredicate(List.of("alice@example.com")));
        assertParseSuccess(parser, " e/alice@example.com", expectedFindCommandSingle);

        FindCommand expectedFindCommandMultiple =
            new FindCommand(new EmailContainsKeywordsPredicate(List
                .of("alice@example.com", "bob@example.com")));
        assertParseSuccess(parser, " e/alice@example.com bob@example.com", expectedFindCommandMultiple);
    }

    @Test
    public void parse_emptyOrWhitespaceInput_throwsParseException() {
        assertParseFailure(parser, "",
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        assertParseFailure(parser, "     ",
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_prefixWithoutValue_throwsParseException() {
        assertParseFailure(parser, " n/", Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " n/    ", Name.MESSAGE_CONSTRAINTS);

        assertParseFailure(parser, " p/", Phone.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " p/    ", Phone.MESSAGE_CONSTRAINTS);

        assertParseFailure(parser, " e/", Email.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " e/    ", Email.MESSAGE_CONSTRAINTS);

        assertParseFailure(parser, " a/", Address.MESSAGE_POSTAL_CODE_CONSTRAINTS);
        assertParseFailure(parser, " a/    ", Address.MESSAGE_POSTAL_CODE_CONSTRAINTS);

        assertParseFailure(parser, " id/", MembershipId.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " id/    ", MembershipId.MESSAGE_CONSTRAINTS);

        assertParseFailure(parser, " m/", MembershipExpiryDate.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " m/    ", MembershipExpiryDate.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_membershipIdKeywords_returnsFindCommand() {
        FindCommand expectedFindCommandSingle =
                new FindCommand(new MembershipIdContainsPredicate(List.of("1000")));
        assertParseSuccess(parser, " id/1000", expectedFindCommandSingle);

        FindCommand expectedFindCommandMultiple =
                new FindCommand(new MembershipIdContainsPredicate(List.of("1000", "1001", "1002")));
        assertParseSuccess(parser, " id/1000 1001 1002", expectedFindCommandMultiple);
    }

    @Test
    public void parse_invalidNameKeyword_throwsParseException() {
        assertParseFailure(parser, " n/Alice@",
            Name.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_invalidPhoneKeyword_throwsParseException() {
        assertParseFailure(parser, " p/1234",
            Phone.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_invalidEmailKeyword_throwsParseException() {
        assertParseFailure(parser, " e/not-an-email",
            Email.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_invalidAddressKeyword_throwsParseException() {
        assertParseFailure(parser, " a/NoPostalCode",
            Address.MESSAGE_POSTAL_CODE_CONSTRAINTS);
    }

    @Test
    public void parse_invalidMembershipIdKeyword_throwsParseException() {
        assertParseFailure(parser, " id/999",
            MembershipId.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_invalidMembershipExpiryDateKeyword_throwsParseException() {
        assertParseFailure(parser, " m/2020-13-01",
            MembershipExpiryDate.MESSAGE_CONSTRAINTS);
    }
}
