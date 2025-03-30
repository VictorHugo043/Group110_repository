package com.myfinanceapp.ui.registrationterms;

import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({MockitoExtension.class, ApplicationExtension.class})
class TermofUseTest {

    @Mock
    private Stage stageMock;

    @Test
    void createScene() {
        // Define expected dimensions
        double width = 800;
        double height = 450;
        
        // Create the scene
        Scene scene = TermofUse.createScene(stageMock, width, height);
        
        // Verify scene is not null
        assertNotNull(scene, "The created scene should not be null");
        
        // Verify scene dimensions
        assertEquals(width, scene.getWidth(), "Scene width should match the provided width");
        assertEquals(height, scene.getHeight(), "Scene height should match the provided height");
        
        // Verify root node is a Parent (base class for all JavaFX containers)
        Parent root = scene.getRoot();
        assertNotNull(root, "Scene root should not be null");
        
        // Extract all components from the scene for verification
        boolean hasTextArea = false;
        boolean hasBackButton = false;
        
        // Check for required components in the scene graph
        for (javafx.scene.Node node : getAllNodes(root)) {
            if (node instanceof TextArea) {
                hasTextArea = true;
                TextArea textArea = (TextArea) node;
                assertFalse(textArea.getText().isEmpty(), "Terms of use text should not be empty");
                assertFalse(textArea.isEditable(), "Terms of use text area should not be editable");
            } else if (node instanceof Button) {
                Button button = (Button) node;
                if ("Back".equals(button.getText()) || button.getText().contains("back")) {
                    hasBackButton = true;
                    // Verify the button has an event handler
                    assertNotNull(button.getOnAction(), "Back button should have an action handler");
                }
            }
        }
        
        assertTrue(hasTextArea, "Scene should contain a TextArea for the terms of use");
        assertTrue(hasBackButton, "Scene should contain a Back button");
    }
    
    /**
     * Helper method to recursively get all nodes in the scene graph
     */
    private java.util.List<javafx.scene.Node> getAllNodes(Parent root) {
        java.util.List<javafx.scene.Node> nodes = new java.util.ArrayList<>();
        addAllDescendants(root, nodes);
        return nodes;
    }
    
    private void addAllDescendants(Parent parent, java.util.List<javafx.scene.Node> nodes) {
        for (javafx.scene.Node node : parent.getChildrenUnmodifiable()) {
            nodes.add(node);
            if (node instanceof Parent)
                addAllDescendants((Parent) node, nodes);
        }
    }
}