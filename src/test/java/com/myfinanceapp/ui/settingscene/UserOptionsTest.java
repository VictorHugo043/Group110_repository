package com.myfinanceapp.ui.settingscene;

import com.myfinanceapp.model.User;
import com.myfinanceapp.service.ThemeService;
import com.myfinanceapp.service.CurrencyService;
import com.myfinanceapp.service.LanguageService;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test class for the UserOptions scene.
 * This class contains tests for user options functionality including:
 * - Scene creation and initialization
 * - User data validation
 * - UI component verification
 * - Error handling
 *
 * @author SE_Group110
 * @version 4.0
 */
@ExtendWith(ApplicationExtension.class)
class UserOptionsTest extends ApplicationTest {

    private Stage stage;
    private User user;
    private ThemeService themeService;
    private CurrencyService currencyService;
    private LanguageService languageService;
    
    @Override
    public void start(Stage stage) {
        this.stage = stage;
        this.user = new User("testUser", "testPass", "testQuestion", "testAnswer");
        this.themeService = new ThemeService();
        this.currencyService = new CurrencyService("CNY");
        this.languageService = LanguageService.getInstance();
    }

    /**
     * Tests scene creation with null user.
     * Verifies that:
     * - Scene creation throws IllegalStateException
     * - Error is properly handled
     */
    @Test
    void createScene_withNullUser_shouldThrowIllegalStateException() {
        assertThrows(IllegalStateException.class, () -> {
            UserOptions.createScene(stage, 800, 450, null, themeService, currencyService);
        });
    }
    
    /**
     * Tests scene creation with valid user.
     * Verifies that:
     * - Scene is created successfully
     * - Scene dimensions are set correctly
     * - Scene is properly initialized
     */
    @Test
    void createScene_withValidUser_shouldReturnNonNullScene() {
        Scene scene = UserOptions.createScene(stage, 800, 450, user, themeService, currencyService);
        assertNotNull(scene, "Scene should not be null");
        assertEquals(800, scene.getWidth(), "Scene width should match provided width");
        assertEquals(450, scene.getHeight(), "Scene height should match provided height");
    }
    
    /**
     * Tests the style and structure of the user options scene.
     * Verifies that:
     * - Root node is properly initialized
     * - Root node is of type BorderPane
     * - Scene structure is correct
     * - All required UI components are present
     */
    @Test
    void createScene_shouldHaveCorrectStyleAndStructure() {
        // Arrange
        double width = 800;
        double height = 450;
        
        // Act
        Scene scene = UserOptions.createScene(stage, width, height, user, themeService, currencyService);
        
        // Assert
        assertNotNull(scene.getRoot(), "Root node should not be null");
        assertTrue(scene.getRoot() instanceof BorderPane, "Root should be BorderPane");
        BorderPane root = (BorderPane) scene.getRoot();
        
        // Verify left sidebar
        assertNotNull(root.getLeft(), "Left sidebar should not be null");
        assertTrue(root.getLeft() instanceof VBox, "Left sidebar should be a VBox");
        
        // Verify center content
        assertNotNull(root.getCenter(), "Center content should not be null");
        assertTrue(root.getCenter() instanceof HBox, "Center content should be an HBox");
        HBox centerBox = (HBox) root.getCenter();
        assertTrue(centerBox.getChildren().get(0) instanceof VBox, "Center box should contain a VBox");
        
        // Verify container structure
        VBox container = (VBox) centerBox.getChildren().get(0);
        assertTrue(container.getChildren().get(0) instanceof HBox, "Container should have a top bar");
        assertTrue(container.getChildren().get(1) instanceof VBox, "Container should have an outer box");
        
        // Verify outer box
        VBox outerBox = (VBox) container.getChildren().get(1);
        assertTrue(outerBox.getChildren().get(0) instanceof HBox, "First child should be current username display");
        assertTrue(outerBox.getChildren().get(1) instanceof HBox, "Second child should be username change header");
        assertTrue(outerBox.getChildren().get(2) instanceof HBox, "Third child should be username change row");
        assertTrue(outerBox.getChildren().get(3) instanceof HBox, "Fourth child should be security question header");
        assertTrue(outerBox.getChildren().get(4) instanceof HBox, "Fifth child should be security question row");
        assertTrue(outerBox.getChildren().get(5) instanceof HBox, "Sixth child should be reset password row");
        
        // Verify current username display
        HBox usernameDisplay = (HBox) outerBox.getChildren().get(0);
        assertTrue(usernameDisplay.getChildren().get(0) instanceof Label, "Should have a current username label");
        
        // Verify username change section
        HBox usernameChangeRow = (HBox) outerBox.getChildren().get(2);
        assertTrue(usernameChangeRow.getChildren().get(0) instanceof TextField, "Should have a new username field");
        assertTrue(usernameChangeRow.getChildren().get(1) instanceof Button, "Should have a save button");
        
        // Verify security question section
        HBox securityQuestionRow = (HBox) outerBox.getChildren().get(4);
        assertTrue(securityQuestionRow.getChildren().get(0) instanceof VBox, "Should have a question container");
        VBox questionContainer = (VBox) securityQuestionRow.getChildren().get(0);
        assertTrue(questionContainer.getChildren().get(0) instanceof ComboBox, "Should have a question combo box");
        assertTrue(questionContainer.getChildren().get(1) instanceof Label, "Should have an answer label");
        assertTrue(questionContainer.getChildren().get(2) instanceof TextField, "Should have an answer field");
        
        // Verify reset password section
        HBox resetPasswordRow = (HBox) outerBox.getChildren().get(5);
        assertTrue(resetPasswordRow.getChildren().get(0) instanceof ImageView, "Should have a password icon");
        assertTrue(resetPasswordRow.getChildren().get(1) instanceof Label, "Should have a reset password label");
    }
}