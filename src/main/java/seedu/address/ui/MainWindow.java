package seedu.address.ui;

import java.util.logging.Logger;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputControl;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.Logic;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * The Main Window. Provides the basic application layout containing
 * a menu bar and space where other JavaFX elements can be placed.
 */
public class MainWindow extends UiPart<Stage> {

    private static final String FXML = "MainWindow.fxml";

    private final Logger logger = LogsCenter.getLogger(getClass());

    private Stage primaryStage;
    private Logic logic;

    // Independent Ui parts residing in this Ui container
    private PersonListPanel personListPanel;
    private ResultDisplay resultDisplay;
    private CommandBox commandBox;
    private HelpWindow helpWindow;
    private Stage clearConfirmationStage;
    private Label clearConfirmationLabel;
    private Button yesButton;
    private Button noButton;
    private boolean isProcessingClearConfirmation;
    private boolean isManualCloseClearConfirmation;

    @FXML
    private StackPane commandBoxPlaceholder;

    @FXML
    private MenuItem helpMenuItem;

    @FXML
    private StackPane personListPanelPlaceholder;

    @FXML
    private StackPane resultDisplayPlaceholder;

    @FXML
    private StackPane statusbarPlaceholder;

    /**
     * Creates a {@code MainWindow} with the given {@code Stage} and {@code Logic}.
     */
    public MainWindow(Stage primaryStage, Logic logic) {
        super(FXML, primaryStage);

        // Set dependencies
        this.primaryStage = primaryStage;
        this.logic = logic;

        // Configure the UI
        setWindowDefaultSize(logic.getGuiSettings());

        setAccelerators();

        helpWindow = new HelpWindow();
        helpWindow.getRoot().setOnHidden(event -> {
            resultDisplay.setFeedbackToUser("Closed help window");
            logger.info("Closed Help window.");
        });
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    private void setAccelerators() {
        setAccelerator(helpMenuItem, KeyCombination.valueOf("F1"));
    }

    /**
     * Sets the accelerator of a MenuItem.
     * @param keyCombination the KeyCombination value of the accelerator
     */
    private void setAccelerator(MenuItem menuItem, KeyCombination keyCombination) {
        menuItem.setAccelerator(keyCombination);

        /*
         * TODO: the code below can be removed once the bug reported here
         * https://bugs.openjdk.java.net/browse/JDK-8131666
         * is fixed in later version of SDK.
         *
         * According to the bug report, TextInputControl (TextField, TextArea) will
         * consume function-key events. Because CommandBox contains a TextField, and
         * ResultDisplay contains a TextArea, thus some accelerators (e.g F1) will
         * not work when the focus is in them because the key event is consumed by
         * the TextInputControl(s).
         *
         * For now, we add following event filter to capture such key events and open
         * help window purposely so to support accelerators even when focus is
         * in CommandBox or ResultDisplay.
         */
        getRoot().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getTarget() instanceof TextInputControl && keyCombination.match(event)) {
                menuItem.getOnAction().handle(new ActionEvent());
                event.consume();
            }
        });
    }

    /**
     * Fills up all the placeholders of this window.
     */
    void fillInnerParts() {
        personListPanel = new PersonListPanel(logic.getFilteredPersonList());
        personListPanelPlaceholder.getChildren().add(personListPanel.getRoot());

        resultDisplay = new ResultDisplay();
        resultDisplayPlaceholder.getChildren().add(resultDisplay.getRoot());

        helpWindow.getRoot().setOnHidden(event -> {
            resultDisplay.setFeedbackToUser("Closed help window");
            logger.info("Closed Help window.");
        });

        StatusBarFooter statusBarFooter = new StatusBarFooter(logic.getAddressBookFilePath());
        statusbarPlaceholder.getChildren().add(statusBarFooter.getRoot());

        commandBox = new CommandBox(this::executeCommand);
        commandBoxPlaceholder.getChildren().add(commandBox.getRoot());
    }

    /**
     * Sets the default size based on {@code guiSettings}.
     */
    private void setWindowDefaultSize(GuiSettings guiSettings) {
        primaryStage.setHeight(guiSettings.getWindowHeight());
        primaryStage.setWidth(guiSettings.getWindowWidth());
        if (guiSettings.getWindowCoordinates() != null) {
            primaryStage.setX(guiSettings.getWindowCoordinates().getX());
            primaryStage.setY(guiSettings.getWindowCoordinates().getY());
        }
    }

    /**
     * Opens the help window or focuses on it if it's already opened.
     */
    @FXML
    public void handleHelp() {
        if (!helpWindow.isShowing()) {
            helpWindow.show();
            logger.info("Opened Help window.");
            resultDisplay.setFeedbackToUser("Opened help window");
        } else if (helpWindow.getRoot().isIconified()) {
            helpWindow.getRoot().setIconified(false);
            helpWindow.focus();
            logger.info("Expanded and focused on help window.");
        } else {
            helpWindow.focus();
            logger.info("Focused on Help window.");
        }
    }

    void show() {
        primaryStage.show();
    }

    /**
     * Shows the clear confirmation window with the given message.
     * If the window has not been created, this method initializes the UI components,
     * configures keyboard shortcuts for Y/N confirmation, and sets up the stage.
     * Also disables the command box while the confirmation window is showing.
     */
    private void showClearConfirmationWindow(String message) {
        if (clearConfirmationStage == null) {
            clearConfirmationLabel = new Label();
            clearConfirmationLabel.setWrapText(true);

            yesButton = new Button("Yes");
            noButton = new Button("No");

            yesButton.setOnAction(event -> handleClearConfirmationYes());
            noButton.setOnAction(event -> handleClearConfirmationNo());

            HBox buttonBox = new HBox(10, yesButton, noButton);
            VBox root = new VBox(15, clearConfirmationLabel, buttonBox);
            root.setPrefWidth(360);
            root.setPrefHeight(200);
            root.setStyle("-fx-padding: 20;");

            Scene scene = new Scene(root);

            scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                if (isProcessingClearConfirmation) {
                    event.consume();
                    return;
                }

                if (event.getCode() == KeyCode.Y) {
                    event.consume();
                    handleClearConfirmationYes();
                } else if (event.getCode() == KeyCode.N) {
                    event.consume();
                    handleClearConfirmationNo();
                }
            });

            scene.addEventFilter(KeyEvent.KEY_TYPED, event -> {
                String character = event.getCharacter();
                if ("y".equalsIgnoreCase(character) || "n".equalsIgnoreCase(character)) {
                    event.consume();
                }
            });

            clearConfirmationStage = new Stage();
            clearConfirmationStage.setTitle("Clear Confirmation");
            clearConfirmationStage.getIcons().add(
                    new Image(getClass().getResourceAsStream("/images/warning.png"))
            );
            clearConfirmationStage.initOwner(primaryStage);
            clearConfirmationStage.initModality(Modality.APPLICATION_MODAL);
            clearConfirmationStage.setResizable(false);
            clearConfirmationStage.setScene(scene);

            clearConfirmationStage.setOnHidden(event -> {
                commandBox.enableInput();
                isProcessingClearConfirmation = false;

                if (isManualCloseClearConfirmation) {
                    logger.info("User closed clear confirmation window manually.");
                    resultDisplay.setFeedbackToUser("Clear command cancelled");
                }
            });
        }

        clearConfirmationLabel.setText(message);
        yesButton.setVisible(true);
        noButton.setVisible(true);
        yesButton.setDisable(false);
        noButton.setDisable(false);
        isProcessingClearConfirmation = false;
        isManualCloseClearConfirmation = true;

        if (!clearConfirmationStage.isShowing()) {
            commandBox.disableInput();
            logger.info("Showing clear confirmation window.");
            clearConfirmationStage.show();
        }

        Platform.runLater(() -> {
            clearConfirmationStage.requestFocus();
            clearConfirmationLabel.requestFocus();
        });
    }

    /**
     * Handles the user's confirmation to clear all data.
     * Executes the internal confirm clear command, updates the result display
     * and confirmation message, then closes the window after a short delay.
     */
    private void handleClearConfirmationYes() {
        if (isProcessingClearConfirmation) {
            return;
        }

        logger.info("User confirmed clear operation.");
        isProcessingClearConfirmation = true;
        isManualCloseClearConfirmation = false;
        yesButton.setDisable(true);
        noButton.setDisable(true);
        yesButton.setVisible(false);
        noButton.setVisible(false);

        try {
            CommandResult commandResult = logic.confirmClear();
            resultDisplay.setFeedbackToUser(commandResult.getFeedbackToUser());
            clearConfirmationLabel.setText("All data has been deleted successfully");
            closeClearConfirmationWindowAfterDelay();
        } catch (CommandException e) {
            logger.warning("Failed to clear address book: " + e.getMessage());
            resultDisplay.setFeedbackToUser(e.getMessage());
            clearConfirmationLabel.setText(e.getMessage());
            closeClearConfirmationWindowAfterDelay();
        }
    }

    /**
     * Handles the user's cancellation of the clear operation.
     * Updates the result display and confirmation message, then closes
     * the confirmation window after a short delay.
     */
    private void handleClearConfirmationNo() {
        if (isProcessingClearConfirmation) {
            return;
        }

        logger.info("User cancelled clear operation.");
        isProcessingClearConfirmation = true;
        isManualCloseClearConfirmation = false;

        resultDisplay.setFeedbackToUser("Clear command cancelled");
        clearConfirmationLabel.setText("Deletion has been cancelled");
        yesButton.setDisable(true);
        noButton.setDisable(true);
        yesButton.setVisible(false);
        noButton.setVisible(false);
        closeClearConfirmationWindowAfterDelay();
    }

    /**
     * Closes the clear confirmation window after a 2-second delay
     * and re-enables the command box input.
     */
    private void closeClearConfirmationWindowAfterDelay() {
        if (clearConfirmationStage != null && clearConfirmationStage.isShowing()) {
            PauseTransition delay = new PauseTransition(Duration.seconds(2));
            delay.setOnFinished(event -> {
                clearConfirmationStage.close();
                commandBox.enableInput();
            });
            delay.play();
        }
    }

    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        GuiSettings guiSettings = new GuiSettings(primaryStage.getWidth(), primaryStage.getHeight(),
                (int) primaryStage.getX(), (int) primaryStage.getY());
        logic.setGuiSettings(guiSettings);
        helpWindow.hide();
        primaryStage.hide();
    }

    public PersonListPanel getPersonListPanel() {
        return personListPanel;
    }

    /**
     * Executes the command and returns the result.
     *
     * @see seedu.address.logic.Logic#execute(String)
     */
    private CommandResult executeCommand(String commandText) throws CommandException, ParseException {
        try {
            CommandResult commandResult = logic.execute(commandText);
            logger.info("Result: " + commandResult.getFeedbackToUser());
            resultDisplay.setFeedbackToUser(commandResult.getFeedbackToUser());

            if (commandResult.isShowHelp()) {
                handleHelp();
            }

            if (commandResult.isExit()) {
                handleExit();
            }

            if (commandResult.isShowClearConfirmation()) {
                showClearConfirmationWindow(commandResult.getClearConfirmationMessage());
            }

            return commandResult;
        } catch (CommandException | ParseException e) {
            logger.info("An error occurred while executing command: " + commandText);
            resultDisplay.setFeedbackToUser(e.getMessage());
            throw e;
        }
    }
}
