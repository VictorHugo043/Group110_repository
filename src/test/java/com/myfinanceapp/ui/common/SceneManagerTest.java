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

@ExtendWith(ApplicationExtension.class)
public class SceneManagerTest {

    private Stage stage;
    private Scene originalScene;
    private Scene newScene;

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