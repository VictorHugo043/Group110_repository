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

@ExtendWith(ApplicationExtension.class)
class SignUpTest {

    @Mock
    private Stage stageMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createScene_shouldReturnNonNullScene() {
        Scene scene = SignUp.createScene(stageMock, 800, 450);
        assertNotNull(scene);
    }

    @Test
    void createScene_shouldSetCorrectDimensions() {
        Scene scene = SignUp.createScene(stageMock, 800, 450);
        assertEquals(800, scene.getWidth());
        assertEquals(450, scene.getHeight());
    }

    @Test
    void createScene_shouldConfigureStage() {
        SignUp.createScene(stageMock, 800, 450);
        verify(stageMock).setMinWidth(800);
        verify(stageMock).setMinHeight(450);
        verify(stageMock).setResizable(true);
    }

    @Test
    void createScene_shouldReturnSceneWithPolygons() {
        Scene scene = SignUp.createScene(stageMock, 800, 450);
        assertNotNull(scene.getRoot());
        // Additional assertions could verify the scene structure
    }
}