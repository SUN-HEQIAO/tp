---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# AB-3 Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

_{ list here sources of all reused/adapted ideas, code, documentation, and third-party libraries -- include links to the original source as well }_

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Person` object residing in the `Model`.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete 1` Command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `AddressBookParser` object which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddCommand`) which the `AddressBookParser` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `AddCommandParser`, `DeleteCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the currently 'selected' `Person` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Person>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<box type="info" seamless>

**Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `AddressBook`, which `Person` references. This allows `AddressBook` to only require one `Tag` object per unique tag, instead of each `Person` needing their own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />

</box>


### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from both `AddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` — Saves the current address book state in its history.
* `VersionedAddressBook#undo()` — Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` — Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

<puml src="diagrams/UndoRedoState0.puml" alt="UndoRedoState0" />

Step 2. The user executes `delete 5` command to delete the 5th person in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

<puml src="diagrams/UndoRedoState1.puml" alt="UndoRedoState1" />

Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

<puml src="diagrams/UndoRedoState2.puml" alt="UndoRedoState2" />

<box type="info" seamless>

**Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.

</box>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

<puml src="diagrams/UndoRedoState3.puml" alt="UndoRedoState3" />


<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</box>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

<puml src="diagrams/UndoSequenceDiagram-Logic.puml" alt="UndoSequenceDiagram-Logic" />

<box type="info" seamless>

**Note:** The lifeline for `UndoCommand` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.

</box>

Similarly, how an undo operation goes through the `Model` component is shown below:

<puml src="diagrams/UndoSequenceDiagram-Model.puml" alt="UndoSequenceDiagram-Model" />

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</box>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

<puml src="diagrams/UndoRedoState4.puml" alt="UndoRedoState4" />

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …​` command. This is the behavior that most modern desktop applications follow.

<puml src="diagrams/UndoRedoState5.puml" alt="UndoRedoState5" />

The following activity diagram summarizes what happens when a user executes a new command:

<puml src="diagrams/CommitActivityDiagram.puml" width="250" />

#### Design considerations:

**Aspect: How undo & redo executes:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
  * Cons: We must ensure that the implementation of each individual command are correct.

_{more aspects and alternatives to be added}_

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_


--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* has a need to manage a significant number of contacts
* prefer desktop apps over other types
* can type fast
* prefers typing to mouse interactions
* is reasonably comfortable using CLI apps

**Value proposition**: manage contacts faster than a typical mouse/GUI driven app


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​     | I want to …​                                                  | So that I can…​                                                            |
|----------|-------------|---------------------------------------------------------------|----------------------------------------------------------------------------|
| `* * *`  | Gym manager | I want to add new members                                     | So that I can keep a record of new members                                 |
| `* * *`  | Gym manager | I want to view the list of members                            | So that I understand who is currently recorded                             |
| `* * *`  | Gym manager | I want to delete a member                                     | So that I can remove incorrect or obsolete records                         |
| `* * `   | Gym manager | I want to know how to interact with the app                   | So that I can begin to use the app                                         |
| `* *`    | Gym manager | I want to search for a member                                 | So that I can retrieve their information                                   |
| `* *`    | Gym manager | I want to edit member's personal information                  | So that my records can stay updated with the latest information            |
| `* *`    | Gym manager | I want to know which member's membership is close to expiring | So that I can contact member's to remind them of their memberhsip validity |
| `*`      | Gym manager | I want to check for a member's membership validity            | So that I can allow valid entry to gyms                                    |
| `*`      | Gym manager | I want to sort member's membership validity                   | So that I can view members in order of membership expiry date              |


### Use cases

(For all use cases below, the **System** is the `GymContactsPro` and the **Actor** is the `GymManager`, unless specified otherwise)


**Use case: UC01 : Add a gym member**

**MSS**

1. Gym Manager requests to add a new member by providing their details
2. GymContactsPro adds the new member
3. GymContactsPro displays a success message showing the newly added member's details

    Use case ends.

**Extensions**

* 1a. The provided details are in an invalid format.
  * 1a1. GymContactsPro shows an error message indicating the required format.

  Use case ends.

