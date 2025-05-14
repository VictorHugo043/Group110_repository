package com.myfinanceapp.ui.settingscene;

import com.myfinanceapp.model.User;
import com.myfinanceapp.service.UserService;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
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
class UserOptionsTest {

    @Mock
    private Stage stageMock;
    
    @Mock
    private UserService userServiceMock;
    
    private User testUser;

    /**
     * Sets up the test environment before each test.
     * Initializes mock objects and creates a test user with predefined values.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User();
        testUser.setUid("1");
        testUser.setUsername("testUser");
        testUser.setPassword("password");
        testUser.setSecurityQuestion("What is your favorite book?");
        testUser.setSecurityAnswer("Test Book");
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
            UserOptions.createScene(stageMock, 800, 450, null);
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
        Scene scene = UserOptions.createScene(stageMock, 800, 450, testUser);
        assertNotNull(scene);
        assertEquals(800, scene.getWidth());
        assertEquals(450, scene.getHeight());
    }
}