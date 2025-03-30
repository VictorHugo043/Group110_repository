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

@ExtendWith({MockitoExtension.class, ApplicationExtension.class})
class GoalsTest {

    @Mock
    private Stage mockStage;
    
    @Mock
    private User mockUser;
    
    private Scene scene;
    

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
    
    @Test
    void createSceneWithNullUser() {
        try (MockedStatic<GoalService> mockedGoalService = Mockito.mockStatic(GoalService.class)) {
            mockedGoalService.when(() -> GoalService.getUserGoals(null))
                    .thenReturn(new ArrayList<>());
            
            scene = Goals.createScene(mockStage, 800, 600, null);
            
            assertNotNull(scene);
        }
    }
    
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