* 1b. The provided details correspond to an already existing member in the system.
  * 1b1. GymContactsPro rejects the addition and shows a duplicate error message.
  
  Use case ends.

**Use case: UC02 : List all gym members**

**MSS**

1. Gym Manager requests to view the list of members
2. GymContactsPro displays the complete list of members

    Use case ends.

**Extensions**

* 2a. There are no members recorded in the system.
  * 2a1. GymContactsPro shows an error message.

    Use case ends.

**Use case: UC03 : Delete a gym member**

**MSS**

1. Gym Manager requests to delete a member by providing their membership ID
2. GymContactsPro deletes the member
3. GymContactsPro displays a success message

    Use case ends.

**Extensions**

* 1a. No membership is provided or format is invalid.
  * 1a1. GymContactsPro shows an error message prompting for the correct format.
  
  Use case ends.

* 1b. No member with given membership ID exists in the system.
  * 1b1. GymContactsPro shows an error message.

  Use case ends.

**Use case : UC05 : View list of executable commands**

**MSS**

1. Gym Manager requests to view the list of executable commands
2. GymContactsPro displays the list of available executable commands and their formats

    Use case ends.

**Use case: UC05 : Find a gym member**

**MSS**

1. Gym Manager requests to find a member by specifying search attribute and the search term
2. GymContactsPro displays a list of members matching the specifies criteria

    Use case ends.

**Extensions**

* 1a. The Gym Manager does not provide a valid search format.
  * 1a1. GymContactsPro shows an error message.

    Use case ends.

* 2a. There are no members matching the provided criteria.
    * 2a1. GymContactsPro shows an error message.

    Use case ends.


**Use case: UC06 : Edit existing member details**

**MSS**

1. Gym Manager requests to edit a member by providing their membership ID
2. GymContactsPro updates the member's details
3. GymContactsPro displays a success message

   Use case ends.

**Extensions**

* 1a. No membership ID is provided or the format is invalid.
  * 1a1. GymContactsPro shows an error message prompting for the correct format.

    Use case ends.

* 3b. The provided new details are in an invalid format.
  * 3b1. GymContactsPro shows an error message corresponding to the invalid field.

    Use case ends.

* 3c. The new details create a duplicate member.
  * 3c1. GymContactsPro rejects the edit and shows a duplicate fields error message.

    Use case ends.

**Use case: UC07 : Check memberships nearing expiry**

**MSS**

1. Gym Manager requests to identify members whose memberships expire within a specified number of days
2. GymContactsPro displays a list of members expiring within that timeframe

    Use case ends.

**Extensions**

* 1a. The Gym Manager provides an invalid number of days.
  * 1a1. GymContactsPro shows an error message prompting for a valid number.

    Use case ends.

* 2a. No members have memberships expiring within the specified timeframe.
    * 2a1. GymContactsPro shows an error message.

  Use case ends.

**Use case: UC08 : Check a member's membership validity**

**MSS**

1. Gym Manager requests to check the validity of a specific membership ID
2. GymContactsPro verifies the membership ID
3. GymContactsPro displays the membership validity status and the member's details

    Use case ends.

**Extensions**

* 1a. No membership ID is provided.
  * 1a1. GymContactsPro shows an error message prompting for the correct format.

    Use case ends.

* 2a. No member with the given membership ID exists in the system.
  * 2a1. GymContactsPro shows an error message.

    Use case ends.

**Use case: UC09 : Sort members by membership expiry**

**MSS**

1. Gym Manager requests to sort members.
2. GymContactsPro displays the list of members sorted in the requested order

    Use case ends.

**Extensions**

* 1a. The Gym Manager specifies an invalid sort order.
  * 1a1. GymContactsPro shows an error message specifying the correct sort options.

    Use case ends.

* 2a. There are no members available to sort.
    * 2a1. GymContactsPro shows an error message specifying the correct sort options.

  Use case ends.


### Non-Functional Requirements

