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

/**
 * Unit test class for the TransactionManagementScene.
 * This class contains tests for transaction management functionality including:
 * - Scene creation and initialization
 * - Transaction table creation and configuration
 * - Theme service integration
 * - Transaction data handling
 *
 * @author SE_Group110
 * @version 4.0
 */
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

    /**
     * Initializes the JavaFX environment before running tests.
     * This is required for JavaFX component testing.
     */
    @BeforeClass
    public static void setupJFX() {
        new JFXPanel();
    }

    /**
     * Sets up the test environment before each test.
     * Initializes mock objects and configures their behavior:
     * - Theme service mock with predefined styles
     * - Scene object with test dimensions
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // Configure mock object behavior
        when(mockThemeService.getCurrentThemeStyle()).thenReturn("-fx-background-color: white;");
        when(mockThemeService.getButtonStyle()).thenReturn("-fx-background-color: blue; -fx-text-fill: white;");
        when(mockThemeService.getTextColorStyle()).thenReturn("-fx-text-fill: black;");
        when(mockThemeService.getTableStyle()).thenReturn("table-style");
        when(mockThemeService.getTableHeaderStyle()).thenReturn("header-style");
        when(mockThemeService.isDayMode()).thenReturn(true);

        // Create scene object
        scene = new TransactionManagementScene(mockStage, testWidth, testHeight, mockUser);
    }

    /**
     * Tests the creation of the transaction management scene.
     * Verifies that:
     * - Scene is created successfully
     * - Transaction data is properly initialized
     * - Service integration works correctly
     * 
     * Note: This test focuses on non-UI aspects as UI testing requires JavaFX thread.
     */
    @Test
    public void testCreateScene() {
        // Prepare test data
        ObservableList<Transaction> testData = FXCollections.observableArrayList();

        // Create transaction object
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

        // Mock service behavior
        when(mockService.getFilteredTransactions()).thenReturn(filteredData);

        try {
            // Initialize scene's service reference
            Field serviceField = TransactionManagementScene.class.getDeclaredField("service");
            serviceField.setAccessible(true);
            serviceField.set(scene, mockService);

            // Create scene and verify
            Scene javaFxScene = scene.createScene(mockThemeService);
            assertNotNull("Scene should be created successfully", javaFxScene);

            // Note: Only non-UI aspects are verified as UI testing requires JavaFX thread
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    /**
     * Tests the creation of the transaction table.
     * Verifies that:
     * - Table is created successfully
     * - Table is editable
     * - Theme service is properly integrated
     * 
     * Uses reflection to access private methods and fields for testing.
     */
    @Test
    public void testCreateTransactionTable() {
        try {
            // Set up required dependencies
            Field themeServiceField = TransactionManagementScene.class.getDeclaredField("themeService");
            themeServiceField.setAccessible(true);
            themeServiceField.set(scene, mockThemeService);

            // Call private method using reflection
            Method createTableMethod = TransactionManagementScene.class.getDeclaredMethod("createTransactionTable");
            createTableMethod.setAccessible(true);
            createTableMethod.invoke(scene);

            // Get created table
            Field tableField = TransactionManagementScene.class.getDeclaredField("transactionTable");
            tableField.setAccessible(true);
            TableView<Transaction> table = (TableView<Transaction>) tableField.get(scene);

            // Verify table properties
            assertNotNull("Table should be created", table);
            assertTrue("Table should be editable", table.isEditable());
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }
}