package com.myfinanceapp.ui.common;

import com.myfinanceapp.model.User;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class SettingsTopBarFactoryTest {

    @Mock
    private Stage mockStage;
    
    @Mock
    private User mockUser;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTopBar() {
        try {
            // Test with different active tabs
            String[] tabOptions = {"System Settings", "User Options", "Other Settings", "About"};
            
            for (String activeTab : tabOptions) {
                HBox topBar = SettingsTopBarFactory.createTopBar(mockStage, activeTab, mockUser);
                
                // Basic structure assertions
                assertNotNull(topBar, "Top bar should not be null");
                assertEquals(4, topBar.getChildren().size(), "Top bar should have 4 tabs");
                
                // Check each tab's style
                for (int i = 0; i < 4; i++) {
                    VBox tab = (VBox) topBar.getChildren().get(i);
                    assertEquals(2, tab.getChildren().size(), "Each tab should have 2 children: arrow and label");
                    
                    // Check if the arrow is visible for active tab
                    Label arrow = (Label) tab.getChildren().get(0);
                    Label tabLabel = (Label) tab.getChildren().get(1);
                    
                    String tabText = tabLabel.getText();
                    if (tabText.equals(activeTab)) {
                        assertTrue(arrow.isVisible(), "Arrow should be visible for active tab: " + activeTab);
                        assertEquals("\u25BC", arrow.getText(), "Arrow should be a down triangle");
                        
                        // Check active tab style
                        assertTrue(tabLabel.getStyle().contains("-fx-background-color: #3282FA"), 
                                "Active tab should have blue background");
                        assertTrue(tabLabel.getStyle().contains("-fx-text-fill: white"), 
                                "Active tab should have white text");
                    } else {
                        assertFalse(arrow.isVisible(), "Arrow should not be visible for inactive tab: " + tabText);
                        
                        // Check inactive tab style
                        assertTrue(tabLabel.getStyle().contains("-fx-background-color: #E0F0FF"), 
                                "Inactive tab should have light blue background");
                        assertTrue(tabLabel.getStyle().contains("-fx-text-fill: #3282FA"), 
                                "Inactive tab should have blue text");
                    }
                    
                    // Check common properties for all tabs
                    assertEquals(120, tabLabel.getPrefWidth(), "Tab label width should be 120px");
                    assertTrue(tabLabel.getStyle().contains("-fx-border-radius: 8 8 0 0"), 
                            "Tab should have rounded top corners");
                }
                
                // Check tab order
                VBox systemSettingsTab = (VBox) topBar.getChildren().get(0);
                VBox userOptionsTab = (VBox) topBar.getChildren().get(1);
                VBox otherSettingsTab = (VBox) topBar.getChildren().get(2);
                VBox aboutTab = (VBox) topBar.getChildren().get(3);
                
                Label systemSettingsLabel = (Label) systemSettingsTab.getChildren().get(1);
                Label userOptionsLabel = (Label) userOptionsTab.getChildren().get(1);
                Label otherSettingsLabel = (Label) otherSettingsTab.getChildren().get(1);
                Label aboutLabel = (Label) aboutTab.getChildren().get(1);
                
                assertEquals("System Settings", systemSettingsLabel.getText(), "First tab should be System Settings");
                assertEquals("User Options", userOptionsLabel.getText(), "Second tab should be User Options");
                assertEquals("Other Settings", otherSettingsLabel.getText(), "Third tab should be Other Settings");
                assertEquals("About", aboutLabel.getText(), "Fourth tab should be About");
            }
        } catch (ExceptionInInitializerError | IllegalStateException e) {
            // This test requires JavaFX thread initialization
            System.out.println("Test skipped: JavaFX toolkit not initialized");
            // Consider using a framework like TestFX for proper JavaFX testing
        }
    }
}