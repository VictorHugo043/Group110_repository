package com.myfinanceapp.ui.registrationterms;

import com.myfinanceapp.ui.signupscene.SignUp;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import javafx.scene.shape.Line;

/**
 * A privacy policy display interface for the Finanger application.
 * This scene presents the application's privacy policy in a scrollable text area
 * with a professional layout including a header and navigation controls.
 *
 * @author SE_Group110
 * @version 4.0
 */
public class PrivacyPolicy {
    private static final double INITIAL_WIDTH = 800;
    private static final double INITIAL_HEIGHT = 450;

    /**
     * Creates and returns a privacy policy scene.
     * The scene displays the privacy policy text in a scrollable area with
     * navigation controls and proper formatting.
     *
     * @param stage The stage to display the scene
     * @param width The initial width of the scene
     * @param height The initial height of the scene
     * @return A configured Scene object for the privacy policy interface
     */
    public static Scene createScene(Stage stage, double width, double height) {
        BorderPane root = new BorderPane();
        root.setPrefSize(width, height);
        root.getStyleClass().add("terms-root");

        stage.setMinWidth(INITIAL_WIDTH);
        stage.setMinHeight(INITIAL_HEIGHT);
        stage.setResizable(true);

        VBox topContainer = new VBox();
        topContainer.getStyleClass().add("header-container");

        HBox logoBackRow = new HBox();
        logoBackRow.setAlignment(Pos.CENTER_LEFT);

        Label logoLabel = new Label("Finanger");
        logoLabel.getStyleClass().add("logo-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button backBtn = new Button("Back");
        backBtn.getStyleClass().add("back-button");
        backBtn.setOnAction(e -> {
            Scene signUpScene = SignUp.createScene(stage, root.getScene().getWidth(), root.getScene().getHeight());
            stage.setScene(signUpScene);
            stage.setTitle("Sign Up");
        });
        logoBackRow.getChildren().addAll(logoLabel, spacer, backBtn);

        VBox updatedAndTitleBox = new VBox();
        updatedAndTitleBox.getStyleClass().add("title-container");

        Label lastUpdated = new Label("Last Update: 2025.3.19");
        lastUpdated.getStyleClass().add("last-updated-label");

        Label titleLabel = new Label("Privacy Policy");
        titleLabel.getStyleClass().add("title-label");

        updatedAndTitleBox.getChildren().addAll(lastUpdated, titleLabel);

        topContainer.getChildren().addAll(logoBackRow, updatedAndTitleBox);
        root.setTop(topContainer);

        // Center area with enhanced formatting
        ScrollPane scrollPane = createFormattedTextContent("/terms/PrivacyPolicy.txt");
        scrollPane.prefWidthProperty().bind(root.widthProperty());
        scrollPane.prefHeightProperty().bind(root.heightProperty().subtract(topContainer.heightProperty()));
        
        root.setCenter(scrollPane);

        Scene scene = new Scene(root, width, height);
        String css = Objects.requireNonNull(PrivacyPolicy.class.getResource("/css/terms-style.css")).toExternalForm();
        scene.getStylesheets().add(css);

        return scene;
    }

    /**
     * Creates a formatted scrollable content area for the privacy policy
     * with enhanced visual styling and text formatting.
     *
     * @param resourcePath The path to the privacy policy content file
     * @return A ScrollPane containing the formatted privacy policy content
     */
    private static ScrollPane createFormattedTextContent(String resourcePath) {
        String content = loadPolicyContent(resourcePath);
        
        VBox contentBox = new VBox(15);
        contentBox.setPadding(new Insets(25));
        contentBox.setMaxWidth(Double.MAX_VALUE);
        contentBox.getStyleClass().add("terms-text-area");

        String[] sections = content.split("————————————————————————————————————————————————————————");
        
        if (sections.length > 0) {
            String introText = sections[0].trim();

            Text headerText = new Text("★ PRIVACY POLICY ★");
            headerText.getStyleClass().add("header-title-text");
            
            HBox titleBox = new HBox();
            titleBox.setAlignment(Pos.CENTER);
            titleBox.getChildren().add(headerText);
            
            VBox headerContainer = new VBox(15);
            headerContainer.getStyleClass().add("header-container-box");
            headerContainer.setAlignment(Pos.CENTER);
            headerContainer.getChildren().add(titleBox);

            Text introContentText = new Text(introText);
            introContentText.getStyleClass().add("intro-text");
            introContentText.setWrappingWidth(700);
            headerContainer.getChildren().add(introContentText);
            
            contentBox.getChildren().add(headerContainer);

            Line divider = new Line(0, 0, 700, 0);
            divider.getStyleClass().add("divider-line");
            contentBox.getChildren().add(divider);

            if (sections.length > 1) {
                String[] paragraphs = sections[1].split("\n");
                
                for (String paragraph : paragraphs) {
                    paragraph = paragraph.trim();
                    if (paragraph.isEmpty()) continue;
                    
                    if (paragraph.matches("\\d+\\..+")) {
                        Text sectionHeader = new Text(paragraph);
                        sectionHeader.getStyleClass().add("section-header");
                        contentBox.getChildren().add(sectionHeader);
                    } else if (paragraph.startsWith("◆")) {
                        HBox listItem = new HBox(10);
                        listItem.setAlignment(Pos.TOP_LEFT);
                        
                        Text bullet = new Text("•");
                        Text itemText = new Text(paragraph.substring(1).trim());
                        itemText.setWrappingWidth(670);
                        
                        listItem.getChildren().addAll(bullet, itemText);
                        contentBox.getChildren().add(listItem);
                    } else {
                        Text paragraphText = new Text(paragraph);
                        paragraphText.setWrappingWidth(700);
                        contentBox.getChildren().add(paragraphText);
                    }
                }
            }
        }
        
        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        return scrollPane;
    }

    /**
     * Loads the privacy policy content from a resource file.
     * Reads the text file and returns its contents as a string.
     *
     * @param resourcePath The path to the privacy policy text file
     * @return The contents of the privacy policy file as a string
     */
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
