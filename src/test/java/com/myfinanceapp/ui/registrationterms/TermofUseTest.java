package com.myfinanceapp.ui.registrationterms;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test class for the Terms of Use scene.
 * This class contains tests for terms of use functionality including:
 * - Scene creation and initialization
 * - UI component validation
 * - Text content verification
 * - Navigation button handling
 *
 * @author SE_Group110
 * @version 4.0
 */
@ExtendWith(ApplicationExtension.class)
class TermofUseTest extends ApplicationTest {

    private Stage stage;
    
    @Override
    public void start(Stage stage) {
        this.stage = stage;
    }

    /**
     * Tests the creation of the terms of use scene.
     * Verifies that:
     * - Scene is created with correct dimensions
     * - Required UI components are present
     * - Terms of use text is displayed
     * - Back button is present and functional
     */
    @Test
    void createScene() {
        // Define expected dimensions
        double width = 800;
        double height = 450;
        
        // Create the scene
        Scene scene = TermofUse.createScene(stage, width, height);
        
        // Verify scene is not null
        assertNotNull(scene, "The created scene should not be null");
        
        // Verify scene dimensions
        assertEquals(width, scene.getWidth(), "Scene width should match the provided width");
        assertEquals(height, scene.getHeight(), "Scene height should match the provided height");
        
        // Verify root node is a BorderPane
        assertTrue(scene.getRoot() instanceof BorderPane, "Scene root should be a BorderPane");
        BorderPane root = (BorderPane) scene.getRoot();
        
        // Verify top container
        assertTrue(root.getTop() instanceof VBox, "Top container should be a VBox");
        VBox topContainer = (VBox) root.getTop();
        
        // Verify logo and back button row
        assertTrue(topContainer.getChildren().get(0) instanceof HBox, "First child should be an HBox");
        HBox logoBackRow = (HBox) topContainer.getChildren().get(0);
        
        // Verify logo label
        assertTrue(logoBackRow.getChildren().get(0) instanceof Label, "First child should be a Label");
        Label logoLabel = (Label) logoBackRow.getChildren().get(0);
        assertEquals("Finanger", logoLabel.getText(), "Logo label should display 'Finanger'");
        
        // Verify back button
        assertTrue(logoBackRow.getChildren().get(2) instanceof Button, "Last child should be a Button");
        Button backBtn = (Button) logoBackRow.getChildren().get(2);
        assertEquals("Back", backBtn.getText(), "Button should display 'Back'");
        assertNotNull(backBtn.getOnAction(), "Back button should have an action handler");
        
        // Verify center content
        assertTrue(root.getCenter() instanceof ScrollPane, "Center content should be a ScrollPane");
        ScrollPane scrollPane = (ScrollPane) root.getCenter();
        assertNotNull(scrollPane.getContent(), "ScrollPane should have content");
        
        // Verify stage properties
        assertTrue(stage.isResizable(), "Stage should be resizable");
        assertEquals(800, stage.getMinWidth(), "Minimum width should be set to 800");
        assertEquals(450, stage.getMinHeight(), "Minimum height should be set to 450");
    }
}