package com.myfinanceapp.ui.transactionscene;

import com.myfinanceapp.model.User;
import com.myfinanceapp.service.TransactionService;
import com.myfinanceapp.service.AISortingService;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testfx.framework.junit5.ApplicationExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit test class for the TransactionScene.
 * This class contains tests for transaction scene functionality including:
 * - Scene creation and initialization
 * - UI component validation
 * - Manual input controls testing
 * - Auto-sorting feature verification
 * - File import functionality testing
 *
 * @author SE_Group110
 * @version 4.0
 */
@ExtendWith(ApplicationExtension.class)
class TransactionSceneTest {

    @Mock
    private Stage stageMock;

    @Mock
    private AISortingService aiSortingService;

    private User testUser;

    /**
     * Sets up the test environment before each test.
     * Initializes mock objects and creates a test user with predefined values.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User();
        testUser.setUsername("testUser");
        testUser.setPassword("password");
    }

    /**
     * Tests the creation of the transaction scene.
     * Verifies that:
     * - Scene is created successfully
     * - Scene is not null
     */
    @Test
    void createScene_shouldReturnNonNullScene() {
        Scene scene = TransactionScene.createScene(stageMock, 800, 600, testUser);
        assertNotNull(scene);
    }

    /**
     * Tests the dimensions of the transaction scene.
     * Verifies that:
     * - Scene width is set correctly
     * - Scene height is set correctly
     */
    @Test
    void createScene_shouldHaveCorrectDimensions() {
        Scene scene = TransactionScene.createScene(stageMock, 800, 600, testUser);
        assertEquals(800, scene.getWidth());
        assertEquals(600, scene.getHeight());
    }

    /**
     * Tests the root container of the transaction scene.
     * Verifies that:
     * - Root node is a BorderPane
     */
    @Test
    void createScene_shouldContainBorderPaneAsRoot() {
        Scene scene = TransactionScene.createScene(stageMock, 800, 600, testUser);
        assertTrue(scene.getRoot() instanceof BorderPane);
    }

    /**
     * Tests the left sidebar of the transaction scene.
     * Verifies that:
     * - Left section is not null
     * - Left section is a VBox
     */
    @Test
    void createScene_shouldContainLeftSidebar() {
        Scene scene = TransactionScene.createScene(stageMock, 800, 600, testUser);
        BorderPane root = (BorderPane) scene.getRoot();
        assertNotNull(root.getLeft());
        assertTrue(root.getLeft() instanceof VBox);
    }

    /**
     * Tests the center and right sections of the transaction scene.
     * Verifies that:
     * - Center section is a GridPane
     * - GridPane contains two columns
     * - First column is a VBox (centerBox)
     * - Second column is a VBox (rightBar)
     */
    @Test
    void createScene_shouldContainCenterAndRightBoxes() {
        Scene scene = TransactionScene.createScene(stageMock, 800, 600, testUser);
        BorderPane root = (BorderPane) scene.getRoot();
        assertNotNull(root.getCenter());
        assertTrue(root.getCenter() instanceof GridPane);

        GridPane centerAndRight = (GridPane) root.getCenter();
        assertEquals(2, centerAndRight.getChildren().size());
        assertTrue(centerAndRight.getChildren().get(0) instanceof VBox);
        assertTrue(centerAndRight.getChildren().get(1) instanceof VBox);
    }

    /**
     * Tests the manual input controls in the transaction scene.
     * Verifies that all required input controls are properly initialized:
     * - Date field
     * - Type combo box
     * - Currency combo box
     * - Amount field
     * - Category field
     * - Payment method field
     * - Submit button
     * Also verifies default values for combo boxes.
     */
    @Test
    void createScene_shouldInitializeManualInputControls() {
        Scene scene = TransactionScene.createScene(stageMock, 800, 600, testUser);
        BorderPane root = (BorderPane) scene.getRoot();
        GridPane centerAndRight = (GridPane) root.getCenter();
        VBox centerBox = (VBox) centerAndRight.getChildren().get(0);

        // Find manual input form controls
        TextField dateField = null;
        ComboBox<String> typeCombo = null;
        ComboBox<String> currencyCombo = null;
        TextField amountField = null;
        TextField categoryField = null;
        TextField methodField = null;
        Button submitButton = null;

        for (int i = 0; i < centerBox.getChildren().size(); i++) {
            if (centerBox.getChildren().get(i) instanceof VBox) {
                VBox item = (VBox) centerBox.getChildren().get(i);
                if (item.getChildren().size() >= 2) {
                    if (item.getChildren().get(0) instanceof Label && item.getChildren().get(1) instanceof TextField) {
                        Label label = (Label) item.getChildren().get(0);
                        if (label.getText().contains("Date")) {
                            dateField = (TextField) item.getChildren().get(1);
                        } else if (label.getText().contains("Amount")) {
                            amountField = (TextField) item.getChildren().get(1);
                        } else if (label.getText().contains("Category")) {
                            categoryField = (TextField) item.getChildren().get(1);
                        } else if (label.getText().contains("Payment")) {
                            methodField = (TextField) item.getChildren().get(1);
                        }
                    } else if (item.getChildren().get(0) instanceof Label
                            && item.getChildren().get(1) instanceof ComboBox) {
                        Label label = (Label) item.getChildren().get(0);
                        if (label.getText().contains("Type")) {
                            typeCombo = (ComboBox<String>) item.getChildren().get(1);
                        } else if (label.getText().contains("Currency")) {
                            currencyCombo = (ComboBox<String>) item.getChildren().get(1);
                        }
                    }
                }
            } else if (centerBox.getChildren().get(i) instanceof Button) {
                submitButton = (Button) centerBox.getChildren().get(i);
            }
        }

        assertNotNull(dateField, "Date field should exist");
        assertNotNull(typeCombo, "Type combo box should exist");
        assertNotNull(currencyCombo, "Currency combo box should exist");
        assertNotNull(amountField, "Amount field should exist");
        assertNotNull(categoryField, "Category field should exist");
        assertNotNull(methodField, "Method field should exist");
        assertNotNull(submitButton, "Submit button should exist");

        assertEquals("Expense", typeCombo.getValue());
        assertEquals("CNY", currencyCombo.getValue());
    }

