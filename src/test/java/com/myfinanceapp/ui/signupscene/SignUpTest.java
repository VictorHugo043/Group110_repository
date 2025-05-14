package com.myfinanceapp.ui.signupscene;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
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
 * Unit test class for the SignUp scene.
 * This class contains tests for sign up functionality including:
 * - Scene creation and initialization
 * - Scene dimension validation
 * - Stage configuration verification
 * - UI component structure testing
 *
 * @author SE_Group110
 * @version 4.0
 */
@ExtendWith(ApplicationExtension.class)
class SignUpTest {

    @Mock
    private Stage stageMock;

    /**
     * Sets up the test environment before each test.
     * Initializes mock objects for Stage.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Tests the creation of the sign up scene.
     * Verifies that:
     * - Scene is created successfully
     * - Scene is not null
     */
    @Test
    void createScene_shouldReturnNonNullScene() {
        Scene scene = SignUp.createScene(stageMock, 800, 450);
        assertNotNull(scene);
    }

    /**
     * Tests the dimensions of the sign up scene.
     * Verifies that:
     * - Scene width is set correctly
     * - Scene height is set correctly
     */
    @Test
    void createScene_shouldSetCorrectDimensions() {
        Scene scene = SignUp.createScene(stageMock, 800, 450);
        assertEquals(800, scene.getWidth());
        assertEquals(450, scene.getHeight());
    }

    /**
     * Tests the stage configuration for the sign up scene.
     * Verifies that:
     * - Minimum width is set
     * - Minimum height is set
     * - Stage is resizable
     */
    @Test
    void createScene_shouldConfigureStage() {
        SignUp.createScene(stageMock, 800, 450);
        verify(stageMock).setMinWidth(800);
        verify(stageMock).setMinHeight(450);
        verify(stageMock).setResizable(true);
    }

    /**
     * Tests the scene structure with polygons.
     * Verifies that:
     * - Root node is not null
     * - Scene structure is properly initialized
     * 
     * Note: Additional assertions could be added to verify
     * specific scene structure details.
     */
    @Test
    void createScene_shouldReturnSceneWithPolygons() {
        Scene scene = SignUp.createScene(stageMock, 800, 450);
        assertNotNull(scene.getRoot());
        // Additional assertions could verify the scene structure
    }
}