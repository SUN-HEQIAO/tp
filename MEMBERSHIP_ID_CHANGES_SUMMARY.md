# Complete List of Files Changed for Membership ID Implementation

## Summary
**Total Files Changed: 26**

---

## 1. Core Model Layer (5 files)

### New Files
1. **`src/main/java/seedu/address/model/person/MembershipId.java`** ✨ NEW
   - New value object class for membership ID
   - Validates ID range (1000-9999)
   - Contains MIN_ID, MAX_ID, MAX_CAPACITY constants
   - MESSAGE_CONSTRAINTS uses constants

### Modified Files
2. **`src/main/java/seedu/address/model/person/Person.java`**
   - Added `private final MembershipId membershipId` field
   - Updated constructor to accept MembershipId parameter
   - Added `getMembershipId()` getter method
   - Updated `equals()`, `hashCode()`, and `toString()` methods

3. **`src/main/java/seedu/address/model/AddressBook.java`**
   - Added `private int nextMembershipId` field
   - Added `getNextMembershipId()` method (returns and increments)
   - Added `canGenerateMembershipId()` method (checks if ID < MAX_ID)
   - Added `updateNextMembershipId()` method (recalculates from existing persons)
   - Modified `resetData()` to call updateNextMembershipId()
   - Changed from stream-based to explicit if/else for clarity
   - Uses `getPersonList()` instead of direct field access

4. **`src/main/java/seedu/address/model/Model.java`** (Interface)
   - Added `int getNextMembershipId()` method
   - Added `boolean canGenerateMembershipId()` method

5. **`src/main/java/seedu/address/model/ModelManager.java`**
   - Added delegation for `getNextMembershipId()` to AddressBook
   - Added delegation for `canGenerateMembershipId()` to AddressBook

---

## 2. Logic/Commands Layer (4 files)

6. **`src/main/java/seedu/address/logic/commands/AddCommand.java`**
   - Added MembershipId import
   - Added `MESSAGE_ADDRESS_BOOK_FULL` constant (uses MembershipId.MAX_CAPACITY)
   - Added capacity check: `if (!model.canGenerateMembershipId())`
   - Gets next ID via `model.getNextMembershipId()`
   - Creates Person with generated membership ID

7. **`src/main/java/seedu/address/logic/commands/EditCommand.java`**
   - Modified `createEditedPerson()` to preserve original membership ID
   - Ensures membership ID cannot be edited

8. **`src/main/java/seedu/address/logic/parser/AddCommandParser.java`**
   - Added MembershipId import
   - Uses `MembershipId.MIN_ID` as placeholder when creating Person
   - Comment explains placeholder will be replaced by AddCommand

9. **`src/main/java/seedu/address/logic/Messages.java`**
   - Updated `format()` method to include membership ID in output

---

## 3. Storage Layer (1 file)

10. **`src/main/java/seedu/address/storage/JsonAdaptedPerson.java`**
    - Added `private final Integer membershipId` field
    - Updated `@JsonCreator` constructor to accept membershipId parameter
    - Updated conversion constructor to extract membershipId from Person
    - Added membershipId parameter in `toModelType()` method
    - Added validation for membershipId:
      - Null check
      - Range validation (1000-9999)
      - Error message: "Person's MembershipId field is missing!"

---

## 4. UI Layer (2 files)

11. **`src/main/java/seedu/address/ui/PersonCard.java`**
    - Added `@FXML private Label membershipId` field
    - Updated constructor to set membership ID label text
    - Format: `"Member ID: " + person.getMembershipId().value`

12. **`src/main/resources/view/PersonListCard.fxml`**
    - Added `<Label fx:id="membershipId" .../>` element
    - Placed after email label in layout

---

## 5. Test Utilities (3 files)

13. **`src/test/java/seedu/address/testutil/PersonBuilder.java`**
    - Added `DEFAULT_MEMBERSHIP_ID = MembershipId.MIN_ID` constant
    - Added `private MembershipId membershipId` field
    - Added `withMembershipId(int membershipId)` builder method
    - Updated constructors to initialize membershipId
    - Updated `build()` to include membershipId in Person creation

14. **`src/test/java/seedu/address/testutil/TypicalPersons.java`**
    - Added MembershipId import
    - Updated all 10 test persons with unique membership IDs (1000-1010):
      - ALICE: 1000, BENSON: 1001, CARL: 1002, DANIEL: 1003, ELLE: 1004
      - FIONA: 1005, GEORGE: 1006, HOON: 1007, IDA: 1008
      - AMY: 1009, BOB: 1010
    - All use `MembershipId.MIN_ID + offset` pattern

15. **`src/main/java/seedu/address/model/util/SampleDataUtil.java`**
    - Updated sample data with membership IDs (1000-1005)
    - Uses `MembershipId.MIN_ID` and `MembershipId.MIN_ID + offset` pattern

