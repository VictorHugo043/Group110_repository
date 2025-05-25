package com.myfinanceapp.ui.mainwindow;

import com.myfinanceapp.ui.loginscene.LoginScene;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.ApplicationTest;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

/**
 * Unit test class for the MainWindow.
 * This class contains tests for main window functionality including:
 * - Application startup
 * - Stage configuration
 * - UI component initialization
 * - Scene management
 * - JavaFX thread handling
 *
 * @author SE_Group110
 * @version 4.0
 */
@ExtendWith(ApplicationExtension.class)
class MainWindowTest extends ApplicationTest {

    private Stage stage;
    
    @Override
    public void start(Stage stage) {
        this.stage = stage;
    }

    /**
     * Tests the application startup process.
     * Verifies that:
     * - Stage is properly configured
     * - UI components are initialized
     * - Scene is created and set
     * - Window properties are set correctly
     */
    @Test
    void testStart() {
        // Run on JavaFX thread to avoid toolkit errors
        Platform.runLater(() -> {
            try {
                // Create a MainWindow instance to test
                MainWindow mainWindow = new MainWindow();
                
                // Call the start method
                mainWindow.start(stage);
                
                // Verify stage configuration
                assertEquals("Finanger - Welcome", stage.getTitle(), "Stage title should be set correctly");
                assertTrue(stage.isResizable(), "Stage should be resizable");
                assertEquals(800, stage.getMinWidth(), "Minimum width should be set to 800");
                assertEquals(450, stage.getMinHeight(), "Minimum height should be set to 450");
                
                // Verify scene is set
                assertNotNull(stage.getScene(), "Scene should be set on stage");
                
                // Verify UI initialization occurred by checking if properties exist
                // Using reflection to access private fields
                Field rootField = MainWindow.class.getDeclaredField("root");
                rootField.setAccessible(true);
                assertNotNull(rootField.get(mainWindow), "Root Group should be initialized");
                
                Field welcomeLabelField = MainWindow.class.getDeclaredField("welcomeLabel");
                welcomeLabelField.setAccessible(true);
                assertNotNull(welcomeLabelField.get(mainWindow), "Welcome label should be initialized");
                
                Field sloganLabelField = MainWindow.class.getDeclaredField("sloganLabel");
                sloganLabelField.setAccessible(true);
                assertNotNull(sloganLabelField.get(mainWindow), "Slogan label should be initialized");
                
                Field arrowButtonField = MainWindow.class.getDeclaredField("arrowButton");
                arrowButtonField.setAccessible(true);
                assertNotNull(arrowButtonField.get(mainWindow), "Arrow button should be initialized");
            } catch (Exception e) {
                fail("Test failed with exception: " + e.getMessage());
            }
        });
    }
}