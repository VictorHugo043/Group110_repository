package com.myfinanceapp.ui.transactionscene;

import com.myfinanceapp.model.User;
import javafx.scene.Scene;
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
 * Unit test class for the TransactionScene.
 * This class contains tests for transaction scene functionality including:
 * - Scene creation and initialization
 * - UI component validation
 * - Manual input controls testing
 * - Auto-sorting feature verification
 * - File import functionality testing
 *
 * @author SE_Group110
 * @version 4.0
 */
@ExtendWith(ApplicationExtension.class)
class TransactionSceneTest extends ApplicationTest {

    private Stage stage;
    private User testUser;

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
        });
        waitForFxEvents();
    }

    @Test
    void createScene_shouldReturnNonNullScene() {
        Platform.runLater(() -> {
            Scene scene = TransactionScene.createScene(stage, 800, 600, testUser);
            assertNotNull(scene, "Scene should not be null");
            assertEquals(800, scene.getWidth(), "Scene width should match provided width");
            assertEquals(600, scene.getHeight(), "Scene height should match provided height");
        });
        waitForFxEvents();
    }

    @Test
    void createScene_shouldHaveCorrectStructure() {
        Platform.runLater(() -> {
            Scene scene = TransactionScene.createScene(stage, 800, 600, testUser);
            
            // 验证根节点
            assertTrue(scene.getRoot() instanceof BorderPane, "Root should be a BorderPane");
            BorderPane root = (BorderPane) scene.getRoot();
            
            // 验证左侧边栏
            assertNotNull(root.getLeft(), "Left sidebar should exist");
            assertTrue(root.getLeft() instanceof VBox, "Left sidebar should be a VBox");
            
            // 验证中心区域
            assertTrue(root.getCenter() instanceof ScrollPane, "Center should be a ScrollPane");
            ScrollPane centerScrollPane = (ScrollPane) root.getCenter();
            
            // 验证主要内容
            assertTrue(centerScrollPane.getContent() instanceof VBox, "ScrollPane content should be a VBox");
            VBox contentWrapper = (VBox) centerScrollPane.getContent();
            
            // 验证 GridPane
            assertTrue(contentWrapper.getChildren().get(0) instanceof GridPane, "First child should be a GridPane");
            GridPane centerAndRight = (GridPane) contentWrapper.getChildren().get(0);
            
            // 验证手动输入部分
            assertTrue(centerAndRight.getChildren().get(0) instanceof VBox, "First child should be manual input VBox");
            VBox centerBox = (VBox) centerAndRight.getChildren().get(0);
            
            // 验证文件导入部分
            assertTrue(centerAndRight.getChildren().get(1) instanceof VBox, "Second child should be file import VBox");
            VBox rightBar = (VBox) centerAndRight.getChildren().get(1);
            
            // 验证手动输入部分的组件
            assertTrue(centerBox.getChildren().get(0) instanceof Label, "Should have topic label");
            assertTrue(centerBox.getChildren().get(1) instanceof VBox, "Should have date box");
            assertTrue(centerBox.getChildren().get(2) instanceof VBox, "Should have type box");
            assertTrue(centerBox.getChildren().get(3) instanceof VBox, "Should have currency box");
            assertTrue(centerBox.getChildren().get(4) instanceof VBox, "Should have amount box");
            assertTrue(centerBox.getChildren().get(5) instanceof VBox, "Should have description box");
            assertTrue(centerBox.getChildren().get(6) instanceof VBox, "Should have category box");
            assertTrue(centerBox.getChildren().get(7) instanceof VBox, "Should have method box");
            assertTrue(centerBox.getChildren().get(8) instanceof Button, "Should have submit button");
            
            // 验证文件导入部分的组件
            assertTrue(rightBar.getChildren().get(0) instanceof Label, "Should have prompt label");
            assertTrue(rightBar.getChildren().get(1) instanceof HBox, "Should have button box");
            HBox fileButtonBox = (HBox) rightBar.getChildren().get(1);
            assertTrue(fileButtonBox.getChildren().get(0) instanceof Button, "Should have import button");
            assertTrue(fileButtonBox.getChildren().get(1) instanceof Button, "Should have template button");
            
            Button importButton = (Button) fileButtonBox.getChildren().get(0);
            Button templateButton = (Button) fileButtonBox.getChildren().get(1);
            
            assertTrue(importButton.getText().contains("Select") || importButton.getText().contains("选择"), "Import button should have correct text");
            assertTrue(templateButton.getText().contains("Reference") || templateButton.getText().contains("参考"), "Template button should have correct text");
        });
        waitForFxEvents();
    }

    @Test
    void createScene_shouldInitializeManualInputControls() {
        Platform.runLater(() -> {
            Scene scene = TransactionScene.createScene(stage, 800, 600, testUser);
            BorderPane root = (BorderPane) scene.getRoot();
            ScrollPane centerScrollPane = (ScrollPane) root.getCenter();
            VBox contentWrapper = (VBox) centerScrollPane.getContent();
            GridPane centerAndRight = (GridPane) contentWrapper.getChildren().get(0);
            VBox centerBox = (VBox) centerAndRight.getChildren().get(0);
            
            // 获取各个输入控件
            VBox dateBox = (VBox) centerBox.getChildren().get(1);
            VBox typeBox = (VBox) centerBox.getChildren().get(2);
            VBox currencyBox = (VBox) centerBox.getChildren().get(3);
            VBox amountBox = (VBox) centerBox.getChildren().get(4);
            VBox descriptionBox = (VBox) centerBox.getChildren().get(5);
            VBox categoryBox = (VBox) centerBox.getChildren().get(6);
            VBox methodBox = (VBox) centerBox.getChildren().get(7);
            
            // 验证日期选择器
            assertTrue(dateBox.getChildren().get(1) instanceof DatePicker, "Should have date picker");
            
            // 验证类型选择器
            assertTrue(typeBox.getChildren().get(1) instanceof ComboBox, "Should have type combo box");
            ComboBox<String> typeCombo = (ComboBox<String>) typeBox.getChildren().get(1);
            assertEquals("Expense", typeCombo.getValue(), "Default type should be Expense");
            
            // 验证货币选择器
            assertTrue(currencyBox.getChildren().get(1) instanceof ComboBox, "Should have currency combo box");
            ComboBox<String> currencyCombo = (ComboBox<String>) currencyBox.getChildren().get(1);
            assertEquals("CNY", currencyCombo.getValue(), "Default currency should be CNY");
            
            // 验证金额输入框
            assertTrue(amountBox.getChildren().get(1) instanceof TextField, "Should have amount field");
            
            // 验证描述文本区
            assertTrue(descriptionBox.getChildren().get(1) instanceof TextArea, "Should have description area");
            
            // 验证类别输入框
            assertTrue(categoryBox.getChildren().get(1) instanceof HBox, "Should have category box with button");
            HBox categoryAndButton = (HBox) categoryBox.getChildren().get(1);
            assertTrue(categoryAndButton.getChildren().get(0) instanceof TextField, "Should have category field");
            assertTrue(categoryAndButton.getChildren().get(1) instanceof Button, "Should have auto-sort button");
            
            // 验证支付方式输入框
            assertTrue(methodBox.getChildren().get(1) instanceof TextField, "Should have method field");
        });
        waitForFxEvents();
    }
}

