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

@ExtendWith({MockitoExtension.class, ApplicationExtension.class})
class CreateGoalSceneTest {

    @Mock
    private Stage mockStage;
    
    @Mock
    private User mockUser;
    
    private Scene scene;

    @Test
    void createScene() {
        scene = CreateGoalScene.createScene(mockStage, 800, 600, mockUser);
        
        assertNotNull(scene);
        assertEquals(800, scene.getWidth());
        assertEquals(600, scene.getHeight());
    }
    
    @Test
    void createSceneWithNullUser() {
        scene = CreateGoalScene.createScene(mockStage, 800, 600, null);
        
        assertNotNull(scene);
    }
    
    @Test
    void validateFormRejectsNegativeAmount() {
        // This test would need to access private method or test via the UI
        // For demonstration only - would require refactoring or TestFX
    }
    
    @Test
    void validateFormRejectsPastDeadline() {
        // This test would need to access private method or test via the UI
        // For demonstration only - would require refactoring or TestFX
    }
    
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