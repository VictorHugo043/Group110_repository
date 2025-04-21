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

public class TransactionManagementServiceTest {

    /* --------- 仍然用 mock 的对象 --------- */
    @Mock private TransactionService mockTxService;
    @Mock private User              mockUser;

    /* --------- 用真实 JavaFX 控件 --------- */
    private TableView<Transaction>  tableView;
    private ComboBox<String> dateFilter;
    private ComboBox<String> typeFilter;
    private ComboBox<String> currencyFilter;
    private ComboBox<String> categoryFilter;
    private ComboBox<String> paymentFilter;

    private TransactionManagementService service;
    private List<Transaction> testTransactions;

    @BeforeClass
    public static void initJfx() {           // 初始化 JavaFX runtime
        new JFXPanel();
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        /* --------- 构造 2 条测试交易 --------- */
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

        /* --------- 创建真实控件并初始化选项 --------- */
        dateFilter      = buildCombo("All Date",      "2023-01-15", "2023-01-20");
        typeFilter      = buildCombo("All Type",      "Income", "Expense");
        currencyFilter  = buildCombo("All Currency",  "USD");
        categoryFilter  = buildCombo("All Category",  "Salary", "Food");
        paymentFilter   = buildCombo("All Payment Method", "Bank Transfer", "Cash");

        tableView = new TableView<>();

        /* --------- 创建待测 service --------- */
        service = new TransactionManagementService(
                mockUser, tableView,
                dateFilter, typeFilter, currencyFilter,
                categoryFilter, paymentFilter);

        // --- 把内部 txService 换成 mock ---
        Field f = TransactionManagementService.class.getDeclaredField("txService");
        f.setAccessible(true);
        f.set(service, mockTxService);

        /* 重新加载 & 初始化 ↓↓↓ */
        service.loadTransactions();      // 重新从 mockTxService 获取测试数据
        service.initializeFilters();     // 让各 ComboBox 选项同步

    }

    /* ---------- 帮助方法 ---------- */
    private ComboBox<String> buildCombo(String first, String... rest) {
        ComboBox<String> cb = new ComboBox<>();
        ObservableList<String> items = FXCollections.observableArrayList();
        items.add(first);
        items.addAll(List.of(rest));
        cb.setItems(items);
        cb.setValue(first);
        return cb;
    }

    /* =============== 测试 =============== */

    @Test
    public void loadTransactions_callsServiceOnce() {
        clearInvocations(mockTxService);
        service.loadTransactions();
        verify(mockTxService, times(1)).loadTransactions(mockUser);
    }

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

    @Test
    public void getUniqueValues_returnsDistinct() {
        List<String> dates = service.getUniqueValues(Transaction::getTransactionDate);
        assertEquals(2, dates.size());
        assertTrue(dates.contains("2023-01-15"));
        assertTrue(dates.contains("2023-01-20"));
    }

    @Test
    public void applyFilters_filtersByDate() {
        dateFilter.setValue("2023-01-15");   // 只看 15 号

        service.applyFilters();
        FilteredList<Transaction> filtered = service.getFilteredTransactions();
        assertEquals(1, filtered.size());
        assertEquals("2023-01-15", filtered.get(0).getTransactionDate());
    }

    @Test
    public void resetFilters_resetsComboValues() {
        // 改成其他值
        typeFilter.setValue("Income");
        // 调用
        service.resetFilters();

        // 断言回到 All*
        assertEquals("All Type", typeFilter.getValue());
        assertEquals("All Date", dateFilter.getValue());
    }
}
