package com.myfinanceapp.service;

import com.myfinanceapp.model.Goal;
import com.myfinanceapp.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test class for the GoalService.
 * This class contains tests for financial goal management functionality including:
 * - Goal creation and retrieval
 * - Goal updates and modifications
 * - Goal deletion
 * - Goal persistence
 * - User-specific goal handling
 *
 * @author SE_Group110
 * @version 4.0
 */
class GoalServiceTest {

    private User testUser;
    private Goal testGoal;
    private File goalsFile;

    /**
     * Sets up the test environment before each test.
     * Initializes test user, test goal, and goals file.
     * Ensures clean test environment by removing any existing goal files.
     *
     * @throws IOException if file operations fail
     */
    @BeforeEach
    void setUp() throws IOException {
        testUser = new User();
        testUser.setUid("test-user-123");

        testGoal = new Goal(
                "goal-1",
                testUser.getUid(),
                "SAVING",
                "Test Saving Goal",
                1000.0,
                500.0,
                LocalDate.now().plusMonths(6),
                "Personal",
                "CNY"
        );

        // Define the goals file location
        goalsFile = new File("src/main/resources/goals/" + testUser.getUid() + ".json");

        // Ensure the goals directory exists and is clean
        File goalsDir = new File("src/main/resources/goals/");
        if (!goalsDir.exists()) {
            goalsDir.mkdirs();
        } else {
            // Clean up any existing file for this user
            if (goalsFile.exists()) {
                goalsFile.delete();
            }
        }
    }

    /**
     * Tests the retrieval of user goals.
     * Verifies that:
     * - Initially returns empty list
     * - Returns correct goal after adding
     * - Goal properties are correctly stored and retrieved
     *
     * @throws IOException if file operations fail
     */
    @Test
    void getUserGoals() throws IOException {
        List<Goal> goals = GoalService.getUserGoals(testUser);
        assertNotNull(goals);
        assertTrue(goals.isEmpty(), "Goals list should be empty initially");

        GoalService.addGoal(testGoal, testUser);
        goals = GoalService.getUserGoals(testUser);
        assertEquals(1, goals.size(), "Should contain one goal after adding");
        assertEquals(testGoal.getId(), goals.get(0).getId());
        assertEquals(testGoal.getTitle(), goals.get(0).getTitle());
        assertEquals(testGoal.getType(), goals.get(0).getType());
        assertEquals(testGoal.getTargetAmount(), goals.get(0).getTargetAmount(), 0.001);
    }

    /**
     * Tests the addition of a new goal.
     * Verifies that:
     * - Goal is successfully added
     * - Goal properties are correctly set
     * - Default values are properly initialized
     *
     * @throws IOException if file operations fail
     */
    @Test
    void addGoal() throws IOException {
        Goal newGoal = new Goal();
        newGoal.setType("DEBT_REPAYMENT");
        newGoal.setTitle("Pay off credit card");
        newGoal.setTargetAmount(2000.0);

        GoalService.addGoal(newGoal, testUser);
        List<Goal> goals = GoalService.getUserGoals(testUser);

        assertEquals(1, goals.size(), "Should contain one goal after adding");
        assertNotNull(goals.get(0).getId());
        assertEquals(testUser.getUid(), goals.get(0).getUserId());
        assertEquals("DEBT_REPAYMENT", goals.get(0).getType());
        assertEquals("Pay off credit card", goals.get(0).getTitle());
        assertEquals(2000.0, goals.get(0).getTargetAmount(), 0.001);
        assertEquals("CNY", goals.get(0).getCurrency());
        assertEquals(0.0, goals.get(0).getCurrentAmount(), 0.001);
    }

    /**
     * Tests the update of an existing goal.
     * Verifies that:
     * - Goal is successfully updated
     * - All properties are correctly modified
     * - Other goals remain unchanged
     *
     * @throws IOException if file operations fail
     */
    @Test
    void updateGoal() throws IOException {
        GoalService.addGoal(testGoal, testUser);

        Goal updatedGoal = new Goal(
                testGoal.getId(),
                testUser.getUid(),
                "BUDGET_CONTROL",
                "Updated Budget Goal",
                1500.0,
                750.0,
                LocalDate.now().plusMonths(3),
                "Household",
                "USD"
        );

        GoalService.updateGoal(updatedGoal, testUser);
        List<Goal> goals = GoalService.getUserGoals(testUser);

        assertEquals(1, goals.size(), "Should contain only one goal after update");
        assertEquals("BUDGET_CONTROL", goals.get(0).getType());
        assertEquals("Updated Budget Goal", goals.get(0).getTitle());
        assertEquals(1500.0, goals.get(0).getTargetAmount(), 0.001);
        assertEquals(750.0, goals.get(0).getCurrentAmount(), 0.001);
        assertEquals("Household", goals.get(0).getCategory());
        assertEquals("USD", goals.get(0).getCurrency());
    }

    /**
     * Tests the deletion of a goal.
     * Verifies that:
     * - Goal is successfully deleted
     * - Other goals remain intact
     * - Goal list is correctly updated
     *
     * @throws IOException if file operations fail
     */
    @Test
    void deleteGoal() throws IOException {
        Goal goal2 = new Goal(
                "goal-2",
                testUser.getUid(),
                "DEBT_REPAYMENT",
                "Loan Repayment",
                5000.0
        );

        GoalService.addGoal(testGoal, testUser);
        GoalService.addGoal(goal2, testUser);

        GoalService.deleteGoal(testGoal.getId(), testUser);
        List<Goal> goals = GoalService.getUserGoals(testUser);

        assertEquals(1, goals.size(), "Should contain one goal after deletion");
        assertEquals("goal-2", goals.get(0).getId());
        assertEquals("DEBT_REPAYMENT", goals.get(0).getType());
        assertEquals("Loan Repayment", goals.get(0).getTitle());
    }

    /**
     * Tests the persistence of goals to file.
     * Verifies that:
     * - Goals are successfully saved to file
     * - Goals can be loaded from file
     * - All goal properties are preserved
     *
     * @throws IOException if file operations fail
     */
    @Test
    void saveGoals() throws IOException {
        Goal goal2 = new Goal(
                "goal-2",
                testUser.getUid(),
                "BUDGET_CONTROL",
                "Monthly Budget",
                2000.0,
                1500.0,
                LocalDate.now().plusMonths(1),
                "Living Expenses",
                "CNY"
        );

        List<Goal> goalsToSave = List.of(testGoal, goal2);
        GoalService.saveGoals(goalsToSave, testUser);

        assertTrue(goalsFile.exists(), "Goals file should exist after saving");

        List<Goal> loadedGoals = GoalService.getUserGoals(testUser);
        assertEquals(2, loadedGoals.size(), "Should have loaded two goals");

        assertTrue(loadedGoals.stream().anyMatch(g ->
                g.getId().equals("goal-1") &&
                        g.getType().equals("SAVING") &&
                        g.getTitle().equals("Test Saving Goal")
        ), "First goal should be present");

        assertTrue(loadedGoals.stream().anyMatch(g ->
                g.getId().equals("goal-2") &&
                        g.getType().equals("BUDGET_CONTROL") &&
                        g.getTitle().equals("Monthly Budget")
        ), "Second goal should be present");
    }

    /**
     * Cleans up test resources after each test.
     * Removes the test goals file if it exists.
     */
    @AfterEach
    void tearDown() {
        if (goalsFile.exists()) {
            goalsFile.delete();
        }
    }
}