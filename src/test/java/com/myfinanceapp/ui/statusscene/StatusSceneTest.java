package com.myfinanceapp.ui.statusscene;

import com.myfinanceapp.model.User;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
class StatusSceneTest {

    @Mock
    private Stage stageMock;
    
    private User testUser;
    private StatusScene statusScene;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User();
        testUser.setUsername("testUser");
        testUser.setPassword("password");
        statusScene = new StatusScene(stageMock, 800, 600, testUser);
    }

    @Test
    void createScene_shouldReturnNonNullScene() {
        Scene scene = statusScene.createScene();
        assertNotNull(scene);
    }
    
    @Test
    void createScene_shouldSetCorrectDimensions() {
        Scene scene = statusScene.createScene();
        assertEquals(800, scene.getWidth());
        assertEquals(600, scene.getHeight());
    }
    
    @Test
    void createScene_shouldInitializeUIComponents() {
        statusScene.createScene();
        
        assertNotNull(statusScene.dateCombo);
        assertNotNull(statusScene.chartTypeCombo);
        assertNotNull(statusScene.exLabel);
        assertNotNull(statusScene.inLabel);
        assertNotNull(statusScene.lineChart);
        assertNotNull(statusScene.barChart);
        assertNotNull(statusScene.pieChart);
        assertNotNull(statusScene.suggestionsArea);
        assertNotNull(statusScene.questionArea);
        assertNotNull(statusScene.sendBtn);
        assertNotNull(statusScene.transactionsBox);
        assertNotNull(statusScene.chartPane);
    }
    
    @Test
    void createScene_shouldSetDefaultValuesForComboBoxes() {
        statusScene.createScene();
        
        assertEquals("This Month", statusScene.dateCombo.getValue());
        assertEquals("Line graph", statusScene.chartTypeCombo.getValue());
        assertTrue(statusScene.dateCombo.getItems().contains("This Month"));
        assertTrue(statusScene.dateCombo.getItems().contains("Last Month"));
        assertTrue(statusScene.dateCombo.getItems().contains("All Transactions"));
        assertTrue(statusScene.chartTypeCombo.getItems().contains("Line graph"));
        assertTrue(statusScene.chartTypeCombo.getItems().contains("Bar graph"));
    }
    
    @Test
    void createScene_shouldInitializeChartPane() {
        statusScene.createScene();
        
        assertNotNull(statusScene.chartPane);
        assertEquals(1, statusScene.chartPane.getChildren().size());
        assertTrue(statusScene.chartPane.getChildren().get(0) instanceof LineChart);
    }
}