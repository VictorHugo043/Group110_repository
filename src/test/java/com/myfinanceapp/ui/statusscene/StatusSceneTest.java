package com.myfinanceapp.ui.statusscene;

import com.myfinanceapp.model.User;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
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
class StatusSceneTest extends ApplicationTest {

    private Stage stage;
    private User testUser;
    private StatusScene statusScene;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
    }

    @BeforeEach
    void setUp() {
        Platform.runLater(() -> {
            testUser = new User();
            testUser.setUsername("testUser");
            testUser.setPassword("password");
            statusScene = new StatusScene(stage, 800, 600, testUser);
        });
        waitForFxEvents();
    }

    @Test
    void createScene_shouldReturnNonNullScene() {
        Platform.runLater(() -> {
            Scene scene = statusScene.createScene();
            assertNotNull(scene, "Scene should not be null");
            assertEquals(800, scene.getWidth(), "Scene width should match provided width");
            assertEquals(600, scene.getHeight(), "Scene height should match provided height");
        });
        waitForFxEvents();
    }

    @Test
    void createScene_shouldHaveCorrectStructure() {
        Platform.runLater(() -> {
            Scene scene = statusScene.createScene();
            
            // 验证根节点
            assertTrue(scene.getRoot() instanceof BorderPane, "Root should be a BorderPane");
            BorderPane root = (BorderPane) scene.getRoot();
            
            // 验证左侧边栏
            assertNotNull(root.getLeft(), "Left sidebar should exist");
            
            // 验证中心区域
            assertTrue(root.getCenter() instanceof ScrollPane, "Center should be a ScrollPane");
            ScrollPane centerScrollPane = (ScrollPane) root.getCenter();
            
            // 验证主要内容
            assertTrue(centerScrollPane.getContent() instanceof VBox, "ScrollPane content should be a VBox");
            VBox mainContent = (VBox) centerScrollPane.getContent();
            
            // 验证顶部面板
            assertTrue(mainContent.getChildren().get(0) instanceof Pane, "First child should be top pane");
            
            // 验证底部区域
            assertTrue(mainContent.getChildren().get(1) instanceof HBox, "Second child should be bottom area");
            HBox bottomArea = (HBox) mainContent.getChildren().get(1);
            
            // 验证左侧列
            assertTrue(bottomArea.getChildren().get(0) instanceof VBox, "First child of bottom area should be left column");
            VBox leftColumn = (VBox) bottomArea.getChildren().get(0);
            assertEquals(2, leftColumn.getChildren().size(), "Left column should have 2 children (category and transactions)");
            
            // 验证右侧列
            assertTrue(bottomArea.getChildren().get(1) instanceof VBox, "Second child of bottom area should be right column");
            VBox rightColumn = (VBox) bottomArea.getChildren().get(1);
            assertEquals(2, rightColumn.getChildren().size(), "Right column should have 2 children (AI and suggestions)");
        });
        waitForFxEvents();
    }

    @Test
    void createScene_shouldInitializeUIComponents() {
        Platform.runLater(() -> {
            statusScene.createScene();

            // 验证日期选择器
            assertNotNull(statusScene.startDatePicker, "Start date picker should be initialized");
            assertNotNull(statusScene.endDatePicker, "End date picker should be initialized");
            
            // 验证图表类型选择器
            assertNotNull(statusScene.chartTypeCombo, "Chart type combo box should be initialized");
            assertEquals("Line graph", statusScene.chartTypeCombo.getValue(), "Default chart type should be Line graph");
            
            // 验证图表
            assertNotNull(statusScene.lineChart, "Line chart should be initialized");
            assertNotNull(statusScene.barChart, "Bar chart should be initialized");
            assertNotNull(statusScene.pieChart, "Pie chart should be initialized");
            
            // 验证 AI 助手组件
            assertNotNull(statusScene.questionArea, "Question area should be initialized");
            assertNotNull(statusScene.sendBtn, "Send button should be initialized");
            assertNotNull(statusScene.suggestionsWebView, "Suggestions web view should be initialized");
            
            // 验证交易列表
            assertNotNull(statusScene.transactionsBox, "Transactions box should be initialized");
        });
        waitForFxEvents();
    }
}