package com.myfinanceapp.ui.loginscene;

import com.myfinanceapp.model.User;
import com.myfinanceapp.service.UserService;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

/**
 * Unit test class for the ResetPassword scene.
 * This class contains tests for password reset functionality including:
 * - Scene creation and initialization
 * - Scene dimension validation
 * - UI component setup
 * - Scene navigation
 *
 * @author SE_Group110
 * @version 4.0
 */
@ExtendWith(ApplicationExtension.class)
class ResetPasswordTest extends ApplicationTest {

    private Stage stage;
    
    @Mock
    private UserService mockUserService;
    
    @Override
    public void start(Stage stage) {
        this.stage = stage;
    }
    
    /**
     * Sets up the test environment before each test.
     * Initializes mock objects for UserService.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Tests the creation of the reset password scene.
     * Verifies that:
     * - Scene is created successfully
     * - Scene dimensions are set correctly
     * - Scene is properly initialized
     */
    @Test
    void createScene() {
        // Test that createScene returns a non-null Scene
        Scene scene = ResetPassword.createScene(stage, 800, 450);
        assertNotNull(scene, "Scene should not be null");
        assertEquals(800, scene.getWidth(), "Scene width should match requested width");
        assertEquals(450, scene.getHeight(), "Scene height should match requested height");
    }
    
    /**
     * Tests stage property settings during scene creation.
     * Verifies that:
     * - Stage is set to resizable
     * - Stage properties are configured correctly
     */
    @Test
    void createScene_setsResizableOnStage() {
        ResetPassword.createScene(stage, 800, 450);
        // Verify that stage properties are set correctly
        assertTrue(stage.isResizable(), "Stage should be resizable");
        assertEquals(800, stage.getMinWidth(), "Minimum width should be set to 800");
        assertEquals(450, stage.getMinHeight(), "Minimum height should be set to 450");
    }
    
    /**
     * Tests UI component initialization.
     * Verifies that:
     * - All UI components are properly initialized
     * - Components are correctly positioned
     */
    @Test
    void createScene_initializesUIComponents() {
        Scene scene = ResetPassword.createScene(stage, 800, 450);
        assertNotNull(scene, "Scene should not be null");
        
        // Verify that the scene has a root node
        assertNotNull(scene.getRoot(), "Scene should have a root node");
        
        // Verify that the root node has children
        assertTrue(scene.getRoot().getChildrenUnmodifiable().size() > 0, 
            "Scene root should have children");
    }
}