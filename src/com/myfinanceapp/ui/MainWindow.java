package com.myfinanceapp.ui;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {

    public MainWindow() {
        // 设置窗口标题
        super("Smart Personal Finance Manager");

        // 设置窗口大小
        setSize(600, 400);

        // 设置关闭窗口时的默认操作
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 居中显示
        setLocationRelativeTo(null);

        // 初始化界面元素
        initComponents();
    }

    private void initComponents() {
        // 创建一个主面板，使用简单的边框布局或流式布局都可以
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // 顶部标题（仅用于演示，可自行修改或删除）
        JLabel titleLabel = new JLabel("Smart Finance Manager", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // 中部放置功能按钮，也可以使用更灵活的布局
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        // 示例按钮：手动添加交易
        JButton addTransactionButton = new JButton("Add Transaction");
        buttonPanel.add(addTransactionButton);

        // 示例按钮：导入交易CSV
        JButton importCSVButton = new JButton("Import TransactionCSV");
        buttonPanel.add(importCSVButton);

        // 示例按钮：分析支出
        JButton analyzeSpendingButton = new JButton("Analyze Outcome");
        buttonPanel.add(analyzeSpendingButton);

        // 示例按钮：设置 / 偏好
        JButton settingsButton = new JButton("System Setting");
        buttonPanel.add(settingsButton);

        // 将中间的按钮面板加到主面板中间
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        // 底部提示，仅用于演示
        JLabel footerLabel = new JLabel("© 2025 My Finance App", SwingConstants.CENTER);
        mainPanel.add(footerLabel, BorderLayout.SOUTH);

        // 将主面板加到窗口中
        setContentPane(mainPanel);
    }
}