    /**
     * Tests the auto-sorting button in the transaction scene.
     * Verifies that:
     * - Auto-sort button exists
     * - Button text is correct
     * - Button has correct style
     */
    @Test
    void createScene_shouldInitializeAutoSortingButton() {
        Scene scene = TransactionScene.createScene(stageMock, 800, 600, testUser);
        BorderPane root = (BorderPane) scene.getRoot();
        HBox centerAndRight = (HBox) root.getCenter();
        VBox centerBox = (VBox) centerAndRight.getChildren().get(0);

        Button autoSortButton = null;
        for (int i = 0; i < centerBox.getChildren().size(); i++) {
            if (centerBox.getChildren().get(i) instanceof VBox) {
                VBox item = (VBox) centerBox.getChildren().get(i);
                if (item.getChildren().size() >= 2 && item.getChildren().get(1) instanceof HBox) {
                    HBox hbox = (HBox) item.getChildren().get(1);
                    for (int j = 0; j < hbox.getChildren().size(); j++) {
                        if (hbox.getChildren().get(j) instanceof Button) {
                            autoSortButton = (Button) hbox.getChildren().get(j);
                            break;
                        }
                    }
                }
            }
        }

        assertNotNull(autoSortButton, "Auto-sort button should exist");
        assertEquals("Auto-sorting", autoSortButton.getText());
        assertTrue(autoSortButton.getStyle().contains("-fx-background-color: #E0F0FF"));
    }

    /**
     * Tests the description area in the transaction scene.
     * Verifies that:
     * - Description text area exists
     * - Description label exists
     * - Label text is correct
     * - Text area has correct prompt text
     * - Text area has correct row count
     * - Text area has word wrap enabled
     */
    @Test
    void createScene_shouldInitializeDescriptionArea() {
        Scene scene = TransactionScene.createScene(stageMock, 800, 600, testUser);
        BorderPane root = (BorderPane) scene.getRoot();
        HBox centerAndRight = (HBox) root.getCenter();
        VBox centerBox = (VBox) centerAndRight.getChildren().get(0);

        TextArea descriptionArea = null;
        Label descriptionLabel = null;

        for (int i = 0; i < centerBox.getChildren().size(); i++) {
            if (centerBox.getChildren().get(i) instanceof VBox) {
                VBox item = (VBox) centerBox.getChildren().get(i);
                if (item.getChildren().size() >= 2
                        && item.getChildren().get(1) instanceof TextArea) {
                    descriptionArea = (TextArea) item.getChildren().get(1);
                    descriptionLabel = (Label) item.getChildren().get(0);
                    break;
                }
            }
        }

        assertNotNull(descriptionArea, "Description text area should exist");
        assertNotNull(descriptionLabel, "Description label should exist");
        assertEquals("Description", descriptionLabel.getText());
        assertEquals("Enter transaction description", descriptionArea.getPromptText());
        assertEquals(3, descriptionArea.getPrefRowCount());
        assertTrue(descriptionArea.isWrapText());
    }

    /**
     * Tests the file import controls in the transaction scene.
     * Verifies that:
     * - Import button exists
     * - CSV instructions label exists
     * - Button text is correct
     */
    @Test
    void createScene_shouldInitializeFileImportControls() {
        Scene scene = TransactionScene.createScene(stageMock, 800, 600, testUser);
        BorderPane root = (BorderPane) scene.getRoot();
        GridPane centerAndRight = (GridPane) root.getCenter();
        VBox rightBar = (VBox) centerAndRight.getChildren().get(1);

        Button importButton = null;
        Label instructionsLabel = null;

        for (int i = 0; i < rightBar.getChildren().size(); i++) {
            if (rightBar.getChildren().get(i) instanceof Button) {
                importButton = (Button) rightBar.getChildren().get(i);
            } else if (rightBar.getChildren().get(i) instanceof Label
                    && ((Label) rightBar.getChildren().get(i)).getText().contains("Your .CSV file")) {
                instructionsLabel = (Label) rightBar.getChildren().get(i);
            }
        }

        assertNotNull(importButton, "Import button should exist");
        assertNotNull(instructionsLabel, "CSV instructions label should exist");
        assertEquals("Select a file", importButton.getText());
    }
}

