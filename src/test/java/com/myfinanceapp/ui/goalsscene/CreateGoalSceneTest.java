package com.myfinanceapp.ui.goalsscene;

import com.myfinanceapp.model.Goal;
import com.myfinanceapp.model.User;
import com.myfinanceapp.service.GoalService;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationExtension;

import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit test class for the CreateGoalScene.
 * This class contains tests for goal creation scene functionality including:
 * - Scene creation and initialization
 * - Form validation
 * - Goal saving process
 * - User interaction handling
 *
 * @author SE_Group110
 * @version 4.0
 */
@ExtendWith({MockitoExtension.class, ApplicationExtension.class})
class CreateGoalSceneTest {

    @Mock
    private Stage mockStage;
    
    @Mock
    private User mockUser;
    
    private Scene scene;

    /**
     * Tests the creation of the goal creation scene.
     * Verifies:
     * - Scene is created successfully
     * - Scene dimensions are set correctly
     * - Scene is properly initialized
     */
    @Test
    void createScene() {
        scene = CreateGoalScene.createScene(mockStage, 800, 600, mockUser);
        
        assertNotNull(scene);
        assertEquals(800, scene.getWidth());
        assertEquals(600, scene.getHeight());
    }
    
    /**
     * Tests scene creation with null user.
     * Verifies that:
     * - Scene can be created without a user
     * - Scene is properly initialized
     * - No null pointer exceptions occur
     */
    @Test
    void createSceneWithNullUser() {
        scene = CreateGoalScene.createScene(mockStage, 800, 600, null);
        
        assertNotNull(scene);
    }
    
    /**
     * Tests form validation for negative amount input.
     * Note: This test requires TestFX framework or method refactoring.
     * Current implementation is a placeholder for demonstration.
     */
    @Test
    void validateFormRejectsNegativeAmount() {
        // This test would need to access private method or test via the UI
        // For demonstration only - would require refactoring or TestFX
    }
    
    /**
     * Tests form validation for past deadline input.
     * Note: This test requires TestFX framework or method refactoring.
     * Current implementation is a placeholder for demonstration.
     */
    @Test
    void validateFormRejectsPastDeadline() {
        // This test would need to access private method or test via the UI
        // For demonstration only - would require refactoring or TestFX
    }
    
    /**
     * Tests the goal saving process.
     * Verifies that:
     * - GoalService is called with correct parameters
     * - Goal and User objects are properly passed
     * 
     * Note: This test requires TestFX framework for proper UI interaction testing.
     * Current implementation is a placeholder for demonstration.
     *
     * @throws IOException if there is an error during goal saving
     */
    @Test
    void saveGoalCallsGoalService() throws IOException {
        // Would require TestFX or refactoring to test properly
        // For demonstration only
        try (MockedStatic<GoalService> mockedGoalService = Mockito.mockStatic(GoalService.class)) {
            mockedGoalService.when(() -> GoalService.addGoal(any(Goal.class), any(User.class)))
                    .thenAnswer(invocation -> null);
            
            // Would need TestFX to interact with UI components
        }
    }
}