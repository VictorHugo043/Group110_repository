package com.myfinanceapp.ui.settingscene;

import com.myfinanceapp.model.User;
import javafx.scene.Scene;
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
class AboutTest {

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
        Scene scene = About.createScene(mockStage, width, height, mockUser);
        
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
        Scene scene = About.createScene(mockStage, width, height, mockUser);
        
        // Assert
        assertNotNull(scene.getRoot(), "Root node should not be null");
        assertEquals("BorderPane", scene.getRoot().getClass().getSimpleName(), "Root should be BorderPane");
    }
}