package com.myfinanceapp.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class GoalTest {
    
    private Goal goal;
    private final String testId = "goal123";
    private final String testUserId = "user456";
    private final String testType = "SAVING";
    private final String testTitle = "Buy a car";
    private final double testTargetAmount = 100000.0;
    private final double testCurrentAmount = 25000.0;
    private final LocalDate testDeadline = LocalDate.of(2024, 12, 31);
    private final String testCategory = "Transportation";
    private final String testCurrency = "USD";
    
    @BeforeEach
    void setUp() {
        goal = new Goal(testId, testUserId, testType, testTitle, testTargetAmount, 
                testCurrentAmount, testDeadline, testCategory, testCurrency);
    }

    @Test
    void getId() {
        assertEquals(testId, goal.getId());
    }

    @Test
    void getUserId() {
        assertEquals(testUserId, goal.getUserId());
    }

    @Test
    void getType() {
        assertEquals(testType, goal.getType());
    }

    @Test
    void getTitle() {
        assertEquals(testTitle, goal.getTitle());
    }

    @Test
    void getTargetAmount() {
        assertEquals(testTargetAmount, goal.getTargetAmount());
    }

    @Test
    void getCurrentAmount() {
        assertEquals(testCurrentAmount, goal.getCurrentAmount());
    }

    @Test
    void getDeadline() {
        assertEquals(testDeadline, goal.getDeadline());
    }

    @Test
    void getCategory() {
        assertEquals(testCategory, goal.getCategory());
    }

    @Test
    void getCurrency() {
        assertEquals(testCurrency, goal.getCurrency());
        
        // Test default currency
        Goal goalWithDefaultCurrency = new Goal(testId, testUserId, testType, testTitle, 
                testTargetAmount, testCurrentAmount, testDeadline, testCategory, null);
        assertEquals("CNY", goalWithDefaultCurrency.getCurrency());
    }

    @Test
    void setId() {
        String newId = "newGoal123";
        goal.setId(newId);
        assertEquals(newId, goal.getId());
    }

    @Test
    void setUserId() {
        String newUserId = "newUser789";
        goal.setUserId(newUserId);
        assertEquals(newUserId, goal.getUserId());
    }

    @Test
    void setType() {
        String newType = "DEBT_REPAYMENT";
        goal.setType(newType);
        assertEquals(newType, goal.getType());
    }

    @Test
    void setTitle() {
        String newTitle = "Buy a house";
        goal.setTitle(newTitle);
        assertEquals(newTitle, goal.getTitle());
    }

    @Test
    void setTargetAmount() {
        double newAmount = 200000.0;
        goal.setTargetAmount(newAmount);
        assertEquals(newAmount, goal.getTargetAmount());
    }

    @Test
    void setCurrentAmount() {
        double newAmount = 50000.0;
        goal.setCurrentAmount(newAmount);
        assertEquals(newAmount, goal.getCurrentAmount());
    }

    @Test
    void setDeadline() {
        LocalDate newDeadline = LocalDate.of(2025, 6, 30);
        goal.setDeadline(newDeadline);
        assertEquals(newDeadline, goal.getDeadline());
    }

    @Test
    void setCategory() {
        String newCategory = "Housing";
        goal.setCategory(newCategory);
        assertEquals(newCategory, goal.getCategory());
    }

    @Test
    void setCurrency() {
        String newCurrency = "EUR";
        goal.setCurrency(newCurrency);
        assertEquals(newCurrency, goal.getCurrency());
    }

    @Test
    void getProgressPercentage() {
        // Test normal case
        assertEquals(25, goal.getProgressPercentage());
        
        // Test 0 target amount
        goal.setTargetAmount(0);
        assertEquals(0, goal.getProgressPercentage());
        
        // Test over 100% completion
        goal.setTargetAmount(10000);
        goal.setCurrentAmount(15000);
        assertEquals(100, goal.getProgressPercentage());
    }

    @Test
    void isCompleted() {
        // Test saving goal (not completed)
        assertFalse(goal.isCompleted());
        
        // Test saving goal (completed)
        goal.setCurrentAmount(goal.getTargetAmount());
        assertTrue(goal.isCompleted());
        
        // Test budget control (completed)
        goal.setType("BUDGET_CONTROL");
        goal.setCurrentAmount(goal.getTargetAmount());
        assertTrue(goal.isCompleted());
        
        // Test budget control (not completed)
        goal.setCurrentAmount(goal.getTargetAmount() + 100);
        assertFalse(goal.isCompleted());
    }
    
    @Test
    void testDefaultConstructor() {
        Goal emptyGoal = new Goal();
        assertNull(emptyGoal.getId());
        assertNull(emptyGoal.getUserId());
        assertNull(emptyGoal.getType());
        assertNull(emptyGoal.getTitle());
        assertEquals(0, emptyGoal.getTargetAmount());
        assertEquals(0, emptyGoal.getCurrentAmount());
        assertNull(emptyGoal.getDeadline());
        assertNull(emptyGoal.getCategory());
        assertEquals("CNY", emptyGoal.getCurrency());
    }
    
    @Test
    void testBasicConstructor() {
        Goal basicGoal = new Goal(testId, testUserId, testType, testTitle, testTargetAmount);
        assertEquals(testId, basicGoal.getId());
        assertEquals(testUserId, basicGoal.getUserId());
        assertEquals(testType, basicGoal.getType());
        assertEquals(testTitle, basicGoal.getTitle());
        assertEquals(testTargetAmount, basicGoal.getTargetAmount());
        assertEquals(0, basicGoal.getCurrentAmount());
        assertNull(basicGoal.getDeadline());
        assertNull(basicGoal.getCategory());
        assertEquals("CNY", basicGoal.getCurrency());
    }
}