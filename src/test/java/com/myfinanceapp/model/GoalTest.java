package com.myfinanceapp.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the Goal model.
 * This class contains unit tests for all Goal class functionality including:
 * - Getters and setters for all properties
 * - Progress calculation
 * - Goal completion status
 * - Constructor variations
 * - Default values handling
 *
 * @author SE_Group110
 * @version 4.0
 */
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
    
    /**
     * Sets up a new Goal instance before each test.
     * Initializes the goal with test data for all properties.
     */
    @BeforeEach
    void setUp() {
        goal = new Goal(testId, testUserId, testType, testTitle, testTargetAmount, 
                testCurrentAmount, testDeadline, testCategory, testCurrency);
    }

    /**
     * Tests the getId method.
     * Verifies that the goal ID is correctly returned.
     */
    @Test
    void getId() {
        assertEquals(testId, goal.getId());
    }

    /**
     * Tests the getUserId method.
     * Verifies that the user ID is correctly returned.
     */
    @Test
    void getUserId() {
        assertEquals(testUserId, goal.getUserId());
    }

    /**
     * Tests the getType method.
     * Verifies that the goal type is correctly returned.
     */
    @Test
    void getType() {
        assertEquals(testType, goal.getType());
    }

    /**
     * Tests the getTitle method.
     * Verifies that the goal title is correctly returned.
     */
    @Test
    void getTitle() {
        assertEquals(testTitle, goal.getTitle());
    }

    /**
     * Tests the getTargetAmount method.
     * Verifies that the target amount is correctly returned.
     */
    @Test
    void getTargetAmount() {
        assertEquals(testTargetAmount, goal.getTargetAmount());
    }

    /**
     * Tests the getCurrentAmount method.
     * Verifies that the current amount is correctly returned.
     */
    @Test
    void getCurrentAmount() {
        assertEquals(testCurrentAmount, goal.getCurrentAmount());
    }

    /**
     * Tests the getDeadline method.
     * Verifies that the deadline date is correctly returned.
     */
    @Test
    void getDeadline() {
        assertEquals(testDeadline, goal.getDeadline());
    }

    /**
     * Tests the getCategory method.
     * Verifies that the category is correctly returned.
     */
    @Test
    void getCategory() {
        assertEquals(testCategory, goal.getCategory());
    }

    /**
     * Tests the getCurrency method.
     * Verifies that the currency is correctly returned and tests default currency behavior.
     */
    @Test
    void getCurrency() {
        assertEquals(testCurrency, goal.getCurrency());
        
        // Test default currency
        Goal goalWithDefaultCurrency = new Goal(testId, testUserId, testType, testTitle, 
                testTargetAmount, testCurrentAmount, testDeadline, testCategory, null);
        assertEquals("CNY", goalWithDefaultCurrency.getCurrency());
    }

    /**
     * Tests the setId method.
     * Verifies that the goal ID can be updated.
     */
    @Test
    void setId() {
        String newId = "newGoal123";
        goal.setId(newId);
        assertEquals(newId, goal.getId());
    }

    /**
     * Tests the setUserId method.
     * Verifies that the user ID can be updated.
     */
    @Test
    void setUserId() {
        String newUserId = "newUser789";
        goal.setUserId(newUserId);
        assertEquals(newUserId, goal.getUserId());
    }

    /**
     * Tests the setType method.
     * Verifies that the goal type can be updated.
     */
    @Test
    void setType() {
        String newType = "DEBT_REPAYMENT";
        goal.setType(newType);
        assertEquals(newType, goal.getType());
    }

    /**
     * Tests the setTitle method.
     * Verifies that the goal title can be updated.
     */
    @Test
    void setTitle() {
        String newTitle = "Buy a house";
        goal.setTitle(newTitle);
        assertEquals(newTitle, goal.getTitle());
    }

    /**
     * Tests the setTargetAmount method.
     * Verifies that the target amount can be updated.
     */
    @Test
    void setTargetAmount() {
        double newAmount = 200000.0;
        goal.setTargetAmount(newAmount);
        assertEquals(newAmount, goal.getTargetAmount());
    }

    /**
     * Tests the setCurrentAmount method.
     * Verifies that the current amount can be updated.
     */
    @Test
    void setCurrentAmount() {
        double newAmount = 50000.0;
        goal.setCurrentAmount(newAmount);
        assertEquals(newAmount, goal.getCurrentAmount());
    }

    /**
     * Tests the setDeadline method.
     * Verifies that the deadline date can be updated.
     */
    @Test
    void setDeadline() {
        LocalDate newDeadline = LocalDate.of(2025, 6, 30);
        goal.setDeadline(newDeadline);
        assertEquals(newDeadline, goal.getDeadline());
    }

    /**
     * Tests the setCategory method.
     * Verifies that the category can be updated.
     */
    @Test
    void setCategory() {
        String newCategory = "Housing";
        goal.setCategory(newCategory);
        assertEquals(newCategory, goal.getCategory());
    }

    /**
     * Tests the setCurrency method.
     * Verifies that the currency can be updated.
     */
    @Test
    void setCurrency() {
        String newCurrency = "EUR";
        goal.setCurrency(newCurrency);
        assertEquals(newCurrency, goal.getCurrency());
    }

    /**
     * Tests the getProgressPercentage method.
     * Verifies progress calculation for:
     * - Normal case (25% progress)
     * - Zero target amount
     * - Over 100% completion
     */
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

    /**
     * Tests the isCompleted method.
     * Verifies completion status for:
     * - Saving goal (not completed)
     * - Saving goal (completed)
     * - Budget control (completed)
     * - Budget control (not completed)
     */
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
    
    /**
     * Tests the default constructor.
     * Verifies that all properties are initialized with default values.
     */
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
    
    /**
     * Tests the basic constructor.
     * Verifies that the constructor correctly initializes required properties
     * and sets default values for optional properties.
     */
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