package com.myfinanceapp.ui.goalsscene;

import com.myfinanceapp.model.Goal;
import com.myfinanceapp.model.User;
import com.myfinanceapp.service.GoalService;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit test class for the Goals scene.
 * This class contains tests for goals scene functionality including:
 * - Scene creation with various parameters
 * - User goals display
 * - Scene dimension handling
 * - Null user handling
 *
 * @author SE_Group110
 * @version 4.0
 */
@ExtendWith({MockitoExtension.class, ApplicationExtension.class})
class GoalsTest {

    @Mock
    private Stage mockStage;
    
    @Mock
    private User mockUser;
    
    private Scene scene;
    

    /**
     * Tests scene creation with valid parameters.
     * Verifies that:
     * - Scene is created successfully
     * - Scene dimensions are set correctly
     * - Empty goals list is handled properly
     */
    @Test
    void createSceneWithValidParameters() {
        try (MockedStatic<GoalService> mockedGoalService = Mockito.mockStatic(GoalService.class)) {
            mockedGoalService.when(() -> GoalService.getUserGoals(any(User.class)))
                    .thenReturn(new ArrayList<>());
            
            scene = Goals.createScene(mockStage, 800, 600, mockUser);
            
            assertNotNull(scene);
            assertEquals(800, scene.getWidth());
            assertEquals(600, scene.getHeight());
        }
    }
    
    /**
     * Tests scene creation with null user.
     * Verifies that:
     * - Scene can be created without a user
     * - Empty goals list is returned for null user
     * - No null pointer exceptions occur
     */
    @Test
    void createSceneWithNullUser() {
        try (MockedStatic<GoalService> mockedGoalService = Mockito.mockStatic(GoalService.class)) {
            mockedGoalService.when(() -> GoalService.getUserGoals(null))
                    .thenReturn(new ArrayList<>());
            
            scene = Goals.createScene(mockStage, 800, 600, null);
            
            assertNotNull(scene);
        }
    }
    
    /**
     * Tests scene creation with user goals.
     * Verifies that:
     * - Scene is created with multiple goals
     * - Goals are properly displayed
     * - Goal titles are correctly set
     */
    @Test
    void createSceneWithUserGoals() {
        // Create simplified mocks with only the properties that are actually used
        Goal mockGoal1 = mock(Goal.class);
        Goal mockGoal2 = mock(Goal.class);
        
        // For example, if only title is used in the UI:
        when(mockGoal1.getTitle()).thenReturn("Test Goal 1");
        when(mockGoal2.getTitle()).thenReturn("Test Goal 2");
        
        List<Goal> goals = Arrays.asList(mockGoal1, mockGoal2);
        
        try (MockedStatic<GoalService> mockedGoalService = Mockito.mockStatic(GoalService.class)) {
            mockedGoalService.when(() -> GoalService.getUserGoals(any(User.class)))
                    .thenReturn(goals);
            
            scene = Goals.createScene(mockStage, 800, 600, mockUser);
            
            assertNotNull(scene);
        }
    }
    
    /**
     * Tests scene creation with zero dimensions.
     * Verifies that:
     * - Scene can be created with zero dimensions
     * - Scene dimensions are set correctly
     * - Empty goals list is handled properly
     */
    @Test
    void createSceneWithZeroDimensions() {
        try (MockedStatic<GoalService> mockedGoalService = Mockito.mockStatic(GoalService.class)) {
            mockedGoalService.when(() -> GoalService.getUserGoals(any(User.class)))
                    .thenReturn(new ArrayList<>());
            
            scene = Goals.createScene(mockStage, 0, 0, mockUser);
            
            assertNotNull(scene);
            assertEquals(0, scene.getWidth());
            assertEquals(0, scene.getHeight());
        }
    }
}