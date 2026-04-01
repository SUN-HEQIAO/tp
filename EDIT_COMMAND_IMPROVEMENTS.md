# EditCommand Improvements - Summary of Changes

## Overview
Enhanced the EditCommand to provide better user feedback when fields are unchanged or identical to existing values.

## Changes Made

### 1. New Message Constants (EditCommand.java)
Added two new constants to communicate different scenarios:

```java
public static final String MESSAGE_ALL_FIELDS_IDENTICAL = 
    "No changes were made because all fields are identical.";

public static final String MESSAGE_UNCHANGED_FIELDS = " Unchanged fields: ";
```

### 2. Updated execute() Method
The execute method now:
- **Checks if all provided fields are identical** to existing values before creating a new Person
- **Returns early** with `MESSAGE_ALL_FIELDS_IDENTICAL` if all fields are identical (no model update)
- **Builds an unchanged fields message** when some fields changed and some didn't
- **Updates the model** only when actual changes occur

### 3. New areAllFieldsIdentical() Method
Determines if all fields provided in the descriptor are identical to the existing person's values:
- Returns `false` if no fields were provided
- Checks each optional field individually
- Returns `true` only if ALL provided fields match existing values

**Usage Scenario:**
```
User types: edit 1001 n/Alice n/Pauline p/91234567 p/94351253 e/alice@example.com e/alice@example.com
Result: "No changes were made because all fields are identical."
(No Person object created, no model update)
```

### 4. New buildUnchangedFieldsMessage() Method
Builds a message listing unchanged fields when some fields changed but some didn't:
- Iterates through each optional field in the descriptor
- Appends field names that are identical to existing values
- Only returns a message if there are unchanged fields to report
- Works correctly because if `areAllFieldsIdentical()` returned false, there's guaranteed to be at least one changed field

**Format:** ` Unchanged fields: Name, Phone` (comma-separated list)

**Why this approach works:**
Since this method is only called after `areAllFieldsIdentical()` returns false, we know there's at least one field that changed. Therefore, we don't need to explicitly check if changed fields exist - they must exist by logic flow.

## Test Coverage

### New Tests Added

1. **execute_allFieldsIdenticalUnfilteredList_success()**
   - Tests when all provided fields match existing values
   - Verifies MESSAGE_ALL_FIELDS_IDENTICAL is returned
   - Confirms model is not updated

2. **execute_someFieldsIdenticalUnfilteredList_success()**
   - Tests when some fields changed and some didn't
   - Verifies MESSAGE_EDIT_PERSON_SUCCESS is shown with unchanged fields
   - Confirms which fields were unchanged in the message

### Updated Tests

- **execute_allFieldsSpecifiedUnfilteredList_success()**
  - Updated to ensure all field values differ from the person being edited
  - Ensures no unchanged fields message appears when all fields change
  - Uses VALID test constants for proper test data

## Implementation Details

### How `.equals()` works
The implementation uses `.equals()` to compare field values:
```java
if (editPersonDescriptor.getName().isPresent()
        && editPersonDescriptor.getName().get().equals(personToEdit.getName())) {
    unchangedFields.append("Name, ");
}
```

Each field type (Name, Phone, Email, Address, MembershipExpiryDate) has its own `equals()` method that compares the internal values properly.

### Why the logic is simple
The `buildUnchangedFieldsMessage()` method doesn't need to track both changed and unchanged field counts because:
- This method is **only called after** `areAllFieldsIdentical()` returns `false`
- If `areAllFieldsIdentical()` returned `false`, there's **guaranteed** to be at least one changed field
- Therefore, we only need to check if unchanged fields exist - any we find are valid to report

This eliminates unnecessary complexity while maintaining correctness.

### String concatenation and cleanup
When building the unchanged fields message:
```java
unchangedFields.append("Name, ");  // Adds trailing ", "
unchangedFields.setLength(unchangedFields.length() - 2);  // Removes last ", "
```
This removes the trailing comma and space to produce clean output: `"Name, Phone"` instead of `"Name, Phone, "`

## Summary Table

| Scenario | Fields Provided | All Identical? | Changed Fields | Message Type | Model Updated |
|----------|-----------------|---|---|---|---|
| Edit with no fields | 0 | N/A | 0 | MESSAGE_NOT_EDITED | No |
| Edit with all identical fields | 3+ | Yes | 0 | MESSAGE_ALL_FIELDS_IDENTICAL | No |
| Edit with all new fields | 3+ | No | 3+ | SUCCESS (no unchanged msg) | Yes |
| Edit with mixed fields | 3+ | No | 1-2 | SUCCESS + unchanged msg | Yes |

## Maintainability

When adding a new editable field in the future:
1. Add the field to `EditPersonDescriptor` class
2. Add a check in `areAllFieldsIdentical()` method
3. Add a check in `buildUnchangedFieldsMessage()` method
4. Add corresponding test cases

The pattern is consistent and easy to follow across all methods.

