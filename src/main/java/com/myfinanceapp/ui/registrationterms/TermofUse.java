package com.myfinanceapp.ui.registrationterms;

import com.myfinanceapp.ui.signupscene.SignUp;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * A terms of use display interface for the Finanger application.
 * This scene presents the application's terms of use in a scrollable text area
 * with a professional layout including a header and navigation controls.
 *
 * @author SE_Group110
 * @version 4.0
 */
public class TermofUse {
    private static final double INITIAL_WIDTH = 800;
    private static final double INITIAL_HEIGHT = 450;

    /**
     * Creates and returns a terms of use scene.
     * The scene displays the terms of use text in a scrollable area with
     * navigation controls and proper formatting.
     *
     * @param stage The stage to display the scene
     * @param width The initial width of the scene
     * @param height The initial height of the scene
     * @return A configured Scene object for the terms of use interface
     */
    public static Scene createScene(Stage stage, double width, double height) {
        BorderPane root = new BorderPane();
        root.setPrefSize(width, height);

        // Set minimum window dimensions
        stage.setMinWidth(INITIAL_WIDTH);
        stage.setMinHeight(INITIAL_HEIGHT);
        stage.setResizable(true);

        // Top area
        VBox topContainer = new VBox();
        topContainer.setPadding(new Insets(10, 15, 0, 15));
        topContainer.setSpacing(5);

        // Row 1: Left LOGO and right Back button
        HBox logoBackRow = new HBox();
        logoBackRow.setAlignment(Pos.CENTER_LEFT);

        Label logoLabel = new Label("Finanger");
        logoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        logoLabel.setStyle("-fx-text-fill: #459af7;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button backBtn = new Button("back");
        backBtn.setStyle("-fx-background-color: #BEE3F8; -fx-text-fill: black;");
        backBtn.setOnAction(e -> {
            Scene signUpScene = SignUp.createScene(stage, root.getScene().getWidth(), root.getScene().getHeight());
            stage.setScene(signUpScene);
            stage.setTitle("Sign Up");
        });
        logoBackRow.getChildren().addAll(logoLabel, spacer, backBtn);

        // Row 2: Center Last Updated and main title
        VBox updatedAndTitleBox = new VBox();
        updatedAndTitleBox.setAlignment(Pos.CENTER);

        Label lastUpdated = new Label("Last Updated: March 19, 2025");
        lastUpdated.setFont(Font.font("Arial", 12));

        Label titleLabel = new Label("Terms of Use");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 26));

        updatedAndTitleBox.getChildren().addAll(lastUpdated, titleLabel);

        topContainer.getChildren().addAll(logoBackRow, updatedAndTitleBox);
        root.setTop(topContainer);

        // Center area: TextArea auto-fill
        TextArea textArea = new TextArea(loadTermsContent("/terms/TermOfUse.txt"));
        textArea.setWrapText(true);
        textArea.setEditable(false);
        textArea.setPadding(new Insets(10));
        textArea.setStyle("-fx-border-color: black; -fx-border-width: 1;");

        // Bind width and height to root
        textArea.prefWidthProperty().bind(root.widthProperty().subtract(40));
        textArea.prefHeightProperty().bind(root.heightProperty().subtract(topContainer.getHeight() + 40));
        root.setCenter(textArea);

        Scene scene = new Scene(root, width, height);
        return scene;
    }

    /**
     * Loads the terms of use content from a resource file.
     * Reads the text file and returns its contents as a string.
     *
     * @param resourcePath The path to the terms of use text file
     * @return The contents of the terms of use file as a string
     */
    private static String loadTermsContent(String resourcePath) {
        StringBuilder sb = new StringBuilder();
        try (InputStream is = TermofUse.class.getResourceAsStream(resourcePath);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            return "Failed to load terms content.";
        }
        return sb.toString();
    }
}
