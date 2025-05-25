package com.myfinanceapp.ui.usermanual;

import com.myfinanceapp.ui.loginscene.LoginScene;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit test class for the UserManual scene.
 * This class contains tests for user manual functionality including:
 * - Scene creation and initialization
 * - UI component validation
 * - Layout and styling verification
 * - Markdown to HTML conversion
 * - Navigation functionality
 *
 * @author SE_Group110
 * @version 4.0
 */
@ExtendWith(ApplicationExtension.class)
public class UserManualTest {

    private Stage stage;
    private Scene scene;
    private UserManual userManual;

    /**
     * Sets up the test environment before running tests.
     * Note: Headless mode is disabled to avoid Monocle dependency.
     */
    @BeforeAll
    public static void setupHeadless() {
        // Headless mode disabled to avoid Monocle dependency
    }

    /**
     * Initializes the test scene and stage.
     * Creates and displays the user manual scene with specified dimensions.
     *
     * @param stage The primary stage for the application
     */
    @Start
    public void start(Stage stage) {
        this.stage = stage;
        userManual = new UserManual();
        Platform.runLater(() -> {
            scene = UserManual.createScene(stage, 800, 450);
            stage.setScene(scene);
            stage.show();
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    /**
     * Sets up the test environment before each test.
     * Configures the stage dimensions and waits for UI updates.
     *
     * @param robot The FxRobot instance for UI interaction
     */
    @BeforeEach
    public void setUp(FxRobot robot) {
        robot.sleep(2000);
        WaitForAsyncUtils.waitForFxEvents();
        robot.interact(() -> {
            stage.setWidth(800);
            stage.setHeight(450);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    /**
     * Tests the creation of the user manual scene.
     * Verifies that:
     * - Scene is created successfully
     * - Scene dimensions are set correctly
     *
     * @param robot The FxRobot instance for UI interaction
     */
    @Test
    public void testSceneCreation(FxRobot robot) {
        assertNotNull(scene, "Scene should not be null");
        assertEquals(800, scene.getWidth(), "Scene width should be 800");
        assertEquals(450, scene.getHeight(), "Scene height should be 450");
    }

    /**
     * Tests the content polygon in the user manual scene.
     * Verifies that:
     * - Polygon exists
     * - Fill color is white
     * - Stroke color is #93D2F3
     * - Stroke width is 2
     *
     * @param robot The FxRobot instance for UI interaction
     */
    @Test
    public void testContentPolygon(FxRobot robot) {
        Polygon contentPolygon = robot.lookup(n -> n instanceof Polygon).queryAs(Polygon.class);
        assertNotNull(contentPolygon, "Content polygon should not be null");
        assertEquals(Color.WHITE, contentPolygon.getFill(), "Polygon fill should be white");
        assertEquals(Color.web("#93D2F3"), contentPolygon.getStroke(), "Polygon stroke should be #93D2F3");
        assertEquals(2, contentPolygon.getStrokeWidth(), "Polygon stroke width should be 2");
    }

    /**
     * Tests the content pane and VBox layout.
     * Verifies that:
     * - Content pane exists
     * - VBox exists with correct spacing and padding
     *
     * @param robot The FxRobot instance for UI interaction
     */
    @Test
    public void testContentPaneAndVBox(FxRobot robot) {
        Pane contentPane = robot.lookup(n -> n instanceof Pane).queryAs(Pane.class);
        assertNotNull(contentPane, "Content pane should not be null");

        VBox vbox = robot.lookup(n -> n instanceof VBox).queryAs(VBox.class);
        assertNotNull(vbox, "VBox should not be null");
        assertEquals(10, vbox.getSpacing(), "VBox spacing should be 10");
        assertEquals(20, vbox.getPadding().getTop(), "VBox padding top should be 20");
    }

    /**
     * Tests the title label in the user manual scene.
     * Verifies that:
     * - Title label exists
     * - Text content is correct
     * - Font properties are set correctly
     * - Text color is dark blue
     *
     * @param robot The FxRobot instance for UI interaction
     */
    @Test
    public void testTitleLabel(FxRobot robot) {
        Label title = robot.lookup(n -> n instanceof Label && "Finager User Manual".equals(((Label) n).getText())).queryAs(Label.class);
        assertNotNull(title, "Title label should not be null");
        assertEquals("Finager User Manual", title.getText(), "Title text should be 'Finager User Manual'");
        assertEquals("Arial", title.getFont().getFamily(), "Title font should be Arial");
        assertEquals(24, title.getFont().getSize(), "Title font size should be 24");
        assertEquals(Color.DARKBLUE, title.getTextFill(), "Title text fill should be dark blue");
    }

    /**
     * Tests the WebView component in the user manual scene.
     * Verifies that:
     * - WebView exists
     * - Border color and width are set correctly
     *
     * @param robot The FxRobot instance for UI interaction
     */
    @Test
    public void testWebView(FxRobot robot) {
        WebView webView = robot.lookup(n -> n instanceof WebView).queryAs(WebView.class);
        assertNotNull(webView, "WebView should not be null");
        assertTrue(webView.getStyle().contains("-fx-border-color: #93D2F3"), "WebView should have correct border color");
        assertTrue(webView.getStyle().contains("-fx-border-width: 2px"), "WebView should have correct border width");
    }

    /**
     * Tests the proceed button in the user manual scene.
     * Verifies that:
     * - Button exists
     * - Text content is correct
     * - Width is set correctly
     * - Style properties are set correctly
     *
     * @param robot The FxRobot instance for UI interaction
     */
    @Test
    public void testProceedButton(FxRobot robot) {
        Button proceedButton = robot.lookup(n -> n instanceof Button && "Proceed to Login ➜".equals(((Button) n).getText())).queryAs(Button.class);
        assertNotNull(proceedButton, "Proceed button should not be null");
        assertEquals("Proceed to Login ➜", proceedButton.getText(), "Button text should be 'Proceed to Login ➜'");
        assertEquals(180, proceedButton.getPrefWidth(), "Button width should be 180");
        assertTrue(proceedButton.getStyle().contains("-fx-background-color: #3377ff"), "Button should have correct background color");
        assertTrue(proceedButton.getStyle().contains("-fx-text-fill: white"), "Button should have white text");
    }

    /**
     * Tests the proceed button action.
     * Verifies that:
     * - Button exists
     * - Button click changes stage title
     *
     * @param robot The FxRobot instance for UI interaction
     */
    @Test
    public void testProceedButtonAction(FxRobot robot) {
        Button proceedButton = robot.lookup(n -> n instanceof Button && "Proceed to Login ➜".equals(((Button) n).getText())).queryAs(Button.class);
        assertNotNull(proceedButton, "Proceed button should not be null");
        
        // 记录初始标题
        String initialTitle = stage.getTitle();
        
        // 点击按钮
        robot.clickOn(proceedButton);
        WaitForAsyncUtils.waitForFxEvents();
        
        // 验证标题已更改
        assertNotEquals(initialTitle, stage.getTitle(), "Stage title should change after button click");
    }

    /**
     * Tests the relayout functionality when window size changes.
     * Verifies that:
     * - Content polygon adjusts correctly
     * - Content pane dimensions update correctly
     * - Margins are calculated correctly
     *
     * @param robot The FxRobot instance for UI interaction
     */
    @Test
    public void testRelayout(FxRobot robot) {
        robot.interact(() -> {
            stage.setWidth(1000);
            stage.setHeight(600);
        });
        WaitForAsyncUtils.waitForFxEvents();

        Polygon contentPolygon = robot.lookup(n -> n instanceof Polygon).queryAs(Polygon.class);
        assertNotNull(contentPolygon, "Content polygon should not be null");
        Pane contentPane = robot.lookup(n -> n instanceof Pane).queryAs(Pane.class);
        assertNotNull(contentPane, "Content pane should not be null");

        // Calculate margins for polygon
        double xMargin = 0.02 * 1000; // 20
        double yMargin = 0.02 * 600;  // 12
        double[] expectedPoints = {
                xMargin, yMargin,
                1000 - xMargin, yMargin,
                1000 - xMargin, 600 - yMargin,
                xMargin, 600 - yMargin
        };

        for (int i = 0; i < expectedPoints.length; i++) {
            assertEquals(expectedPoints[i], contentPolygon.getPoints().get(i), 0.1, "Polygon point " + i + " should match");
        }

        // Verify pane dimensions
        assertEquals(xMargin, contentPane.getLayoutX(), 0.1, "Content pane X position should match");
        assertEquals(1000 - 2 * xMargin, contentPane.getPrefWidth(), 0.1, "Content pane width should match");
        assertEquals(560.0, contentPane.getPrefHeight(), 0.1, "Content pane height should match");
    }

    /**
     * Tests the Markdown to HTML conversion functionality.
     * Verifies that:
     * - Headings with IDs are converted correctly
     * - Lists are converted correctly
     * - Bold text is converted correctly
     * - Links are converted correctly
     */
    @Test
    public void testMarkdownToHtmlConversion() {
        String markdown = "# Heading {#id1}\n- Item 1\n- Item 2\n**Bold** text [Link](#id1)";
        String html = UserManual.convertMarkdownToHtml(markdown);
        assertTrue(html.contains("<h1 id=\"id1\">Heading</h1>"), "Should convert heading with ID");
        assertTrue(html.contains("<ul>"), "Should start unordered list");
        assertTrue(html.contains("<li>Item 1</li>"), "Should convert list item 1");
        assertTrue(html.contains("<li>Item 2</li>"), "Should convert list item 2");
        assertTrue(html.contains("</ul>"), "Should close unordered list");
        assertTrue(html.contains("<strong>Bold</strong>"), "Should convert bold text");
        assertTrue(html.contains("<a href=\"#id1\">Link</a>"), "Should convert link");
    }
}