1. The product should be optimized for gym managers who are comfortable typing commands, such that frequent operations can be completed entirely via keyboard without requiring mouse interaction.
2. A new user with basic CLI familiarity should be able to learn the core commands by referring to the user guide.
3. Should work on any _mainstream OS_ such as Windows and macOS as long as it has Java `17` or above installed.
4. The product should respond to any valid command within 2 seconds when managing up to 1000 member records.
5. Should be able to hold up to 1000 persons without a noticeable lag in performance for typical usage.
6. A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
7. The application’s persistent data should be stored in a human-readable format such as JSON, so that data can be backed up, inspected, or migrated if needed.
8. The product should be fully usable without an Internet connection after installation
9. The application should be installable and usable by a single manager without requiring a database server, external hosting, or Internet access.
10. Invalid commands or inputs should not cause the application to crash. The system should display an informative error message and continue running.
11. The product is intended for single-user desktop use and is not required to support concurrent editing by multiple staff members.


### Glossary

**AB3 (AddressBook Level-3)**  
The baseline codebase from which GymContactsPro is developed. Refers to the SE-EDU AddressBook-Level3 project.

**Command**  
A text instruction entered by the Gym Manager to perform an operation in GymContactsPro.

**Command Format**  
The required structure for entering a valid command in GymContactsPro.

**Command Line Interface (CLI)**  
A text-based interface that allows the Gym Manager to interact with GymContactsPro by typing commands instead of using graphical buttons.

**Duplicate Member**  
A situation where a member being added or edited has the same identifying fields as an existing member.

**Error Message**  
A system-generated message displayed when the requested operation cannot be completed.

**Front-Desk Staff**  
Personnel working at the gym reception who manage member registrations, check-ins, and membership records using GymContactsPro.

**Gym Manager**  
The primary user of GymContactsPro who manages gym member records and memberships.

**GymContactsPro**  
A command-line based gym member management application designed to help gym managers efficiently manage member records and memberships.

**JavaFX**  
A Java GUI framework used to render the visual interface of GymContactsPro, displaying member records in a structured layout.

**JSON (JavaScript Object Notation)**  
A human-readable file format used by GymContactsPro to store member data persistently.

**Mainstream Operating Systems**  
Widely used operating systems such as Windows, macOS and Linux that GymContactsPro is designed to run on.

**Member**  
An individual registered in GymContactsPro with personal and membership information.

**Member Lookup**  
The process of searching for and retrieving a member’s record from the system using attributes such as name, phone number, or membership ID.

**Member Record**  
A stored set of information about a gym member, including personal details and membership information.

**Membership Expiry Date**  
The date on which a member’s membership becomes invalid.

**Membership ID**  
A unique identifier assigned to each gym member.

**Membership Validity**  
The period during which a member's gym membership is considered active.

**Offline Usage**  
The ability to use GymContactsPro without an Internet connection after installation.

**Persistent Storage**  
The method used by GymContactsPro to store member data so that it remains available after the application is closed and reopened.

**Prefix**  
A short identifier used before a value in a command to indicate the type of data being entered (e.g., `n/`, `p/`, `e/`).

**Search Attribute**  
A specific field (such as name, phone number, or email) used to locate members in the system.

**Sorting**  
The process of arranging members in a specific order.

**Success Message**  
A confirmation message shown after a command has been executed successfully.

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</box>

### Launch and shutdown

1. Initial launch

   1. Download the jar file and copy into an empty folder

   1. Double-click the jar file Expected: Shows the GUI with a set of sample contacts. The window size may not be optimum.

1. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   1. Re-launch the app by double-clicking the jar file.<br>
       Expected: The most recent window size and location is retained.

1. _{ more test cases …​ }_

### Deleting a person

1. Deleting a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command. Multiple persons in the list.

   1. Test case: `delete 1`<br>
      Expected: First contact is deleted from the list. Details of the deleted contact shown in the status message. Timestamp in the status bar is updated.

   1. Test case: `delete 0`<br>
      Expected: No person is deleted. Error details shown in the status message. Status bar remains the same.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

1. _{ more test cases …​ }_

### Saving data

1. Dealing with missing/corrupted data files

   1. _{explain how to simulate a missing/corrupted file, and the expected behavior}_

1. _{ more test cases …​ }_
