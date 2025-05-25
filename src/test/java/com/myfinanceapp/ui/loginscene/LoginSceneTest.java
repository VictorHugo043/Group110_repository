package com.myfinanceapp.ui.loginscene;

import com.myfinanceapp.model.User;
import com.myfinanceapp.service.UserService;
import com.myfinanceapp.service.CurrencyService;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.beans.value.ChangeListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit test class for the LoginScene.
 * This class contains tests for login scene functionality including:
 * - Scene creation and initialization
 * - Stage property settings
 * - UI component validation
 *
 * @author SE_Group110
 * @version 4.0
 */
@ExtendWith(ApplicationExtension.class)
class LoginSceneTest extends ApplicationTest {

    private Stage stage;
    
    @Mock
    private UserService mockUserService;
    
    @Mock
    private CurrencyService mockCurrencyService;
    
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
     * Tests the creation of the login scene.
     * Verifies that:
     * - Scene is created successfully
     * - Scene dimensions are set correctly
     * - Scene is properly initialized
     */
    @Test
    void createScene_returnValidScene() {
        // Test that createScene returns a non-null Scene
        Scene scene = LoginScene.createScene(stage, 800, 450);
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
        LoginScene.createScene(stage, 800, 450);
        // Verify that stage properties are set correctly
        assertTrue(stage.isResizable(), "Stage should be resizable");
        assertEquals(800, stage.getMinWidth(), "Minimum width should be set to 800");
        assertEquals(450, stage.getMinHeight(), "Minimum height should be set to 450");
    }
    
    /**
     * Tests scene creation with custom currency service.
     * Verifies that:
     * - Scene is created successfully with custom currency service
     * - Scene dimensions are set correctly
     */
    @Test
    void createScene_withCustomCurrencyService() {
        Scene scene = LoginScene.createScene(stage, 800, 450, mockCurrencyService);
        assertNotNull(scene, "Scene should not be null");
        assertEquals(800, scene.getWidth(), "Scene width should match requested width");
        assertEquals(450, scene.getHeight(), "Scene height should match requested height");
    }
}