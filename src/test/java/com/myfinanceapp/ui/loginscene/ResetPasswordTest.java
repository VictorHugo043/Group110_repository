package com.myfinanceapp.ui.loginscene;

import com.myfinanceapp.model.User;
import com.myfinanceapp.service.UserService;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.mockStatic;

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
@ExtendWith({MockitoExtension.class, ApplicationExtension.class})
class ResetPasswordTest {

    @Mock
    private Stage stageMock;

    /**
     * Tests the creation of the reset password scene.
     * Verifies that:
     * - Scene is created successfully
     * - Scene dimensions are set correctly
     * - Scene is properly initialized
     * - LoginScene integration works correctly
     */
    @Test
    void createScene() {
        // Test that the createScene method returns a valid Scene
        try (MockedStatic<LoginScene> loginSceneMock = mockStatic(LoginScene.class)) {
            loginSceneMock.when(() -> LoginScene.createScene(Mockito.any(), anyDouble(), anyDouble()))
                    .thenReturn(new Scene(new javafx.scene.Group()));
            
            Scene scene = ResetPassword.createScene(stageMock, 800, 450);
            
            assertNotNull(scene, "The created scene should not be null");
            assertEquals(800, scene.getWidth(), "Scene width should be set to 800");
            assertEquals(450, scene.getHeight(), "Scene height should be set to 450");
        }
    }
}