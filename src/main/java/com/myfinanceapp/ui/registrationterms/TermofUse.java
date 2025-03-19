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
    public static Scene createScene(Stage stage, double width, double height) {
        BorderPane root = new BorderPane();
        root.setPrefSize(width, height);

        // ========== 顶部区域：包含LOGO、Back按钮，以及"Last Updated"和主标题 ==========
        VBox topContainer = new VBox();
        topContainer.setPadding(new Insets(10, 15, 0, 15));
        topContainer.setSpacing(5);

        // 行1: 左侧“Finanger” (LOGO), 右侧“back”按钮
        HBox logoBackRow = new HBox();
        logoBackRow.setAlignment(Pos.CENTER_LEFT);

        Label logoLabel = new Label("Finanger");
        logoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        logoLabel.setStyle("-fx-text-fill: #459af7;"); // 例如浅蓝色

        // 占位符弹簧
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button backBtn = new Button("back");
        backBtn.setStyle("-fx-background-color: #BEE3F8; -fx-text-fill: black;");
        backBtn.setOnAction(e -> {
            // 返回 SignUp 界面
            Scene signUpScene = SignUp.createScene(stage, width, height);
            stage.setScene(signUpScene);
            stage.setTitle("Sign Up");
        });

        // 将LOGO、空白、back按钮加入logoBackRow
        logoBackRow.getChildren().addAll(logoLabel, spacer, backBtn);

        // 行2: 显示 Last Updated 和 主标题(居中)
        VBox updatedAndTitleBox = new VBox();
        updatedAndTitleBox.setAlignment(Pos.CENTER);

        Label lastUpdated = new Label("Last Updated: March 19, 2025");
        lastUpdated.setFont(Font.font("Arial", 12));

        Label titleLabel = new Label("Terms of Use");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 26));

        updatedAndTitleBox.getChildren().addAll(lastUpdated, titleLabel);

        // 将这两行加入topContainer
        topContainer.getChildren().addAll(logoBackRow, updatedAndTitleBox);

        root.setTop(topContainer);

        // ========== 中心区域：放置滚动文本（单滚动条） ==========
        // 用一个 TextArea 或 ScrollPane+Label都可，示例用TextArea
        TextArea textArea = new TextArea(loadTermsContent("/terms/TermOfUse.txt"));
        textArea.setWrapText(true);
        textArea.setEditable(false);

        // 调整留白
        textArea.setPadding(new Insets(10));
        // 给文本区加简单边框
        textArea.setStyle("-fx-border-color: black; -fx-border-width: 1;");
        // 让文本区尽量填满中心
        textArea.setPrefSize(width - 40, height - 200);

        // 只需一个滚动条的话，TextArea 自带滚动功能，无需额外 ScrollPane
        root.setCenter(textArea);

        return new Scene(root, width, height);
    }

    /**
     * 从外部资源加载文本
     * 需将TermOfUse.txt放在 src/main/resources/terms/TermOfUse.txt
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
