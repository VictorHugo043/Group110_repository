package com.myfinanceapp.ui.signupscene;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.ApplicationTest;
import javafx.application.Platform;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

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
class SignUpTest extends ApplicationTest {

    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
    }

    /**
     * Tests the creation of the sign up scene.
     * Verifies that:
     * - Scene is created successfully
     * - Scene is not null
     * - Scene has correct dimensions
     */
    @Test
    void createScene_shouldReturnNonNullScene() {
        Platform.runLater(() -> {
            Scene scene = SignUp.createScene(stage, 800, 450);
            assertNotNull(scene, "Scene should not be null");
            assertEquals(800, scene.getWidth(), "Scene width should match provided width");
            assertEquals(450, scene.getHeight(), "Scene height should match provided height");
        });
        waitForFxEvents();
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
        Platform.runLater(() -> {
            SignUp.createScene(stage, 800, 450);
            assertEquals(800, stage.getMinWidth(), "Stage minimum width should be set");
            assertEquals(450, stage.getMinHeight(), "Stage minimum height should be set");
            assertTrue(stage.isResizable(), "Stage should be resizable");
        });
        waitForFxEvents();
    }

    /**
     * Tests the scene structure and UI components.
     * Verifies that:
     * - Root node is properly initialized
     * - Scene contains required UI components
     * - Component hierarchy is correct
     */
    @Test
    void createScene_shouldHaveCorrectStructure() {
        Platform.runLater(() -> {
            Scene scene = SignUp.createScene(stage, 800, 450);
            
            // Verify root node
            assertNotNull(scene.getRoot(), "Root node should not be null");
            
            // Get the VBox containing the form elements
            Pane rightPane = (Pane) scene.getRoot().getChildrenUnmodifiable().get(2);
            VBox vbox = (VBox) rightPane.getChildren().get(0);
            assertNotNull(vbox, "VBox should exist");
            
            // Verify title area
            VBox titleBox = (VBox) vbox.getChildren().get(0);
            assertTrue(titleBox.getChildren().get(0) instanceof Label, "Should have main title label");
            assertTrue(titleBox.getChildren().get(1) instanceof Label, "Should have subtitle label");
            
            // Verify username and password fields
            HBox userBox = (HBox) vbox.getChildren().get(1);
            assertTrue(userBox.getChildren().get(0) instanceof Label, "Should have username label");
            assertTrue(userBox.getChildren().get(1) instanceof TextField, "Should have username field");
            
            HBox passBox = (HBox) vbox.getChildren().get(2);
            assertTrue(passBox.getChildren().get(0) instanceof Label, "Should have password label");
            assertTrue(passBox.getChildren().get(1) instanceof PasswordField, "Should have password field");
            
            // Verify security question section
            VBox questionContainer = (VBox) vbox.getChildren().get(3);
            assertTrue(questionContainer.getChildren().get(0) instanceof Label, "Should have question label");
            assertTrue(questionContainer.getChildren().get(1) instanceof ComboBox, "Should have question combo box");
            
            // Verify security answer section
            VBox answerContainer = (VBox) vbox.getChildren().get(4);
            assertTrue(answerContainer.getChildren().get(0) instanceof Label, "Should have answer label");
            assertTrue(answerContainer.getChildren().get(1) instanceof TextField, "Should have answer field");
            
            // Verify buttons and links
            assertTrue(vbox.getChildren().get(5) instanceof Button, "Should have next button");
            assertTrue(vbox.getChildren().get(6) instanceof HBox, "Should have agreement box");
            assertTrue(vbox.getChildren().get(7) instanceof Hyperlink, "Should have sign in link");
            
            // Verify agreement box contents
            HBox agreeBox = (HBox) vbox.getChildren().get(6);
            assertTrue(agreeBox.getChildren().get(0) instanceof CheckBox, "Should have agreement checkbox");
            assertTrue(agreeBox.getChildren().get(1) instanceof Hyperlink, "Should have terms link");
            assertTrue(agreeBox.getChildren().get(2) instanceof Label, "Should have 'and' label");
            assertTrue(agreeBox.getChildren().get(3) instanceof Hyperlink, "Should have privacy policy link");
        });
        waitForFxEvents();
    }
}