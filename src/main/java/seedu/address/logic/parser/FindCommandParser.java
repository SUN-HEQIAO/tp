package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MEMBERSHIP_EXPIRY_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;

import java.util.Arrays;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
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

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public FindCommand parse(String args) throws ParseException {
        Prefix[] allPrefixes = new Prefix[] {
            PREFIX_NAME, PREFIX_ID, PREFIX_PHONE,
            PREFIX_ADDRESS, PREFIX_MEMBERSHIP_EXPIRY_DATE, PREFIX_EMAIL
        };
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, allPrefixes);

        if (!argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(allPrefixes);

        Prefix usedPrefix = argMultimap.verifyExactlyOnePrefixPresentFor(FindCommand.MESSAGE_USAGE, allPrefixes);

        String value = argMultimap.getValue(usedPrefix).get().trim().replaceAll("\\s+", " ");
        String[] tokens = value.isEmpty() ? new String[0] : value.split(" ");

        switch (usedPrefix.getPrefix()) {
        case "n/":
            if (tokens.length == 0) {
                throw new ParseException(Name.MESSAGE_CONSTRAINTS);
            }
            for (String token : tokens) {
                ParserUtil.parseName(token);
            }
            return new FindCommand(new NameContainsKeywordsPredicate(Arrays.asList(tokens)));
        case "p/":
            if (tokens.length == 0) {
                throw new ParseException(Phone.MESSAGE_CONSTRAINTS);
            }
            for (String token : tokens) {
                ParserUtil.parsePhone(token);
            }
            return new FindCommand(new PhoneContainsKeywordsPredicate(Arrays.asList(tokens)));
        case "e/":
            if (tokens.length == 0) {
                throw new ParseException(Email.MESSAGE_CONSTRAINTS);
            }
            for (String token : tokens) {
                ParserUtil.parseEmail(token);
            }
            return new FindCommand(new EmailContainsKeywordsPredicate(Arrays.asList(tokens)));
        case "a/":
            if (tokens.length == 0) {
                throw new ParseException(Address.MESSAGE_POSTAL_CODE_CONSTRAINTS);
            }
            for (String token : tokens) {
                if (!Address.isValidPostalCode(token)) {
                    throw new ParseException(Address.MESSAGE_POSTAL_CODE_CONSTRAINTS);
                }
            }
            return new FindCommand(new PostalCodeContainsKeywordsPredicate(Arrays.asList(tokens)));
        case "id/":
            if (tokens.length == 0) {
                throw new ParseException(MembershipId.MESSAGE_CONSTRAINTS);
            }
            for (String token : tokens) {
                ParserUtil.parseMembershipId(token);
            }
            return new FindCommand(new MembershipIdContainsPredicate(Arrays.asList(tokens)));
        case "m/":
            if (tokens.length == 0) {
                throw new ParseException(MembershipExpiryDate.MESSAGE_CONSTRAINTS);
            }
            for (String token : tokens) {
                ParserUtil.parseMembershipExpiryDateForFind(token);
            }
            return new FindCommand(new ExpiryDateContainsKeywordsPredicate(Arrays.asList(tokens)));
        default:
            throw new ParseException("Find by this prefix is not yet supported.");
        }
    }
}
