package com.myfinanceapp.ui.settingscene;

import com.myfinanceapp.model.User;
import com.myfinanceapp.service.ThemeService;
import com.myfinanceapp.service.CurrencyService;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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
 * Unit test class for the About scene.
 * This class contains tests for about page functionality including:
 * - Scene creation and initialization
 * - Scene dimension validation
 * - UI structure verification
 * - Style property validation
 *
 * @author SE_Group110
 * @version 4.0
 */
@ExtendWith(ApplicationExtension.class)
class AboutTest extends ApplicationTest {

    private Stage stage;
    private User user;
    private ThemeService themeService;
    private CurrencyService currencyService;
    
    @Override
    public void start(Stage stage) {
        this.stage = stage;
        this.user = new User("testUser", "testPass", "testQuestion", "testAnswer");
        this.themeService = new ThemeService();
        this.currencyService = new CurrencyService("CNY");
    }

    /**
     * Tests the creation of the about scene.
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
        Scene scene = About.createScene(stage, width, height, user, themeService, currencyService);
        
        // Assert
        assertNotNull(scene, "Scene should not be null");
        assertEquals(width, scene.getWidth(), "Scene width should match provided width");
        assertEquals(height, scene.getHeight(), "Scene height should match provided height");
    }
    
    /**
     * Tests the style and structure of the about scene.
     * Verifies that:
     * - Root node is properly initialized
     * - Root node is of type BorderPane
     * - Scene structure is correct
     */
    @Test
    void createScene_shouldHaveCorrectStyleAndStructure() {
        // Arrange
        double width = 800;
        double height = 450;
        
        // Act
        Scene scene = About.createScene(stage, width, height, user, themeService, currencyService);
        
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
        
        // Verify top bar and content container
        VBox container = (VBox) centerBox.getChildren().get(0);
        assertTrue(container.getChildren().get(0) instanceof HBox, "Container should have a top bar");
        assertTrue(container.getChildren().get(1) instanceof VBox, "Container should have a content box");
        
        // Verify content box
        VBox contentBox = (VBox) container.getChildren().get(1);
        assertTrue(contentBox.getChildren().get(0) instanceof VBox, "Content box should have a VBox for content");
        
        // Verify about content
        VBox aboutContent = (VBox) contentBox.getChildren().get(0);
        assertTrue(aboutContent.getChildren().get(0) instanceof Label, "First child should be a title label");
        assertTrue(aboutContent.getChildren().get(1) instanceof ScrollPane, "Second child should be a scroll pane");
        assertTrue(aboutContent.getChildren().get(2) instanceof VBox, "Third child should be a button box");
        
        // Verify button box
        VBox buttonBox = (VBox) aboutContent.getChildren().get(2);
        assertTrue(buttonBox.getChildren().get(0) instanceof Button, "Button box should contain a button");
    }
}