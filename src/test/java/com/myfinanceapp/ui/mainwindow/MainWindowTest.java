package com.myfinanceapp.ui.mainwindow;

import com.myfinanceapp.ui.loginscene.LoginScene;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, ApplicationExtension.class})
class MainWindowTest {

    private Stage stage;
    
    @Mock
    private Stage stageMock;

    @Start
    private void start(Stage stage) {
        this.stage = stage;
    }

    @Test
    void testStart() throws Exception {
        // Run on JavaFX thread to avoid toolkit errors
        Platform.runLater(() -> {
            try {
                // Create a MainWindow instance to test
                MainWindow mainWindow = new MainWindow();
                
                // Create a dummy scene for verification
                Scene dummyScene = new Scene(new Group(), 800, 450);
                
                // Mock LoginScene's createScene method
                try (MockedStatic<LoginScene> loginSceneMock = mockStatic(LoginScene.class)) {
                    loginSceneMock.when(() -> LoginScene.createScene(any(), anyDouble(), anyDouble()))
                            .thenReturn(dummyScene);
                    
                    // Call the start method
                    mainWindow.start(stageMock);
                    
                    // Verify stage configuration
                    verify(stageMock).setTitle("Finanger - Welcome");
                    verify(stageMock).setScene(any(Scene.class));
                    verify(stageMock).setResizable(true);
                    verify(stageMock).setMinWidth(800);
                    verify(stageMock).setMinHeight(450);
                    verify(stageMock).show();
                    
                    // Capture the scene that was set on the stage
                    ArgumentCaptor<Scene> sceneCaptor = ArgumentCaptor.forClass(Scene.class);
                    verify(stageMock).setScene(sceneCaptor.capture());
                    Scene capturedScene = sceneCaptor.getValue();
                    
                    // Verify UI initialization occurred by checking if properties exist
                    // Using reflection to access private fields
                    Field rootField = MainWindow.class.getDeclaredField("root");
                    rootField.setAccessible(true);
                    assertNotNull(rootField.get(mainWindow), "Root Group should be initialized");
                    
                    Field welcomeLabelField = MainWindow.class.getDeclaredField("welcomeLabel");
                    welcomeLabelField.setAccessible(true);
                    assertNotNull(welcomeLabelField.get(mainWindow), "Welcome label should be initialized");
                    
                    Field sloganLabelField = MainWindow.class.getDeclaredField("sloganLabel");
                    sloganLabelField.setAccessible(true);
                    assertNotNull(sloganLabelField.get(mainWindow), "Slogan label should be initialized");
                    
                    Field arrowButtonField = MainWindow.class.getDeclaredField("arrowButton");
                    arrowButtonField.setAccessible(true);
                    assertNotNull(arrowButtonField.get(mainWindow), "Arrow button should be initialized");
                }
            } catch (Exception e) {
                fail("Test failed with exception: " + e.getMessage());
            }
        });
        
        // Let JavaFX process the runLater events
        Thread.sleep(1000);
    }

    @Test
    void testMain() {
        // Testing the main method involves Application.launch which is static
        try (MockedStatic<Application> applicationMock = mockStatic(Application.class)) {
            // Call the main method
            String[] args = new String[]{"arg1", "arg2"};
            MainWindow.main(args);
            
            // Verify that launch was called with our arguments
            // The lambda should exactly match how launch is called in MainWindow.main()
            applicationMock.verify(() -> Application.launch(args));
        }
    }
}