package com.myfinanceapp.ui.settingscene;

import com.myfinanceapp.model.User;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
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
class SystemSettingsTest {

    @Mock
    private Stage mockStage;
    
    @Mock
    private User mockUser;

    /**
     * Sets up the test environment before each test.
     * Initializes mock objects for Stage and User.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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
        Scene scene = SystemSettings.createScene(mockStage, width, height, mockUser);
        
        // Assert
        assertNotNull(scene, "Scene should not be null");
        assertEquals(width, scene.getWidth(), "Scene width should match provided width");
        assertEquals(height, scene.getHeight(), "Scene height should match provided height");
    }
    
    /**
     * Tests the root structure of the system settings scene.
     * Verifies that:
     * - Root node is a BorderPane
     * - Left and center sections are properly initialized
     * - Section types are correct (VBox and HBox)
     */
    @Test
    void createScene_shouldHaveCorrectRootStructure() {
        // Arrange
        double width = 800;
        double height = 450;
        
        // Act
        Scene scene = SystemSettings.createScene(mockStage, width, height, mockUser);
        
        // Assert
        assertNotNull(scene.getRoot(), "Root node should not be null");
        assertTrue(scene.getRoot() instanceof BorderPane, "Root should be BorderPane");
        BorderPane root = (BorderPane) scene.getRoot();
        assertNotNull(root.getLeft(), "Left section should not be null");
        assertNotNull(root.getCenter(), "Center section should not be null");
        assertTrue(root.getLeft() instanceof VBox, "Left section should be VBox");
        assertTrue(root.getCenter() instanceof HBox, "Center section should be HBox");
    }
    
    /**
     * Tests the style properties of the system settings scene.
     * Verifies that:
     * - Root node has correct background color
     * - Style properties are properly applied
     */
    @Test
    void createScene_shouldHaveCorrectStyle() {
        // Arrange
        double width = 800;
        double height = 450;
        
        // Act
        Scene scene = SystemSettings.createScene(mockStage, width, height, mockUser);
        
        // Assert
        BorderPane root = (BorderPane) scene.getRoot();
        assertEquals("-fx-background-color: white;", root.getStyle(), "Root should have white background");
    }
    
    /**
     * Tests the component hierarchy of the system settings scene.
     * Verifies that:
     * - Center box is properly initialized
     * - Component hierarchy is correct
     * - Child components are of correct type
     * 
     * Note: This test assumes a specific component structure
     * and may need adjustment based on actual implementation.
     */
    @Test
    void createScene_shouldIncludeSettingsComponents() {
        // Arrange
        double width = 800;
        double height = 450;
        
        // Act
        Scene scene = SystemSettings.createScene(mockStage, width, height, mockUser);
        
        // Assert
        BorderPane root = (BorderPane) scene.getRoot();
        HBox centerBox = (HBox) root.getCenter();
        assertNotNull(centerBox, "Center box should not be null");
        
        // This assumes we can access the container VBox from centerBox
        // The test may need adjustment based on actual structure
        assertEquals(1, centerBox.getChildren().size(), "Center box should have one child");
        assertTrue(centerBox.getChildren().get(0) instanceof VBox, "Container should be a VBox");
    }
}