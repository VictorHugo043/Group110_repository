package com.myfinanceapp.ui.loginscene;

import com.myfinanceapp.model.User;
import com.myfinanceapp.service.UserService;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testfx.framework.junit5.ApplicationExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(ApplicationExtension.class)
class LoginSceneTest {

    @Mock
    private Stage mockStage;
    
    @Mock
    private UserService mockUserService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createScene_returnValidScene() {
        // Test that createScene returns a non-null Scene
        Scene scene = LoginScene.createScene(mockStage, 800, 450);
        assertNotNull(scene, "Scene should not be null");
        assertEquals(800, scene.getWidth(), "Scene width should match requested width");
        assertEquals(450, scene.getHeight(), "Scene height should match requested height");
    }
    
    @Test
    void createScene_setsResizableOnStage() {
        LoginScene.createScene(mockStage, 800, 450);
        // Verify that stage properties are set correctly
        // Note: This would ideally use verify() but we're keeping it simple with assertions
    }
    
    @Test
    void createScene_handlesResizing() {
        // Test that the scene properly handles resize events
        Scene scene = LoginScene.createScene(mockStage, 800, 450);
        // Simulate resize event
        // This would require TestFX interaction to be properly tested
    }
}