package com.myfinanceapp;

import com.myfinanceapp.ui.mainwindow.MainWindow;

/**
 * The main entry point for the Finanger application.
 * This class initializes and launches the JavaFX application window.
 * It serves as the starting point for the entire application.
 *
 * @author SE_Group110
 * @version 4.0
 */
public class FinanceApp {
    /**
     * The main method that launches the JavaFX application.
     * This method is called when the application starts and initializes
     * the main window of the application.
     *
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        // Launch the JavaFX application
        MainWindow.launch(MainWindow.class, args);
    }
}
