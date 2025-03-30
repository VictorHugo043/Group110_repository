package com.myfinanceapp.ui.settingscene;

import com.myfinanceapp.model.User;
import com.myfinanceapp.service.UserService;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testfx.framework.junit5.ApplicationExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
class UserOptionsTest {

    @Mock
    private Stage stageMock;
    
    @Mock
    private UserService userServiceMock;
    
    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User();
        testUser.setUid("1");
        testUser.setUsername("testUser");
        testUser.setPassword("password");
        testUser.setSecurityQuestion("What is your favorite book?");
        testUser.setSecurityAnswer("Test Book");
    }

    @Test
    void createScene_withNullUser_shouldThrowIllegalStateException() {
        assertThrows(IllegalStateException.class, () -> {
            UserOptions.createScene(stageMock, 800, 450, null);
        });
    }
    
    @Test
    void createScene_withValidUser_shouldReturnNonNullScene() {
        Scene scene = UserOptions.createScene(stageMock, 800, 450, testUser);
        assertNotNull(scene);
        assertEquals(800, scene.getWidth());
        assertEquals(450, scene.getHeight());
    }
}