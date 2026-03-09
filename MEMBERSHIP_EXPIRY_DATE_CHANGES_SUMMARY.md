# Complete List of Files Changed for Membership Expiry Date Implementation

## Summary
**Total Files Changed: 17**

**Phase Note**: `m/` is reserved for future use and is not accepted by `add`/`edit` in this phase.

---

## 1. Core Model Layer (2 files)

### New Files
1. **`src/main/java/seedu/address/model/person/MembershipExpiryDate.java`** ✨ NEW
   - New value object class for membership expiry date
   - Validates date format (YYYY-MM-DD)
   - Uses Java LocalDate for date handling
   - Contains MESSAGE_CONSTRAINTS and DATE_FORMATTER constants

### Modified Files
2. **`src/main/java/seedu/address/model/person/Person.java`**
   - Added `private final MembershipExpiryDate membershipExpiryDate` field
   - Updated constructor to accept MembershipExpiryDate parameter
   - Added `getMembershipExpiryDate()` getter method
   - Updated `equals()`, `hashCode()`, and `toString()` methods

---

## 2. Logic/Commands Layer (3 files)

3. **`src/main/java/seedu/address/logic/commands/AddCommand.java`**
   - Updated Person creation to include membershipExpiryDate
   - Preserves expiry date from parsed input when assigning membership ID

4. **`src/main/java/seedu/address/logic/commands/EditCommand.java`**
   - Added MembershipExpiryDate import
   - Modified `createEditedPerson()` to preserve original membership expiry date
   - Ensures expiry date cannot be edited
   - `m/` is reserved for future use and not accepted by `edit` in this phase

5. **`src/main/java/seedu/address/logic/parser/AddCommandParser.java`**
   - Added MembershipExpiryDate import
   - Uses `"2099-12-31"` as placeholder when creating Person
   - `m/` is reserved for future use and not accepted by `add` in this phase
   - Comment explains placeholder will be updated when add/edit commands support it

6. **`src/main/java/seedu/address/logic/Messages.java`**
   - Updated `format()` method to include membership expiry date in output

---

## 3. Storage Layer (1 file)

7. **`src/main/java/seedu/address/storage/JsonAdaptedPerson.java`**
   - Added `private final String membershipExpiryDate` field
   - Updated `@JsonCreator` constructor to accept membershipExpiryDate parameter
   - Updated conversion constructor to extract membershipExpiryDate from Person
   - Added membershipExpiryDate parameter in `toModelType()` method
   - Added validation for membershipExpiryDate:
     - Null check
     - Format validation (YYYY-MM-DD)
     - Error message: "Person's MembershipExpiryDate field is missing!"

---

## 4. UI Layer (2 files)

8. **`src/main/java/seedu/address/ui/PersonCard.java`**
   - Added `@FXML private Label membershipExpiryDate` field
   - Updated constructor to set expiry date label text
   - Format: `"Expiry: " + person.getMembershipExpiryDate().toString()`

9. **`src/main/resources/view/PersonListCard.fxml`**
   - Added `<Label fx:id="membershipExpiryDate" .../>` element
   - Placed after membershipId label in layout

---

## 5. Test Utilities (2 files)

10. **`src/test/java/seedu/address/testutil/PersonBuilder.java`**
    - Added `DEFAULT_MEMBERSHIP_EXPIRY_DATE = "2099-12-31"` constant
    - Added `private MembershipExpiryDate membershipExpiryDate` field
    - Added `withMembershipExpiryDate(String membershipExpiryDate)` builder method
    - Updated constructors to initialize membershipExpiryDate
    - Updated `build()` to include membershipExpiryDate in Person creation

11. **`src/main/java/seedu/address/model/util/SampleDataUtil.java`**
    - Updated all 6 sample persons with membership expiry dates:
      - Alex Yeoh: 2027-01-15
      - Bernice Yu: 2026-12-31
      - Charlotte Oliveiro: 2027-06-30
      - David Li: 2026-11-20
      - Irfan Ibrahim: 2028-03-10
      - Roy Balakrishnan: 2027-09-05

---

## 6. Test Files (2 files)

12. **`src/test/java/seedu/address/storage/JsonAdaptedPersonTest.java`**
    - Added `VALID_MEMBERSHIP_EXPIRY_DATE` constant
    - Updated all JsonAdaptedPerson constructor calls to include membershipExpiryDate parameter
    - All test methods now pass membershipExpiryDate as 7th parameter

13. **`src/test/java/seedu/address/model/person/PersonTest.java`**
    - Updated `toStringMethod()` test to include membershipExpiryDate in expected output

---

## 7. Data Files (4 files)

### Production Data
14. **`data/addressbook.json`**
    - Added `"membershipExpiryDate"` field to all 6 persons:
      - Alice Yeoh: 2027-01-15
      - Bernice Yu: 2026-12-31
      - Charlotte Oliveiro: 2027-06-30
      - David Li: 2026-11-20
      - Irfan Ibrahim: 2028-03-10
      - Roy Balakrishnan: 2027-09-05

### Test Data
15. **`src/test/data/JsonSerializableAddressBookTest/typicalPersonsAddressBook.json`**
    - Added `"membershipExpiryDate"` field to all 7 persons (all set to "2099-12-31")
    - Matches TypicalPersons test utility default expiry date

16. **`src/test/data/JsonSerializableAddressBookTest/duplicatePersonAddressBook.json`**
    - Added `"membershipExpiryDate"` field to both duplicate persons
    - Both set to "2099-12-31"

17. **`src/test/data/JsonAddressBookStorageTest/invalidAndValidPersonAddressBook.json`**
    - Added `"membershipExpiryDate"` field to both persons
    - Both set to "2099-12-31"

---

## File Changes by Category

| Category | Count | Files |
|----------|-------|-------|
| New Files | 1 | MembershipExpiryDate.java |
| Model Layer | 2 | Person, MembershipExpiryDate |
| Logic Layer | 3 | AddCommand, EditCommand, AddCommandParser, Messages |
| Storage Layer | 1 | JsonAdaptedPerson |
| UI Layer | 2 | PersonCard (Java), PersonListCard (FXML) |
| Test Utilities | 2 | PersonBuilder, SampleDataUtil |
| Test Files | 2 | JsonAdaptedPersonTest, PersonTest |
| Data Files | 4 | addressbook.json, typicalPersonsAddressBook.json, duplicatePersonAddressBook.json, invalidAndValidPersonAddressBook.json |
| **TOTAL** | **17** | **17 code/data files** |

---

## Key Constants Used

- `MembershipExpiryDate.DATE_FORMATTER` = DateTimeFormatter.ofPattern("yyyy-MM-dd")
- `PersonBuilder.DEFAULT_MEMBERSHIP_EXPIRY_DATE` = "2099-12-31"
- Placeholder expiry date in AddCommandParser = "2099-12-31"

---

## Key Differences from Membership ID Implementation

1. **No automatic generation**: Unlike membership IDs which are auto-generated, expiry dates use a placeholder value
2. **Simpler validation**: Only validates date format (YYYY-MM-DD), no range constraints
3. **Storage type**: Stored as String in JSON (converted to LocalDate in model)
4. **No capacity concerns**: No need to check if we can generate more expiry dates
5. **Command input deferred**: `m/` is reserved for future use and not accepted by `add`/`edit` in this phase
6. **Future extensibility**: Ready for add/edit command input in a later phase

---

## Testing Status

✅ All 267 tests passing
✅ Compilation successful
✅ No backward compatibility issues (as requested)

---

**Implementation Date**: March 9, 2026

