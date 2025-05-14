package com.myfinanceapp.ui.common;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test class for the SceneManager.
 * This class contains tests for scene management functionality including:
 * - Scene switching
 * - Window size maintenance
 * - JavaFX event handling
 * - Stage management
 *
 * @author SE_Group110
 * @version 4.0
 */
@ExtendWith(ApplicationExtension.class)
public class SceneManagerTest {

    private Stage stage;
    private Scene originalScene;
    private Scene newScene;

    /**
     * Initializes the test environment with a JavaFX stage and scenes.
     * Sets up the initial scene and displays the stage.
     *
     * @param stage The JavaFX stage provided by TestFX
     */
    @Start
    void start(Stage stage) {
        this.stage = stage;
        this.originalScene = new Scene(new javafx.scene.layout.Pane(), 800, 600);
        this.newScene = new Scene(new javafx.scene.layout.Pane(), 400, 300);
        Platform.runLater(() -> {
            stage.setScene(originalScene);
            stage.show();
        });
        WaitForAsyncUtils.waitForFxEvents(); // Ensure JavaFX events are processed
    }

    /**
     * Tests that scene switching maintains the window size.
     * Verifies that:
     * - Initial window dimensions are preserved
     * - Scene is correctly switched
     * - Window size remains unchanged after scene switch
     */
    @Test
    void testSwitchSceneMaintainsWindowSize() {
        // Arrange
        double initialWidth = 800.0;
        double initialHeight = 600.0;

        Platform.runLater(() -> {
            stage.setWidth(initialWidth);
            stage.setHeight(initialHeight);
        });
        WaitForAsyncUtils.waitForFxEvents(); // Wait for size changes to apply

        // Act
        Platform.runLater(() -> SceneManager.switchScene(stage, newScene));
        WaitForAsyncUtils.waitForFxEvents(); // Wait for scene switch to complete

        // Assert
        assertEquals(initialWidth, stage.getWidth(), 0.1, "Window width should remain unchanged");
        assertEquals(initialHeight, stage.getHeight(), 0.1, "Window height should remain unchanged");
        assertEquals(newScene, stage.getScene(), "New scene should be set");
    }
}