package com.myfinanceapp.ui.usermanual;

import com.myfinanceapp.ui.loginscene.LoginScene;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 * A comprehensive user manual interface for the Finanger application.
 * This scene provides users with detailed documentation about the application's features,
 * including:
 * - Interactive navigation with smooth scrolling
 * - Responsive layout with dynamic resizing
 * - Markdown to HTML conversion for content formatting
 * - Theme-aware styling with day/night mode support
 * The interface features a clean, professional design with proper margins,
 * navigation controls, and a proceed to login button.
 */
public class UserManual {

    private static final double INITIAL_WIDTH = 800;
    private static final double INITIAL_HEIGHT = 450;
    private static final double MARGIN_FRAC = 0.02; // 2% margin on each side

    private static Group root;
    private static Polygon contentPolygon;
    private static Pane contentPane;
    private static VBox vbox;
    private static WebView webView;

    // Content area polygon with margins: (margin, margin), (1-margin, margin), (1-margin, 1-margin), (margin, 1-margin)
    private static final double[] CONTENT_POLY_FRACS = {
            MARGIN_FRAC, MARGIN_FRAC,
            1.0 - MARGIN_FRAC, MARGIN_FRAC,
            1.0 - MARGIN_FRAC, 1.0 - MARGIN_FRAC,
            MARGIN_FRAC, 1.0 - MARGIN_FRAC
    };

    // Content pane X fraction (starts at 0 since there's no sidebar)
    private static final double CONTENT_PANE_X_FRAC = 0.0;

