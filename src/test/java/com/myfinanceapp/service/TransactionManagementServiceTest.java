package com.myfinanceapp.service;

import com.myfinanceapp.model.Transaction;
import com.myfinanceapp.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit test class for the TransactionManagementService.
 * This class contains tests for transaction management functionality including:
 * - Transaction loading and filtering
 * - Transaction updates
 * - Filter operations
 * - UI component interactions
 * - Data synchronization
 *
 * @author SE_Group110
 * @version 4.0
 */
public class TransactionManagementServiceTest {

    /* --------- Mock objects --------- */
    @Mock private TransactionService mockTxService;
    @Mock private User              mockUser;

    /* --------- Real JavaFX controls --------- */
    private TableView<Transaction>  tableView;
    private ComboBox<String> dateFilter;
    private ComboBox<String> typeFilter;
    private ComboBox<String> currencyFilter;
    private ComboBox<String> categoryFilter;
    private ComboBox<String> paymentFilter;

    private TransactionManagementService service;
    private List<Transaction> testTransactions;

    /**
     * Initializes the JavaFX runtime environment.
     * Required for testing JavaFX components.
     */
    @BeforeClass
    public static void initJfx() {
        new JFXPanel();
    }

    /**
     * Sets up the test environment before each test.
     * Initializes mock objects, test data, and UI components.
     * Configures the service with test data and initializes filters.
     *
     * @throws Exception if setup fails
     */
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        /* --------- Create test transactions --------- */
        testTransactions = new ArrayList<>();
        Transaction tx1 = new Transaction();
        tx1.setTransactionDate("2023-01-15");
        tx1.setTransactionType("Income");
        tx1.setCurrency("USD");
        tx1.setAmount(1000);
        tx1.setCategory("Salary");
        tx1.setPaymentMethod("Bank Transfer");

        Transaction tx2 = new Transaction();
        tx2.setTransactionDate("2023-01-20");
        tx2.setTransactionType("Expense");
        tx2.setCurrency("USD");
        tx2.setAmount(200);
        tx2.setCategory("Food");
        tx2.setPaymentMethod("Cash");

        testTransactions.add(tx1);
        testTransactions.add(tx2);

        when(mockTxService.loadTransactions(any(User.class)))
                .thenReturn(testTransactions);

        /* --------- Create and initialize real controls --------- */
        dateFilter      = buildCombo("All Date",      "2023-01-15", "2023-01-20");
        typeFilter      = buildCombo("All Type",      "Income", "Expense");
        currencyFilter  = buildCombo("All Currency",  "USD");
        categoryFilter  = buildCombo("All Category",  "Salary", "Food");
        paymentFilter   = buildCombo("All Payment Method", "Bank Transfer", "Cash");

        tableView = new TableView<>();

        /* --------- Create service under test --------- */
        service = new TransactionManagementService(
                mockUser, tableView,
                dateFilter, typeFilter, currencyFilter,
                categoryFilter, paymentFilter);

        // Replace internal txService with mock
        Field f = TransactionManagementService.class.getDeclaredField("txService");
        f.setAccessible(true);
        f.set(service, mockTxService);

        /* Reload and initialize */
        service.loadTransactions();      // Reload test data from mockTxService
        service.initializeFilters();     // Synchronize ComboBox options
    }

    /**
     * Helper method to create and initialize a ComboBox.
     * Sets up the ComboBox with the specified first item and additional items.
     *
     * @param first The first item in the ComboBox
     * @param rest Additional items to add to the ComboBox
     * @return Initialized ComboBox with the specified items
     */
    private ComboBox<String> buildCombo(String first, String... rest) {
        ComboBox<String> cb = new ComboBox<>();
        ObservableList<String> items = FXCollections.observableArrayList();
        items.add(first);
        items.addAll(List.of(rest));
        cb.setItems(items);
        cb.setValue(first);
        return cb;
    }

    /* =============== Tests =============== */

    /**
     * Tests that loadTransactions calls the service exactly once.
     * Verifies the interaction with the transaction service.
     */
    @Test
    public void loadTransactions_callsServiceOnce() {
        clearInvocations(mockTxService);
        service.loadTransactions();
        verify(mockTxService, times(1)).loadTransactions(mockUser);
    }

    /**
     * Tests transaction update functionality.
     * Verifies that modified transactions are saved back to the service.
     */
    @Test
    public void updateTransaction_savesBack() {
        Transaction modified = new Transaction();
        modified.setTransactionDate("2023-01-20");
        modified.setTransactionType("Expense");
        modified.setCurrency("USD");
        modified.setAmount(250);
        modified.setCategory("Food");
        modified.setPaymentMethod("Cash");

        service.updateTransaction(modified);
        verify(mockTxService).saveTransactions(eq(mockUser), any(List.class));
    }

    /**
     * Tests unique value extraction from transactions.
     * Verifies that distinct values are correctly extracted from transaction properties.
     */
    @Test
    public void getUniqueValues_returnsDistinct() {
        List<String> dates = service.getUniqueValues(Transaction::getTransactionDate);
        assertEquals(2, dates.size());
        assertTrue(dates.contains("2023-01-15"));
        assertTrue(dates.contains("2023-01-20"));
    }

    /**
     * Tests date-based filtering functionality.
     * Verifies that transactions are correctly filtered by date.
     */
    @Test
    public void applyFilters_filtersByDate() {
        dateFilter.setValue("2023-01-15");   // Filter for January 15th only

        service.applyFilters();
        FilteredList<Transaction> filtered = service.getFilteredTransactions();
        assertEquals(1, filtered.size());
        assertEquals("2023-01-15", filtered.get(0).getTransactionDate());
    }

    /**
     * Tests filter reset functionality.
     * Verifies that all filter ComboBoxes are reset to their default "All" values.
     */
    @Test
    public void resetFilters_resetsComboValues() {
        // Change to different value
        typeFilter.setValue("Income");
        // Call reset
        service.resetFilters();

        // Assert back to All*
        assertEquals("All Type", typeFilter.getValue());
        assertEquals("All Date", dateFilter.getValue());
    }
}
