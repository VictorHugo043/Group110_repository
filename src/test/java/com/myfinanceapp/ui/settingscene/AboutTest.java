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

@ExtendWith(ApplicationExtension.class)
class AboutTest {

    @Mock
    private Stage mockStage;
    
    @Mock
    private User mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

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