    /**
     * Creates and returns a user manual scene with the specified dimensions.
     * The scene includes a WebView for displaying formatted content, navigation controls,
     * and a proceed to login button.
     *
     * @param stage The stage to display the scene
     * @param width The initial width of the scene
     * @param height The initial height of the scene
     * @return A configured Scene object for the user manual interface
     */
    public static Scene createScene(Stage stage, double width, double height) {
        root = new Group();
        Scene scene = new Scene(root, width, height);

        // Set minimum window dimensions
        stage.setMinWidth(INITIAL_WIDTH);
        stage.setMinHeight(INITIAL_HEIGHT);
        stage.setResizable(true);

        // === Content Polygon (Background) ===
        contentPolygon = new Polygon();
        contentPolygon.setFill(Color.WHITE); // White background for content
        contentPolygon.setStroke(Color.web("#93D2F3")); // Blue border to match Finager style
        contentPolygon.setStrokeWidth(2);
        root.getChildren().add(contentPolygon);

        // === Content Pane for the Manual ===
        contentPane = new Pane();
        root.getChildren().add(contentPane);

        vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));
        contentPane.getChildren().add(vbox);

        // === Title ===
        Label title = new Label("Finager User Manual");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.DARKBLUE);
        vbox.getChildren().add(title);

        // === User Manual Content in WebView ===
        webView = new WebView();
        webView.setStyle("-fx-border-color: #93D2F3; -fx-border-width: 2px; -fx-padding: 10px;");

        // Load the HTML content with the CSS and JavaScript for anchor links
        String htmlContent = "<html><head>" +
                "<link rel=\"stylesheet\" href=\"" + UserManual.class.getResource("/css/manual-style.css").toExternalForm() + "\">" +
                "<script>" +
                "document.addEventListener('DOMContentLoaded', function() {" +
                "  var links = document.getElementsByTagName('a');" +
                "  for (var i = 0; i < links.length; i++) {" +
                "    links[i].addEventListener('click', function(e) {" +
                "      e.preventDefault();" +
                "      var targetId = this.getAttribute('href').substring(1);" +
                "      var targetElement = document.getElementById(targetId);" +
                "      if (targetElement) {" +
                "        targetElement.scrollIntoView({ behavior: 'smooth' });" +
                "      }" +
                "    });" +
                "  }" +
                "});" +
                "</script>" +
                "</head><body>" + convertMarkdownToHtml(getUserManualContent()) + "</body></html>";
        WebEngine webEngine = webView.getEngine();
        webEngine.loadContent(htmlContent);

        // Make WebView take up available space, accounting for margins
        double marginPixels = MARGIN_FRAC * scene.getWidth();
        webView.prefWidthProperty().bind(vbox.widthProperty().subtract(40).subtract(2 * marginPixels));
        webView.prefHeightProperty().bind(vbox.heightProperty().subtract(100).subtract(2 * marginPixels));

        vbox.getChildren().add(webView);

        // === Proceed to Login Button ===
        Button proceedButton = new Button("Proceed to Login ➜");
        proceedButton.setPrefWidth(180);
        proceedButton.setStyle("-fx-background-color: #3377ff; -fx-text-fill: white; -fx-font-weight: bold;");
        proceedButton.setOnAction(e -> {
            stage.setScene(LoginScene.createScene(stage, root.getScene().getWidth(), root.getScene().getHeight()));
            stage.setTitle("Finanger - Login");
        });
        HBox buttonBox = new HBox(proceedButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(5, 0, 5, 0));
        vbox.getChildren().add(buttonBox);

        // Add dynamic resizing listener
        scene.widthProperty().addListener((obs, oldVal, newVal) -> relayout());
        scene.heightProperty().addListener((obs, oldVal, newVal) -> relayout());
        relayout();

        return scene;
    }

    /**
     * Dynamically recalculates the layout of UI components based on the current window size.
     * This method ensures proper positioning and scaling of all elements, including:
     * - Content polygon with margins
     * - Content pane position and size
     * - WebView dimensions
     */
    private static void relayout() {
        double curWidth = root.getScene().getWidth();
        double curHeight = root.getScene().getHeight();

        // Recalculate content polygon coordinates with margins
        contentPolygon.getPoints().setAll(
                CONTENT_POLY_FRACS[0] * curWidth, CONTENT_POLY_FRACS[1] * curHeight,
                CONTENT_POLY_FRACS[2] * curWidth, CONTENT_POLY_FRACS[3] * curHeight,
                CONTENT_POLY_FRACS[4] * curWidth, CONTENT_POLY_FRACS[5] * curHeight,
                CONTENT_POLY_FRACS[6] * curWidth, CONTENT_POLY_FRACS[7] * curHeight
        );

        // Recalculate contentPane position and size
        double marginPixels = MARGIN_FRAC * curWidth;
        double paneX = CONTENT_PANE_X_FRAC * curWidth + marginPixels;
        contentPane.setLayoutX(paneX);
        contentPane.setPrefSize(curWidth - paneX - marginPixels, curHeight - 2 * marginPixels);
        vbox.setPrefSize(contentPane.getPrefWidth(), contentPane.getPrefHeight());
    }

    /**
     * Converts Markdown content to HTML with enhanced formatting and styling.
     * This method handles:
     * - Headings with custom IDs
     * - Lists with proper indentation
     * - Paragraphs and text formatting
     * - Links with smooth scrolling
     * - Bold text formatting
     *
     * @param markdown The Markdown content to convert
     * @return The converted HTML content with proper styling and structure
     */
    static String convertMarkdownToHtml(String markdown) {
        // Split the markdown into lines
        String[] lines = markdown.split("\n");
        StringBuilder html = new StringBuilder();
        boolean inList = false;
        int indentLevel = 0;

        for (String line : lines) {
            String trimmedLine = line.trim();

            // Skip empty lines
            if (trimmedLine.isEmpty()) {
                if (inList) {
                    while (indentLevel >= 0) {
                        html.append("</ul>");
                        indentLevel -= 2;
                    }
                    inList = false;
                }
                html.append("\n");
                continue;
            }

            // Check for headings first
            if (trimmedLine.startsWith("#")) {
                if (inList) {
                    while (indentLevel >= 0) {
                        html.append("</ul>");
                        indentLevel -= 2;
                    }
                    inList = false;
                }

                // Handle headings with custom IDs (e.g., ## Heading {#custom-id})
                String headingId = "";
                String headingContent = "";
                if (trimmedLine.contains("{#")) {
                    int idStart = trimmedLine.indexOf("{#");
                    int idEnd = trimmedLine.indexOf("}", idStart);
                    if (idEnd != -1) {
                        headingId = trimmedLine.substring(idStart + 2, idEnd).trim();
                        headingContent = trimmedLine.substring(0, idStart).trim();
                    } else {
                        headingContent = trimmedLine;
                    }
                } else {
                    headingContent = trimmedLine;
                    headingId = headingContent.replaceAll("^#+\\s*", "").toLowerCase().replaceAll("[^a-z0-9]+", "-");
                }

                if (trimmedLine.startsWith("###")) {
                    headingContent = headingContent.replaceAll("^###\\s*", "").replaceAll("\\s*\\{#.*\\}", "");
                    html.append("<h3 id=\"").append(headingId).append("\">").append(headingContent).append("</h3>\n");
                } else if (trimmedLine.startsWith("##")) {
                    headingContent = headingContent.replaceAll("^##\\s*", "").replaceAll("\\s*\\{#.*\\}", "");
                    html.append("<h2 id=\"").append(headingId).append("\">").append(headingContent).append("</h2>\n");
                } else if (trimmedLine.startsWith("#")) {
                    headingContent = headingContent.replaceAll("^#\\s*", "").replaceAll("\\s*\\{#.*\\}", "");
                    html.append("<h1 id=\"").append(headingId).append("\">").append(headingContent).append("</h1>\n");
                }
            }
            // Check for list items
            else if (trimmedLine.startsWith("- ")) {
                int currentIndent = line.indexOf("- ");
                if (!inList) {
                    html.append("<ul>\n");
                    inList = true;
                    indentLevel = currentIndent;
                } else if (currentIndent > indentLevel) {
                    html.append("<ul>\n");
                    indentLevel = currentIndent;
                } else if (currentIndent < indentLevel) {
                    html.append("</ul>\n");
                    indentLevel = currentIndent;
                }
                String listItem = trimmedLine.substring(2); // Remove "- "
                html.append("<li>").append(listItem).append("</li>\n");
            }
            // Handle paragraphs and other content
            else {
                if (inList) {
                    while (indentLevel >= 0) {
                        html.append("</ul>\n");
                        indentLevel -= 2;
                    }
                    inList = false;
                }
                // Wrap in paragraph if not already a heading or list
                if (!trimmedLine.startsWith("<h") && !trimmedLine.startsWith("<ul")) {
                    html.append("<p>").append(trimmedLine).append("</p>\n");
                } else {
                    html.append(trimmedLine).append("\n");
                }
            }
        }

        // Close any remaining lists
        if (inList) {
            while (indentLevel >= 0) {
                html.append("</ul>\n");
                indentLevel -= 2;
            }
        }

        // Post-process the HTML
        String result = html.toString();

        // Replace Markdown bold text (e.g., **text**)
        result = result.replaceAll("\\*\\*(.*?)\\*\\*", "<strong>$1</strong>");

        // Replace Markdown links (e.g., [text](#link))
        result = result.replaceAll("\\[(.*?)\\]\\((.*?)\\)", "<a href=\"$2\">$1</a>");

        return result;
    }

    /**
     * Returns the comprehensive user manual content in Markdown format.
     * The content includes:
     * - Table of contents with navigation links
     * - Detailed feature descriptions
     * - Step-by-step usage instructions
     * - Interface overview
     * - Navigation guidelines
     *
     * @return A string containing the formatted Markdown content for the user manual
     */
    private static String getUserManualContent() {
        return "# Finager User Manual {#finager-user-manual}\n\n" +
                "Welcome to Finager, an AI-powered personal finance manager designed to help you take control of your money with ease. This user manual will guide you through the key features and functionalities of Finager, following the structure of the application's interface, starting with the left sidebar navigation and its corresponding subpages.\n\n" +
                "## Table of Contents {#table-of-contents}\n" +
                "1. [Overview of the Interface](#overview-of-the-interface)\n" +
                "2. [Status](#status)\n" +
                "   - [Income and Expense Trend](#income-and-expense-trend)\n" +
                "   - [Category Proportion Analysis](#category-proportion-analysis)\n" +
                "   - [Recent Transactions](#recent-transactions)\n" +
                "   - [AI Assistant](#ai-assistant)\n" +
                "3. [Goals](#goals)\n" +
                "   - [Viewing Goals](#viewing-goals)\n" +
                "   - [Create New Goal (Subpage)](#create-new-goal-subpage)\n" +
                "4. [New](#new)\n" +
                "   - [Manual Import](#manual-import)\n" +
                "   - [File Import](#file-import)\n" +
                "5. [Settings](#settings)\n" +
                "   - [System Settings (Subpage)](#system-settings-subpage)\n" +
                "   - [User Options (Subpage)](#user-options-subpage)\n" +
                "   - [Export Report (Subpage)](#export-report-subpage)\n" +
                "   - [About (Subpage)](#about-subpage)\n" +
                "6. [Log Out](#log-out)\n\n" +
                "## 1. Overview of the Interface {#overview-of-the-interface}\n" +
                "After logging into Finager, you'll see a motivational message on the left sidebar (e.g., \"WELCOME BACK!\"). The interface is divided into two main sections:\n" +
                "- **Left Sidebar**: Contains the main navigation options in the following order:\n" +
                "  - Status\n" +
                "  - Goals\n" +
                "  - New\n" +
                "  - Settings\n" +
                "  - Log out\n" +
                "- **Main Content Area**: Displays the selected feature or subpage (e.g., Goals, Settings subpages, etc.).\n\n" +
                "The structure of the pages and subpages mirrors the sidebar hierarchy, with some options (like Goals and Settings) having subpages for more specific tasks.\n\n" +
                "## 2. Status {#status}\n" +
                "The **Status** page is the default landing page after login, providing a comprehensive overview of your financial status.\n\n" +
                "### Income and Expense Trend {#income-and-expense-trend}\n" +
                "- A line graph displays your income and expenses over a selected date range.\n" +
                "- Adjust the date range using the **Start Date** and **End Date** fields (e.g., 2025/04/01 to 2025/04/14).\n" +
                "- Select the **Chart Type** (e.g., Line graph).\n" +
                "- View totals for expenses (e.g., 5610.00 CNY) and income (e.g., 1700.00 CNY).\n\n" +
                "### Category Proportion Analysis {#category-proportion-analysis}\n" +
                "- A pie chart shows the breakdown of your expenses by category (e.g., Debt: 1000.00 CNY, Groceries: 2500.00 CNY, Housing: 3000.00 CNY).\n\n" +
                "### Recent Transactions {#recent-transactions}\n" +
                "- A list of your recent transactions is displayed (e.g., 2025-04-14 Debt 1000.00 CNY, 2025-04-11 Freelance 700.00 CNY).\n" +
                "- Click **More** to view additional transactions.\n\n" +
                "### AI Assistant {#ai-assistant}\n" +
                "- Use the **Ask Your AI Assistant** section to get personalized financial advice.\n" +
                "- Type your question in the text box (e.g., \"How can I save more?\").\n" +
                "- The AI will provide suggestions (e.g., \"Reduce leisure spending, as you spent 750 CNY this month\").\n\n" +
                "## 3. Goals {#goals}\n" +
                "The **Goals** page allows you to track and manage your financial goals.\n\n" +
                "### Viewing Goals {#viewing-goals}\n" +
                "1. From the left sidebar, click on **Goals**.\n" +
                "2. You will see a list of your goals with details such as:\n" +
                "   - Goal title (e.g., \"test\")\n" +
                "   - Target Amount (e.g., 123.00 CNY)\n" +
                "   - Deadline (e.g., 2025/05/14)\n" +
                "   - Current Savings (e.g., 6,350.00 CNY)\n" +
                "   - Progress percentage (e.g., 100.0%)\n" +
                "3. To return to the main dashboard, click **Back to Status**.\n\n" +
                "### Create New Goal (Subpage) {#create-new-goal-subpage}\n" +
                "1. From the **Goals** page, click on the **Create a new goal** button in the main content area.\n" +
                "2. A subpage will appear with the following form fields:\n" +
                "   - **Type of your goal**: Select \"Saving Goal\".\n" +
                "   - **Goal title**: Enter a name for your goal (e.g., \"Vacation Fund\").\n" +
                "   - **Target amount**: Enter the amount you want to save (e.g., 5000.00).\n" +
                "   - **Currency**: Select the currency (e.g., CNY).\n" +
                "   - **Deadline**: Choose a date (format: YYYY/MM/DD, e.g., 2025/12/31).\n" +
                "3. Click **Save Goal** to create the goal, or **Cancel** to return to the Goals page.\n\n" +
                "## 4. New {#new}\n" +
                "The **New** page allows you to add transactions to your financial records, either manually or by importing a file.\n\n" +
                "### Manual Import {#manual-import}\n" +
                "1. In the **Manual Import** section, fill in the following fields:\n" +
                "   - **Transition Date**: Enter the date (format: YYYY-MM-DD, e.g., 2025-04-14).\n" +
                "   - **Transition Type**: Select the type (e.g., Expense).\n" +
                "   - **Currency**: Select the currency (e.g., CNY).\n" +
                "   - **Amount**: Enter the amount (e.g., 1000.00).\n" +
                "   - **Category**: Select a category (e.g., Salary, Rent, Utility).\n" +
                "   - **Payment Method**: Select a method (e.g., Cash, PayPal, Bank Transfer).\n" +
                "2. Click **Submit** to save the transaction.\n\n" +
                "### File Import {#file-import}\n" +
                "1. In the **File Import** section, click **Select a file**.\n" +
                "2. Upload a CSV file with the following columns:\n" +
                "   - Transaction Date (format: YYYY-MM-DD, e.g., 2025-03-15)\n" +
                "   - Transaction Type (e.g., Income/Expense)\n" +
                "   - Currency (e.g., CNY, USD)\n" +
                "   - Amount (e.g., 1234.56)\n" +
                "   - Category (income or expense category)\n" +
                "   - Payment Method (e.g., Cash, Bank Transfer)\n" +
                "3. Finager will process the file and add the transactions to your account.\n\n" +
                "## 5. Settings {#settings}\n" +
                "The **Settings** section allows you to customize your Finager experience. It includes four subpages accessible via the top navigation bar: System Settings, User Options, Export Report, and About.\n\n" +
                "### System Settings (Subpage) {#system-settings-subpage}\n" +
                "1. From the top navigation bar, click on **System Settings**.\n" +
                "2. Adjust the following options:\n" +
                "   - **Languages**: Select your preferred language (e.g., English).\n" +
                "   - **Night/Daytime Mode**: Choose between Daytime or Night mode.\n" +
                "   - **Window Size**: Select the resolution (e.g., 1920x1080).\n" +
                "3. Click **Reset to Default** to revert to default settings, or **Back to Status** to return to the Status page.\n\n" +
                "### User Options (Subpage) {#user-options-subpage}\n" +
                "1. From the top navigation bar, click on **User Options**.\n" +
                "2. Update the following:\n" +
                "   - **Reset Username**: Enter a new username and click **Save**.\n" +
                "   - **Reset Security Question**: Select a question (e.g., \"What is your favorite book?\"), enter your answer, and click **Save**.\n" +
                "   - **Reset Password**: Click the **Reset Password** link to update your password.\n" +
                "3. Click **Back to Status** to return to the Status page.\n\n" +
                "### Export Report (Subpage) {#export-report-subpage}\n" +
                "1. From the top navigation bar, click on **Export Report**.\n" +
                "2. A subpage will appear to select a date range:\n" +
                "   - **Start Date**: Choose the start date (e.g., 2025/04/01).\n" +
                "   - **End Date**: Choose the end date (e.g., 2025/04/14).\n" +
                "3. Click **Export Report** to download your financial report.\n" +
                "4. Click **Back to Status** to return to the Status page.\n\n" +
                "### About (Subpage) {#about-subpage}\n" +
                "1. From the top navigation bar, click on **About**.\n" +
                "2. Read the description:\n" +
                "   - \"Finager is an AI-powered personal finance manager designed to help you take control of your money with ease. Whether you input transactions manually or import files, Finager keeps everything organized in one place. Using smart AI, Finager automatically categorizes your expenses, detects spending patterns, and suggests personalized budgets and saving tips. Of course, you stay in control — review and adjust any misclassifications at any time. Smarter finance starts here. With Finager, you're not just tracking...\"\n" +
                "3. Click **Back to Status** to return to the Status page.\n\n" +
                "## 6. Log Out {#log-out}\n" +
                "1. From the left sidebar, click on **Log out**.\n" +
                "2. You will be logged out of Finager and returned to the login screen.\n\n" +
                "With Finager, you're on your way to smarter financial management. Follow the structure of the sidebar and its subpages to navigate the application seamlessly. If you have any questions, use the AI Assistant feature on the Status page or refer to this manual for guidance. Happy budgeting!";
    }
}