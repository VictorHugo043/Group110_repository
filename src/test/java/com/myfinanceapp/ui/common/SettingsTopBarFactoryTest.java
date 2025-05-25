package com.myfinanceapp.ui.common;

import com.myfinanceapp.model.User;
import com.myfinanceapp.service.CurrencyService;
import com.myfinanceapp.service.ThemeService;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test class for the SettingsTopBarFactory.
 * This class contains tests for settings top bar UI component functionality including:
 * - Top bar creation and structure
 * - Tab selection states
 * - Style and layout properties
 * - Tab order and hierarchy
 * - Common properties for all tabs
 *
 * @author SE_Group110
 * @version 4.0
 */
@ExtendWith(ApplicationExtension.class)
class SettingsTopBarFactoryTest extends ApplicationTest {

    private Stage stage;
    private User testUser;
    private ThemeService themeService;
    private CurrencyService currencyService;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        this.stage.setWidth(800);
        this.stage.setHeight(600);
        this.stage.show();
    }

    /**
     * Sets up the test environment before each test.
     * Initializes test user and services.
     */
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUid("testUser123");
        themeService = new ThemeService();
        currencyService = new CurrencyService("CNY");
    }

    /**
     * Tests the creation and properties of the settings top bar.
     * Verifies:
     * - Top bar structure and component count
     * - Tab selection states
     * - Style and layout properties
     * - Tab order and hierarchy
     * - Common properties for all tabs
     */
    @Test
    void createTopBar() throws Exception {
        // Test with different active tabs
        String[] tabOptions = {"System Settings", "User Options", "Export Report", "About"};
        
        for (String activeTab : tabOptions) {
            CountDownLatch latch = new CountDownLatch(1);
            HBox[] topBarRef = new HBox[1];

            Platform.runLater(() -> {
                try {
                    topBarRef[0] = SettingsTopBarFactory.createTopBar(stage, activeTab, testUser, themeService, currencyService);
                } finally {
                    latch.countDown();
                }
            });

            assertTrue(latch.await(5, TimeUnit.SECONDS), "Timeout waiting for top bar creation");
            WaitForAsyncUtils.waitForFxEvents();

            HBox topBar = topBarRef[0];
            
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
                String style = tabLabel.getStyle();
                
                if (tabText.equals(activeTab)) {
                    assertTrue(arrow.isVisible(), "Arrow should be visible for active tab: " + activeTab);
                    assertEquals("\u25BC", arrow.getText(), "Arrow should be a down triangle");
                    
                    // Check active tab style
                    assertTrue(style.contains("-fx-background-color: #3282FA"), 
                            "Active tab should have blue background");
                    assertTrue(style.contains("-fx-text-fill: white"), 
                            "Active tab should have white text");
                } else {
                    assertFalse(arrow.isVisible(), "Arrow should not be visible for inactive tab: " + tabText);
                    
                    // Check inactive tab style
                    assertTrue(style.contains("-fx-background-color: #E0F0FF") || 
                             style.contains("-fx-background-color: #4A6FA5"), 
                            "Inactive tab should have light blue or dark blue background");
                    assertTrue(style.contains(themeService.getTextColorStyle()), 
                            "Inactive tab should use theme service text color");
                }
                
                // Check common properties for all tabs
                assertEquals(120, tabLabel.getPrefWidth(), "Tab label width should be 120px");
                assertTrue(style.contains("-fx-border-radius: 8 8 0 0"), 
                        "Tab should have rounded top corners");
            }
            
            // Check tab order
            VBox systemSettingsTab = (VBox) topBar.getChildren().get(0);
            VBox userOptionsTab = (VBox) topBar.getChildren().get(1);
            VBox exportReportTab = (VBox) topBar.getChildren().get(2);
            VBox aboutTab = (VBox) topBar.getChildren().get(3);
            
            Label systemSettingsLabel = (Label) systemSettingsTab.getChildren().get(1);
            Label userOptionsLabel = (Label) userOptionsTab.getChildren().get(1);
            Label exportReportLabel = (Label) exportReportTab.getChildren().get(1);
            Label aboutLabel = (Label) aboutTab.getChildren().get(1);
            
            assertEquals("System Settings", systemSettingsLabel.getText(), "First tab should be System Settings");
            assertEquals("User Options", userOptionsLabel.getText(), "Second tab should be User Options");
            assertEquals("Export Report", exportReportLabel.getText(), "Third tab should be Export Report");
            assertEquals("About", aboutLabel.getText(), "Fourth tab should be About");
        }
    }
}