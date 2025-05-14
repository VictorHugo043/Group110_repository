package com.myfinanceapp.ui.statusscene;

import com.myfinanceapp.model.User;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testfx.framework.junit5.ApplicationExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test class for the StatusScene.
 * This class contains tests for status scene functionality including:
 * - Scene creation and initialization
 * - UI component validation
 * - Chart initialization and configuration
 * - Default value verification
 *
 * @author SE_Group110
 * @version 4.0
 */
@ExtendWith(ApplicationExtension.class)
class StatusSceneTest {

    @Mock
    private Stage stageMock;

    private User testUser;
    private StatusScene statusScene;

    /**
     * Sets up the test environment before each test.
     * Initializes mock objects and creates a test user with predefined values.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User();
        testUser.setUsername("testUser");
        testUser.setPassword("password");
        statusScene = new StatusScene(stageMock, 800, 600, testUser);
    }

    /**
     * Tests the creation of the status scene.
     * Verifies that:
     * - Scene is created successfully
     * - Scene is not null
     */
    @Test
    void createScene_shouldReturnNonNullScene() {
        Scene scene = statusScene.createScene();
        assertNotNull(scene);
    }

    /**
     * Tests the dimensions of the status scene.
     * Verifies that:
     * - Scene width is set correctly
     * - Scene height is set correctly
     */
    @Test
    void createScene_shouldSetCorrectDimensions() {
        Scene scene = statusScene.createScene();
        assertEquals(800, scene.getWidth());
        assertEquals(600, scene.getHeight());
    }

    /**
     * Tests the initialization of UI components in the status scene.
     * Verifies that all required components are properly initialized:
     * - Date pickers
     * - Chart type combo box
     * - Labels
     * - Charts (Line, Bar, Pie)
     * - Web view for suggestions
     * - Question area
     * - Send button
     * - Transaction box
     * - Chart pane
     */
    @Test
    void createScene_shouldInitializeUIComponents() {
        statusScene.createScene();

        assertNotNull(statusScene.startDatePicker);
        assertNotNull(statusScene.endDatePicker);
        assertNotNull(statusScene.chartTypeCombo);
        assertNotNull(statusScene.exLabel);
        assertNotNull(statusScene.inLabel);
        assertNotNull(statusScene.lineChart);
        assertNotNull(statusScene.barChart);
        assertNotNull(statusScene.pieChart);
        assertNotNull(statusScene.suggestionsWebView);
        assertNotNull(statusScene.questionArea);
        assertNotNull(statusScene.sendBtn);
        assertNotNull(statusScene.transactionsBox);
        assertNotNull(statusScene.chartPane);
    }

    /**
     * Tests the default values for combo boxes in the status scene.
     * Verifies that:
     * - Default chart type is set to "Line graph"
     * - Chart type options include both "Line graph" and "Bar graph"
     */
    @Test
    void createScene_shouldSetDefaultValuesForComboBoxes() {
        statusScene.createScene();

        assertEquals("Line graph", statusScene.chartTypeCombo.getValue());
        assertTrue(statusScene.chartTypeCombo.getItems().contains("Line graph"));
        assertTrue(statusScene.chartTypeCombo.getItems().contains("Bar graph"));
    }

    /**
     * Tests the initialization of the chart pane in the status scene.
     * Verifies that:
     * - Chart pane is not null
     * - Chart pane contains exactly one child
     * - The child is an instance of LineChart
     */
    @Test
    void createScene_shouldInitializeChartPane() {
        statusScene.createScene();

        assertNotNull(statusScene.chartPane);
        assertEquals(1, statusScene.chartPane.getChildren().size());
        assertTrue(statusScene.chartPane.getChildren().get(0) instanceof LineChart);
    }
}