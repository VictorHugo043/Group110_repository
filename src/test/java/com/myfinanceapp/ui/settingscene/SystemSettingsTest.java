package com.myfinanceapp.ui.settingscene;

import com.myfinanceapp.model.User;
import com.myfinanceapp.service.ThemeService;
import com.myfinanceapp.service.CurrencyService;
import com.myfinanceapp.service.LanguageService;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
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
 * Unit test class for the SystemSettings scene.
 * This class contains tests for system settings functionality including:
 * - Scene creation and initialization
 * - UI structure validation
 * - Style property verification
 * - Component hierarchy testing
 *
 * @author SE_Group110
 * @version 4.0
 */
@ExtendWith(ApplicationExtension.class)
class SystemSettingsTest extends ApplicationTest {

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
     * Tests the creation of the system settings scene.
     * Verifies that:
     * - Scene is created successfully
     * - Scene dimensions are set correctly
     * - Scene is properly initialized
     */
    @Test
    void createScene_shouldReturnNonNullScene() {
        // Arrange
        double width = 800;
        double height = 450;
        
        // Act
        Scene scene = SystemSettings.createScene(stage, width, height, user, themeService, currencyService);
        
        // Assert
        assertNotNull(scene, "Scene should not be null");
        assertEquals(width, scene.getWidth(), "Scene width should match provided width");
        assertEquals(height, scene.getHeight(), "Scene height should match provided height");
    }
    
    /**
     * Tests the style and structure of the system settings scene.
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
        Scene scene = SystemSettings.createScene(stage, width, height, user, themeService, currencyService);
        
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
        assertTrue(outerBox.getChildren().get(0) instanceof VBox, "Outer box should contain a settings form");
        
        // Verify settings form
        VBox settingsForm = (VBox) outerBox.getChildren().get(0);
        assertTrue(settingsForm.getChildren().get(0) instanceof HBox, "First child should be language selection");
        assertTrue(settingsForm.getChildren().get(1) instanceof HBox, "Second child should be theme selection");
        assertTrue(settingsForm.getChildren().get(2) instanceof HBox, "Third child should be window size selection");
        assertTrue(settingsForm.getChildren().get(3) instanceof HBox, "Fourth child should be currency selection");
        assertTrue(settingsForm.getChildren().get(4) instanceof HBox, "Fifth child should be button box");
        
        // Verify language selection
        HBox langBox = (HBox) settingsForm.getChildren().get(0);
        assertTrue(langBox.getChildren().get(1) instanceof Label, "Should have a language label");
        assertTrue(langBox.getChildren().get(2) instanceof ComboBox, "Should have a language combo box");
        
        // Verify theme selection
        HBox themeBox = (HBox) settingsForm.getChildren().get(1);
        assertTrue(themeBox.getChildren().get(1) instanceof Label, "Should have a theme label");
        assertTrue(themeBox.getChildren().get(2) instanceof ComboBox, "Should have a theme combo box");
        
        // Verify window size selection
        HBox sizeBox = (HBox) settingsForm.getChildren().get(2);
        assertTrue(sizeBox.getChildren().get(1) instanceof Label, "Should have a size label");
        assertTrue(sizeBox.getChildren().get(2) instanceof ComboBox, "Should have a size combo box");
        
        // Verify currency selection
        HBox currencyBox = (HBox) settingsForm.getChildren().get(3);
        assertTrue(currencyBox.getChildren().get(1) instanceof Label, "Should have a currency label");
        assertTrue(currencyBox.getChildren().get(2) instanceof ComboBox, "Should have a currency combo box");
    }
}