---

## 6. Test Files (7 files)

16. **`src/test/java/seedu/address/logic/commands/AddCommandTest.java`**
    - Added MembershipId import
    - Updated ModelStub to implement `getNextMembershipId()` and `canGenerateMembershipId()`
    - ModelStubAcceptingPersonAdded: `private int nextMembershipId = MembershipId.MIN_ID`
    - Test assertions verify generated ID = MembershipId.MIN_ID

17. **`src/test/java/seedu/address/logic/commands/AddCommandIntegrationTest.java`**
    - Fixed `execute_newPerson_success()` test
    - Gets next ID from model: `int nextId = expectedModel.getNextMembershipId()`
    - Creates expected person with correct ID for comparison

18. **`src/test/java/seedu/address/logic/commands/EditCommandTest.java`**
    - Fixed `execute_allFieldsSpecifiedUnfilteredList_success()` test
    - Gets person being edited and preserves its membership ID
    - Creates editedPerson with same membership ID as original

19. **`src/test/java/seedu/address/logic/parser/AddCommandParserTest.java`**
    - Fixed `parse_allFieldsPresent_success()` test
    - Fixed `parse_optionalFieldsMissing_success()` test
    - Both now use `.withMembershipId(MembershipId.MIN_ID)` on expected persons
    - Matches placeholder ID that parser creates

20. **`src/test/java/seedu/address/logic/LogicManagerTest.java`**
    - Fixed `assertCommandFailureForExceptionFromStorage()` method
    - Syncs membership ID counter in expectedModel
    - Calls `getNextMembershipId()` to match actual model state

21. **`src/test/java/seedu/address/storage/JsonAdaptedPersonTest.java`**
    - Added `VALID_MEMBERSHIP_ID = MembershipId.MIN_ID` constant
    - Updated all JsonAdaptedPerson constructor calls to include membershipId parameter
    - Added null check and validation tests for membershipId

22. **`src/test/java/seedu/address/model/person/PersonTest.java`**
    - Updated `toString()` test to include membership ID in expected output

---

## 7. Data Files (4 files)

### Production Data
23. **`data/addressbook.json`**
    - Added `"membershipId"` field to all 6 persons:
      - Alice Yeoh: 1000
      - Bernice Yu: 1001
      - Charlotte Oliveiro: 1002
      - David Li: 1003
      - Irfan Ibrahim: 1004
      - Roy Balakrishnan: 1005

### Test Data
24. **`src/test/data/JsonSerializableAddressBookTest/typicalPersonsAddressBook.json`**
    - Added `"membershipId"` field to all 7 persons (1000-1006)
    - Matches TypicalPersons test utility IDs

25. **`src/test/data/JsonSerializableAddressBookTest/duplicatePersonAddressBook.json`**
    - Added `"membershipId"` field to both duplicate persons
    - First Alice: 1000, Second Alice: 1001

26. **`src/test/data/JsonAddressBookStorageTest/invalidAndValidPersonAddressBook.json`**
    - Added `"membershipId"` field to both persons
    - Valid Person: 1000, Invalid Phone Person: 1001

---

## File Changes by Category

| Category | Count | Files |
|----------|-------|-------|
| New Files | 1 | MembershipId.java |
| Model Layer | 5 | Person, AddressBook, Model, ModelManager, MembershipId |
| Logic Layer | 4 | AddCommand, EditCommand, AddCommandParser, Messages |
| Storage Layer | 1 | JsonAdaptedPerson |
| UI Layer | 2 | PersonCard (Java), PersonListCard (FXML) |
| Test Utilities | 3 | PersonBuilder, TypicalPersons, SampleDataUtil |
| Test Files | 7 | AddCommandTest, AddCommandIntegrationTest, EditCommandTest, AddCommandParserTest, LogicManagerTest, JsonAdaptedPersonTest, PersonTest |
| Data Files | 4 | addressbook.json, typicalPersonsAddressBook.json, duplicatePersonAddressBook.json, invalidAndValidPersonAddressBook.json |
| **TOTAL** | **26** | **26 code/data files** |

---

## Key Constants Used

- `MembershipId.MIN_ID` = 1000
- `MembershipId.MAX_ID` = 9999
- `MembershipId.MAX_CAPACITY` = 9000 (total possible IDs)

## Implementation Approach

✅ **Sequential ID Generation**: IDs auto-increment from 1000 onwards  
✅ **No Editing**: Membership IDs cannot be modified after creation  
✅ **Unique Constraint**: System enforces uniqueness through sequential generation  
✅ **Persistence**: IDs stored in JSON and preserved across sessions  
✅ **Capacity Limit**: System blocks additions when max capacity (9000) is reached  
✅ **UI Display**: Membership IDs shown after tags in contact cards  

