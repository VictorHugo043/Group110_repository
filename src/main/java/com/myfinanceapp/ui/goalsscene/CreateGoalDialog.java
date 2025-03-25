package com.myfinanceapp.ui.goalsscene;

import com.myfinanceapp.model.Goal;
import com.myfinanceapp.model.User;
import com.myfinanceapp.service.GoalManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

public class CreateGoalDialog extends Dialog<Goal> {
    
    public CreateGoalDialog(Stage owner, User loggedUser) {
        setTitle("Create New Goal");
        
        // 设置按钮
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // 主容器
        VBox mainBox = new VBox(15);
        mainBox.setAlignment(Pos.CENTER);
        mainBox.setPadding(new Insets(20));
        mainBox.setStyle("-fx-background-color: white;");

        // 表单容器
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);

        // 组件样式
        Font labelFont = new Font(14);
        Color labelColor = Color.DARKBLUE;

        // 目标类型选择
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Saving Goal", "Debt Repayment Goal");
        typeCombo.getSelectionModel().selectFirst();
        
        // 目标金额
        TextField amountField = new TextField();
        amountField.setPromptText("Target Amount (CNY)");
        amountField.setPrefWidth(200);

        // 截止日期
        DatePicker deadlinePicker = new DatePicker(LocalDate.now().plusMonths(1));
        deadlinePicker.setPrefWidth(200);

        // 添加组件到网格
        addStyledRow(grid, 0, "Type of your goal:", typeCombo, labelFont, labelColor);
        addStyledRow(grid, 1, "Target amount:", amountField, labelFont, labelColor);
        addStyledRow(grid, 2, "Deadline:", deadlinePicker, labelFont, labelColor);

        mainBox.getChildren().add(grid);
        getDialogPane().setContent(mainBox);

        // 结果转换
        setResultConverter(buttonType -> {
            if (buttonType == saveButtonType) {
                try {
                    Goal newGoal = new Goal(
                        UUID.randomUUID().toString(),
                        typeCombo.getValue().startsWith("Saving") ? "SAVING" : "DEBT_REPAYMENT",
                        "New Goal", // 根据需求可添加标题字段
                        Double.parseDouble(amountField.getText()),
                        0.0, // 当前金额默认0
                        deadlinePicker.getValue(),
                        null
                    );
                    try {
                        // Save the new goal to storage
                        GoalManager.addGoal(newGoal);
                        
                        return newGoal;
                    } catch (IOException e) {
                        showErrorAlert("Failed to save goal: " + e.getMessage());
                        return null;
                    }
                } catch (NumberFormatException e) {
                    showErrorAlert("Invalid amount format");
                }
            }
            return null;
        });
    }

    private void addStyledRow(GridPane grid, int row, String labelText, Control control, Font font, Color color) {
        Label label = new Label(labelText);
        label.setFont(font);
        label.setTextFill(color);
        grid.add(label, 0, row);
        grid.add(control, 1, row);
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Input Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}