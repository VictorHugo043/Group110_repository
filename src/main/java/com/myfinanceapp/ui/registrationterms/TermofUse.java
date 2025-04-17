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

public class TermofUse {
    private static final double INITIAL_WIDTH = 800;
    private static final double INITIAL_HEIGHT = 450;

    public static Scene createScene(Stage stage, double width, double height) {
        BorderPane root = new BorderPane();
        root.setPrefSize(width, height);

        // 设置最小窗口尺寸
        stage.setMinWidth(INITIAL_WIDTH);
        stage.setMinHeight(INITIAL_HEIGHT);
        stage.setResizable(true);

        // 顶部区域
        VBox topContainer = new VBox();
        topContainer.setPadding(new Insets(10, 15, 0, 15));
        topContainer.setSpacing(5);

        // 行1: 左侧LOGO和右侧Back按钮
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

        // 行2: 居中显示 Last Updated 和 主标题
        VBox updatedAndTitleBox = new VBox();
        updatedAndTitleBox.setAlignment(Pos.CENTER);

        Label lastUpdated = new Label("Last Updated: March 19, 2025");
        lastUpdated.setFont(Font.font("Arial", 12));

        Label titleLabel = new Label("Terms of Use");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 26));

        updatedAndTitleBox.getChildren().addAll(lastUpdated, titleLabel);

        topContainer.getChildren().addAll(logoBackRow, updatedAndTitleBox);
        root.setTop(topContainer);

        // 中心区域：TextArea 自动填充
        TextArea textArea = new TextArea(loadTermsContent("/terms/TermOfUse.txt"));
        textArea.setWrapText(true);
        textArea.setEditable(false);
        textArea.setPadding(new Insets(10));
        textArea.setStyle("-fx-border-color: black; -fx-border-width: 1;");

        // 绑定宽高，使其随 root 变化
        textArea.prefWidthProperty().bind(root.widthProperty().subtract(40));
        textArea.prefHeightProperty().bind(root.heightProperty().subtract(topContainer.getHeight() + 40));
        root.setCenter(textArea);

        Scene scene = new Scene(root, width, height);
        return scene;
    }

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
