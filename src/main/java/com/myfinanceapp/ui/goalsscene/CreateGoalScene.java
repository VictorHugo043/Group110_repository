package com.myfinanceapp.ui.goalsscene;

import com.myfinanceapp.model.Goal;
import com.myfinanceapp.model.User;
import com.myfinanceapp.service.GoalManager;
import com.myfinanceapp.ui.common.LeftSidebarFactory;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

public class CreateGoalScene {
    
    public static Scene createScene(Stage stage, double width, double height, User loggedUser) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: white;");

        // 左侧导航栏
        VBox sideBar = LeftSidebarFactory.createLeftSidebar(stage, "Create Goal", loggedUser);
        root.setLeft(sideBar);
        
        // 主容器
        VBox mainBox = new VBox(20);
        mainBox.setAlignment(Pos.CENTER);
        mainBox.setPadding(new Insets(40));
        mainBox.setMaxWidth(600);

        // 标题
        Label titleLabel = new Label("Create New Goal");
        titleLabel.setFont(Font.font("Arial", 24));
        titleLabel.setTextFill(Color.DARKBLUE);

        // 表单容器
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);

        // 组件样式
        Font labelFont = Font.font("Arial", 14);
        Color labelColor = Color.DARKBLUE;

        // 目标类型选择
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Saving Goal", "Debt Repayment Goal", "Budget Control Goal");
        typeCombo.getSelectionModel().selectFirst();
        typeCombo.setPrefWidth(250);
        
        // 目标标题
        TextField titleField = new TextField();
        titleField.setPromptText("Goal Title");
        titleField.setPrefWidth(250);
        
        // 目标金额
        TextField amountField = new TextField();
        amountField.setPromptText("Target Amount (CNY)");
        amountField.setPrefWidth(250);

        // 截止日期
        DatePicker deadlinePicker = new DatePicker(LocalDate.now().plusMonths(1));
        deadlinePicker.setPrefWidth(250);

        // 添加组件到网格
        addStyledRow(grid, 0, "Type of your goal:", typeCombo, labelFont, labelColor);
        addStyledRow(grid, 1, "Goal title:", titleField, labelFont, labelColor);
        addStyledRow(grid, 2, "Target amount:", amountField, labelFont, labelColor);
        addStyledRow(grid, 3, "Deadline:", deadlinePicker, labelFont, labelColor);

        // 按钮区域
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button saveButton = new Button("Save Goal");
        saveButton.setStyle("-fx-background-color: #3282FA; -fx-text-fill: white;");
        saveButton.setPrefWidth(120);
        
        Button cancelButton = new Button("Cancel");
        cancelButton.setPrefWidth(120);
        
        buttonBox.getChildren().addAll(saveButton, cancelButton);
        
        // 保存按钮点击事件
        saveButton.setOnAction(event -> {
            try {
                String goalTitle = titleField.getText().isEmpty() ? "New Goal" : titleField.getText();
                
                Goal newGoal = new Goal(
                    UUID.randomUUID().toString(),
                    getGoalType(typeCombo.getValue()),
                    goalTitle,
                    Double.parseDouble(amountField.getText()),
                    0.0, // 当前金额默认0
                    deadlinePicker.getValue(),
                    null
                );
                
                try {
                    // Save the new goal to storage
                    GoalManager.addGoal(newGoal);
                    
                    // Navigate back to goals list
                    Scene goalsScene = Goals.createScene(stage, width, height, loggedUser);
                    stage.setScene(goalsScene);
                } catch (IOException e) {
                    showErrorAlert("Failed to save goal: " + e.getMessage());
                }
            } catch (NumberFormatException e) {
                showErrorAlert("Invalid amount format");
            }
        });
        
        // 取消按钮点击事件
        cancelButton.setOnAction(event -> {
            Scene goalsScene = Goals.createScene(stage, width, height, loggedUser);
            stage.setScene(goalsScene);
        });

        // 组装整个界面
        mainBox.getChildren().addAll(titleLabel, grid, buttonBox);
        
        // 使用居中布局
        VBox centerContainer = new VBox(mainBox);
        centerContainer.setAlignment(Pos.CENTER);
        root.setCenter(centerContainer);

        return new Scene(root, width, height);
    }
    
    private static String getGoalType(String selection) {
        if (selection.startsWith("Saving")) {
            return "SAVING";
        } else if (selection.startsWith("Debt")) {
            return "DEBT_REPAYMENT";
        } else {
            return "BUDGET_CONTROL";
        }
    }

    private static void addStyledRow(GridPane grid, int row, String labelText, Control control, Font font, Color color) {
        Label label = new Label(labelText);
        label.setFont(font);
        label.setTextFill(color);
        grid.add(label, 0, row);
        grid.add(control, 1, row);
    }

    private static void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Input Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}