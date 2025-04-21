package com.myfinanceapp.ui.transactionscene;

import com.myfinanceapp.model.Transaction;
import com.myfinanceapp.model.User;
import com.myfinanceapp.service.ThemeService;
import com.myfinanceapp.service.TransactionManagementService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testfx.framework.junit.ApplicationTest;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TransactionManagementSceneTest extends ApplicationTest {

    private TransactionManagementScene scene;

    @Mock
    private Stage mockStage;

    @Mock
    private User mockUser;

    @Mock
    private ThemeService mockThemeService;

    @Mock
    private TransactionManagementService mockService;

    private double testWidth = 1600;
    private double testHeight = 900;

    @BeforeClass
    public static void setupJFX() {
        // 初始化JavaFX环境
        new JFXPanel();
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // 配置模拟对象行为
        when(mockThemeService.getCurrentThemeStyle()).thenReturn("-fx-background-color: white;");
        when(mockThemeService.getButtonStyle()).thenReturn("-fx-background-color: blue; -fx-text-fill: white;");
        when(mockThemeService.getTextColorStyle()).thenReturn("-fx-text-fill: black;");
        when(mockThemeService.getTableStyle()).thenReturn("table-style");
        when(mockThemeService.getTableHeaderStyle()).thenReturn("header-style");
        when(mockThemeService.isDayMode()).thenReturn(true);

        // 创建场景对象
        scene = new TransactionManagementScene(mockStage, testWidth, testHeight, mockUser);
    }


    @Test
    public void testCreateScene() {
        // 准备测试数据
        ObservableList<Transaction> testData = FXCollections.observableArrayList();

        // 创建交易对象
        Transaction transaction = new Transaction();
        transaction.setTransactionDate("2023-01-01");
        transaction.setTransactionType("Income");
        transaction.setCurrency("CNY");
        transaction.setAmount(1000.0);
        transaction.setCategory("Salary");
        transaction.setPaymentMethod("Bank");
        transaction.setDescription("Monthly salary");
        testData.add(transaction);

        FilteredList<Transaction> filteredData = new FilteredList<>(testData);

        // 模拟服务行为
        when(mockService.getFilteredTransactions()).thenReturn(filteredData);

        try {
            // 初始化场景的服务引用
            Field serviceField = TransactionManagementScene.class.getDeclaredField("service");
            serviceField.setAccessible(true);
            serviceField.set(scene, mockService);

            // 创建场景并验证
            Scene javaFxScene = scene.createScene(mockThemeService);
            assertNotNull("Scene should be created successfully", javaFxScene);

            // 注意：这里只验证非UI方面的功能，因为UI测试需要JavaFX线程
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    public void testCreateTransactionTable() {
        try {
            // 设置必要的依赖
            Field themeServiceField = TransactionManagementScene.class.getDeclaredField("themeService");
            themeServiceField.setAccessible(true);
            themeServiceField.set(scene, mockThemeService);

            // 使用反射调用私有方法
            Method createTableMethod = TransactionManagementScene.class.getDeclaredMethod("createTransactionTable");
            createTableMethod.setAccessible(true);
            createTableMethod.invoke(scene);

            // 获取创建的表格
            Field tableField = TransactionManagementScene.class.getDeclaredField("transactionTable");
            tableField.setAccessible(true);
            TableView<Transaction> table = (TableView<Transaction>) tableField.get(scene);

            // 验证表格属性
            assertNotNull("Table should be created", table);
            assertTrue("Table should be editable", table.isEditable());
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }
}