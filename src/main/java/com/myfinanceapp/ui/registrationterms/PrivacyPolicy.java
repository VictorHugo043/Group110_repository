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

public class PrivacyPolicy {
    private static final double INITIAL_WIDTH = 800;
    private static final double INITIAL_HEIGHT = 450;

    public static Scene createScene(Stage stage, double width, double height) {
        BorderPane root = new BorderPane();
        root.setPrefSize(width, height);

        stage.setMinWidth(INITIAL_WIDTH);
        stage.setMinHeight(INITIAL_HEIGHT);
        stage.setResizable(true);

        VBox topContainer = new VBox();
        topContainer.setPadding(new Insets(10, 15, 0, 15));
        topContainer.setSpacing(5);

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
            Scene signUpScene = SignUp.createScene(stage, INITIAL_WIDTH, INITIAL_HEIGHT);
            stage.setScene(signUpScene);
            stage.setTitle("Sign Up");
        });
        logoBackRow.getChildren().addAll(logoLabel, spacer, backBtn);

        VBox updatedAndTitleBox = new VBox();
        updatedAndTitleBox.setAlignment(Pos.CENTER);

        Label lastUpdated = new Label("Last Updated: March 19, 2025");
        lastUpdated.setFont(Font.font("Arial", 12));

        Label titleLabel = new Label("Privacy Policy");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 26));

        updatedAndTitleBox.getChildren().addAll(lastUpdated, titleLabel);

        topContainer.getChildren().addAll(logoBackRow, updatedAndTitleBox);
        root.setTop(topContainer);

        TextArea textArea = new TextArea(loadPolicyContent("/terms/PrivacyPolicy.txt"));
        textArea.setWrapText(true);
        textArea.setEditable(false);
        textArea.setPadding(new Insets(10));
        textArea.setStyle("-fx-border-color: black; -fx-border-width: 1;");

        textArea.prefWidthProperty().bind(root.widthProperty().subtract(40));
        textArea.prefHeightProperty().bind(root.heightProperty().subtract(150));
        root.setCenter(textArea);

        Scene scene = new Scene(root, width, height);
        return scene;
    }

    private static String loadPolicyContent(String resourcePath) {
        StringBuilder sb = new StringBuilder();
        try (InputStream is = PrivacyPolicy.class.getResourceAsStream(resourcePath);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            return "Failed to load privacy policy content.";
        }
        return sb.toString();
    }
}
