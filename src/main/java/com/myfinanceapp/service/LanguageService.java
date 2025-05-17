package com.myfinanceapp.service;

import java.util.HashMap;
import java.util.Map;

/**
 * Service class for managing internationalization and localization in the
 * application.
 * This class implements the Singleton pattern to provide a centralized service
 * for
 * handling multiple languages and translations throughout the application.
 * 
 * The service supports:
 * - Multiple language support (currently English and Chinese)
 * - Dynamic language switching
 * - Translation key management
 * - Welcome messages and UI text translations
 * - Error messages and notifications
 *
 * @author SE_Group110
 * @version 4.0
 */
public class LanguageService {
    /** Singleton instance of LanguageService */
    private static LanguageService instance;

    /** Current selected language */
    private String currentLanguage;

    /** Map of translations for different languages */
    private Map<String, Map<String, String>> translations;

    /**
     * Private constructor to enforce singleton pattern.
     * Initializes the service with default language (English) and loads
     * translations.
     */
    private LanguageService() {
        this.currentLanguage = "English";
        initializeTranslations();
    }

    /**
     * Gets the singleton instance of LanguageService.
     * Creates a new instance if one doesn't exist.
     *
     * @return The singleton instance of LanguageService
     */
    public static LanguageService getInstance() {
        if (instance == null) {
            instance = new LanguageService();
        }
        return instance;
    }

    /**
     * Initializes the translation maps for all supported languages.
     * Sets up translation keys and their corresponding values for:
     * - UI elements
     * - Welcome messages
     * - Error messages
     * - System notifications
     * - User interface text
     */
    private void initializeTranslations() {
        translations = new HashMap<>();

        // English translations
        Map<String, String> english = new HashMap<>();
        english.put("languages", "Languages");
        english.put("night_day_mode", "Night/Daytime Mode");
        english.put("window_size", "Window Size");
        english.put("default_currency", "Default Currency");
        english.put("reset_to_default", "Reset to Default");
        english.put("back_to_status", "Back to Status");
        english.put("daytime", "Daytime");
        english.put("nighttime", "Nighttime");
        english.put("system_settings", "System Settings");
        english.put("user_options", "User Options");
        english.put("export_report", "Export Report");
        english.put("about", "About");
        english.put("welcome_message",
                "Welcome to use the financial assistant. Please feel free to ask any financial questions you have.");
        english.put("welcome_message_status", "Welcome back!");
        english.put("welcome_message_goals", "It's My Goal!!!!!");
        english.put("welcome_message_new", "Every day is a\nnew beginning");
        english.put("welcome_message_settings", "Only you can do!");
        english.put("welcome_message_default", "Welcome to Finanger");
        english.put("status", "Status");
        english.put("goals", "Goals");
        english.put("new", "New");
        english.put("settings", "Settings");
        english.put("logout", "Log out");
        english.put("logout_confirmation", "Are you sure you want to log out?");
        english.put("logout_title", "Confirm Logout");
        english.put("login", "Login");
        english.put("import_successful", "Import Successfully");
        english.put("import_success_message", "Successfully import transaction items");

        // User Options translations
        english.put("current_username", "Current Username");
        english.put("reset_username", "Reset Username");
        english.put("new_username", "New username");
        english.put("save", "Save");
        english.put("error", "Error");
        english.put("success", "Success");
        english.put("username_empty", "Username cannot be empty!");
        english.put("username_updated", "Username updated!");
        english.put("username_update_failed", "Failed to update username!");
        english.put("reset_security_question", "Reset Security Question");
        english.put("security_question_1", "What is your favorite book?");
        english.put("security_question_2", "What is your mother's maiden name?");
        english.put("security_question_3", "What is your best friend's name?");
        english.put("security_question_4", "What city were you born in?");
        english.put("your_answer", "Your answer:");
        english.put("answer_empty", "Answer cannot be empty!");
        english.put("security_question_updated", "Security question updated!");
        english.put("security_question_update_failed", "Failed to update question!");
        english.put("reset_password", "Reset Password");

        // About page translations
        english.put("about_finanger", "About Finanger");
        english.put("about_description",
                "Finanger is an AI-powered personal finance manager designed to help you take control " +
                        "of your money with ease. Whether you input transactions manually or import files, " +
                        "Finanger keeps everything organized in one place.\n\n" +
                        "Using smart AI, Finanger automatically categorizes your expenses, detects spending " +
                        "patterns, and suggests personalized budgets and saving tips. Of course, you stay in " +
                        "control — review and adjust any misclassifications at any time.\n\n" +
                        "Smarter finance starts here. With Finanger, you're not just tracking money — " +
                        "you're mastering it.");

        // Export Report translations
        english.put("select_date_range", "Select Date Range");
        english.put("start_date", "Start Date");
        english.put("end_date", "End Date");
        english.put("to", "to");
        english.put("export_success", "Financial report exported successfully!");
        english.put("export_failed", "Failed to export report");

        // Transaction Scene translations
        english.put("manual_import", "Manual Import:");
        english.put("transaction_date", "Transaction Date");
        english.put("select_date", "Select date");
        english.put("transaction_type", "Transaction Type");
        english.put("income", "Income");
        english.put("expense", "Expense");
        english.put("currency", "Currency");
        english.put("amount", "Amount");
        english.put("enter_amount", "Please enter amount");
        english.put("description", "Description");
        english.put("enter_description", "Enter transaction description");
        english.put("auto_sorting", "Auto-sorting");
        english.put("category", "Category");
        english.put("category_example", "e.g., Salary, Rent, Utilities");
        english.put("payment_method", "Payment Method");
        english.put("payment_method_example", "e.g., Cash, PayPal, Bank Transfer");
        english.put("submit", "Submit");
        english.put("warning", "Warning");
        english.put("enter_description_first", "Please enter a description first");
        english.put("auto_sort_failed", "Failed to auto-sort category");
        english.put("missing_info", "Missing Information");
        english.put("fill_all_blanks", "Please fill in all blanks before submitting");
        english.put("invalid_date", "Invalid Date");
        english.put("invalid_date_format",
                "Please enter a valid date in format yyyy-MM-dd\nMonth must be 1-12 and day must be 1-31");
        english.put("invalid_category", "Invalid Category");
        english.put("category_english_only", "Category must contain only English letters");
        english.put("invalid_payment_method", "Invalid Payment Method");
        english.put("payment_method_english_only", "Payment method must contain only English letters");
        english.put("invalid_amount", "Invalid Amount");
        english.put("enter_valid_number", "Please type in a valid number");
        english.put("transaction_added", "Transaction Added");
        english.put("transaction_success", "Transaction has been successfully added.");
        english.put("file_import", "File Import:");
        english.put("select_file", "Select a file");
        english.put("reference_template", "Reference Template");
        english.put("save_template_csv", "Save Template Excel");
        english.put("template_not_found", "Template file not found in resources/template folder.");
        english.put("download_complete", "Download Complete");
        english.put("template_download_success", "Excel template downloaded successfully!");
        english.put("download_failed", "Download Failed");
        english.put("template_download_failed", "Failed to download template");
        english.put("csv_format_guide",
                "Your file (supports CSV UTF-8, CSV, Excel files) should\ncontain the following columns:\n\n" +
                        "Transaction Date\n" +
                        "(format: YYYY-MM-DD, e.g. 2025-03-15)\n\n" +
                        "Transaction Type\n" +
                        "(only: Income / Expense)\n\n" +
                        "Currency\n" +
                        "(currency type, e.g. CNY, USD)\n\n" +
                        "Amount\n" +
                        "(number format, e.g. 1234.56)\n\n" +
                        "Description\n" +
                        "(transaction description)\n\n" +
                        "Category\n" +
                        "(income and expense category)\n\n" +
                        "Payment Method\n" +
                        "(payment method)");

        // File type filter translations
        english.put("csv_utf8_filter", "CSV UTF-8 (Comma Separated) (*.csv)");
        english.put("csv_filter", "CSV (Comma Separated) (*.csv)");
        english.put("excel_filter", "Excel Files (*.xlsx)");
        english.put("all_supported_filter", "All Supported Files (*.csv, *.xlsx)");

        // Status Scene translations
        english.put("income_and_expenses", "Income and Expenses");
        english.put("start_date", "Start Date");
        english.put("end_date", "End Date");
        english.put("select_start_date", "Select start date");
        english.put("select_end_date", "Select end date");
        english.put("chart_type", "Chart Type");
        english.put("line_graph", "Line graph");
        english.put("bar_graph", "Bar graph");
        english.put("date", "Date");
        english.put("amount", "Amount");
        english.put("ex_in_trend", "Ex/In Trend");
        english.put("category_proportion", "Category Proportion Analysis");
        english.put("recent_transactions", "Recent Transactions");
        english.put("manage_transactions", "Manage All Transactions");
        english.put("ask_ai_assistant", "Ask Your AI Assistant:");
        english.put("type_question", "Type your question...");
        english.put("suggestion", "Suggestion");
        english.put("more", "More>");
        english.put("chat_history", "Chat History");
        english.put("you", "You");
        english.put("assistant", "Assistant");

        // Transaction Management translations
        english.put("transactions", "Transactions");
        english.put("manage_transactions", "Manage Your Transactions");
        english.put("filter", "Filter");
        english.put("reset_filter", "Reset the filter");
        english.put("all", "All");
        english.put("action", "Action");
        english.put("delete", "Delete");

        // Add export report translations
        english.put("financial_report", "Financial Report");
        english.put("user", "User");
        english.put("date_range", "Date Range");
        english.put("to", "to");
        english.put("income_and_expenses", "Income and Expense Trend");
        english.put("category_proportion", "Expense by Category");
        english.put("financial_summary", "Financial Summary");
        english.put("total_income", "Total Income");
        english.put("total_expense", "Total Expense");
        english.put("net_balance", "Net Balance");
        english.put("top_expense_category", "Top Expense Category");
        english.put("transaction_details", "Transaction Details");
        english.put("date", "Date");
        english.put("transaction_type", "Type");
        english.put("amount", "Amount");
        english.put("currency", "Currency");
        english.put("category", "Category");
        english.put("payment_method", "Payment Method");
        english.put("income", "Income");
        english.put("expense", "Expense");

        translations.put("English", english);

        // Chinese translations
        Map<String, String> chinese = new HashMap<>();
        chinese.put("languages", "语言");
        chinese.put("night_day_mode", "夜间/日间模式");
        chinese.put("window_size", "窗口大小");
        chinese.put("default_currency", "默认货币");
        chinese.put("reset_to_default", "重置为默认");
        chinese.put("back_to_status", "返回状态页");
        chinese.put("daytime", "日间模式");
        chinese.put("nighttime", "夜间模式");
        chinese.put("system_settings", "系统设置");
        chinese.put("user_options", "用户选项");
        chinese.put("export_report", "导出报告");
        chinese.put("about", "关于");
        chinese.put("welcome_message", "欢迎使用财务助手。请随时询问任何财务相关的问题。");
        chinese.put("welcome_message_status", "欢迎回来！");
        chinese.put("welcome_message_goals", "这是我的目标！");
        chinese.put("welcome_message_new", "每一天都是\n新的开始");
        chinese.put("welcome_message_settings", "只有你能做到！");
        chinese.put("welcome_message_default", "欢迎使用 Finanger");
        chinese.put("status", "状态");
        chinese.put("goals", "目标");
        chinese.put("new", "新建");
        chinese.put("settings", "设置");
        chinese.put("logout", "退出登录");
        chinese.put("logout_confirmation", "确定要退出登录吗？");
        chinese.put("logout_title", "确认退出");
        chinese.put("login", "登录");
        chinese.put("import_successful", "导入成功");
        chinese.put("import_success_message", "成功导入交易记录");

        // User Options translations
        chinese.put("current_username", "当前用户名");
        chinese.put("reset_username", "重置用户名");
        chinese.put("new_username", "新用户名");
        chinese.put("save", "保存");
        chinese.put("error", "错误");
        chinese.put("success", "成功");
        chinese.put("username_empty", "用户名不能为空！");
        chinese.put("username_updated", "用户名已更新！");
        chinese.put("username_update_failed", "更新用户名失败！");
        chinese.put("reset_security_question", "重置安全问题");
        chinese.put("security_question_1", "你最喜欢的书是什么？");
        chinese.put("security_question_2", "你母亲的娘家姓是什么？");
        chinese.put("security_question_3", "你最好的朋友叫什么名字？");
        chinese.put("security_question_4", "你在哪个城市出生？");
        chinese.put("your_answer", "你的答案：");
        chinese.put("answer_empty", "答案不能为空！");
        chinese.put("security_question_updated", "安全问题已更新！");
        chinese.put("security_question_update_failed", "更新安全问题失败！");
        chinese.put("reset_password", "重置密码");

        // About page translations
        chinese.put("about_finanger", "关于 Finanger");
        chinese.put("about_description",
                "Finanger 是一款由人工智能驱动的个人理财管理工具，旨在帮助您轻松掌控财务。无论您是手动输入交易还是导入文件，" +
                        "Finanger 都能将所有内容有条理地组织在一起。\n\n" +
                        "通过智能 AI 技术，Finanger 可以自动对您的支出进行分类，检测消费模式，并提供个性化的预算和储蓄建议。" +
                        "当然，您始终掌握控制权 — 随时可以查看和调整任何分类错误。\n\n" +
                        "更智能的理财从这里开始。使用 Finanger，您不仅仅是追踪资金 — 您正在掌握它。");

        // Export Report translations
        chinese.put("select_date_range", "选择日期范围");
        chinese.put("start_date", "开始日期");
        chinese.put("end_date", "结束日期");
        chinese.put("to", "至");
        chinese.put("export_success", "财务报表导出成功！");
        chinese.put("export_failed", "导出报告失败");

        // Transaction Scene translations
        chinese.put("manual_import", "手动导入：");
        chinese.put("transaction_date", "交易日期");
        chinese.put("select_date", "选择日期");
        chinese.put("transaction_type", "交易类型");
        chinese.put("income", "收入");
        chinese.put("expense", "支出");
        chinese.put("currency", "货币");
        chinese.put("amount", "金额");
        chinese.put("enter_amount", "请输入金额");
        chinese.put("description", "描述");
        chinese.put("enter_description", "输入交易描述");
        chinese.put("auto_sorting", "自动分类");
        chinese.put("category", "类别");
        chinese.put("category_example", "例如：工资、房租、水电费");
        chinese.put("payment_method", "支付方式");
        chinese.put("payment_method_example", "例如：现金、支付宝、银行转账");
        chinese.put("submit", "提交");
        chinese.put("warning", "警告");
        chinese.put("enter_description_first", "请先输入描述");
        chinese.put("auto_sort_failed", "自动分类失败");
        chinese.put("missing_info", "信息不完整");
        chinese.put("fill_all_blanks", "请填写所有必填项");
        chinese.put("invalid_date", "无效日期");
        chinese.put("invalid_date_format", "请输入有效的日期格式 yyyy-MM-dd\n月份必须在 1-12 之间，日期必须在 1-31 之间");
        chinese.put("invalid_category", "无效类别");
        chinese.put("category_english_only", "类别只能包含英文字母");
        chinese.put("invalid_payment_method", "无效支付方式");
        chinese.put("payment_method_english_only", "支付方式只能包含英文字母");
        chinese.put("invalid_amount", "无效金额");
        chinese.put("enter_valid_number", "请输入有效的数字");
        chinese.put("transaction_added", "交易已添加");
        chinese.put("transaction_success", "交易已成功添加。");
        chinese.put("file_import", "文件导入：");
        chinese.put("select_file", "选择文件");
        chinese.put("reference_template", "参考模板");
        chinese.put("save_template_csv", "保存 Excel 模板");
        chinese.put("template_not_found", "在资源文件夹中未找到模板文件。");
        chinese.put("download_complete", "下载完成");
        chinese.put("template_download_success", "Excel 模板下载成功！");
        chinese.put("download_failed", "下载失败");
        chinese.put("template_download_failed", "下载模板失败");
        chinese.put("csv_format_guide",
                "您的文件（支持CSV UTF-8、CSV、Excel文件）应包含以下列：\n\n" +
                        "交易日期\n" +
                        "（格式：YYYY-MM-DD，例如 2025-03-15）\n\n" +
                        "交易类型\n" +
                        "（仅限：收入 / 支出）\n\n" +
                        "货币\n" +
                        "（货币类型，例如 CNY、USD）\n\n" +
                        "金额\n" +
                        "（数字格式，例如 1234.56）\n\n" +
                        "描述\n" +
                        "（交易描述）\n\n" +
                        "类别\n" +
                        "（收入和支出类别）\n\n" +
                        "支付方式\n" +
                        "（支付方式）");

        // File type filter translations
        chinese.put("csv_utf8_filter", "CSV UTF-8 (逗号分隔) (*.csv)");
        chinese.put("csv_filter", "CSV (逗号分隔) (*.csv)");
        chinese.put("excel_filter", "Excel 文件 (*.xlsx)");
        chinese.put("all_supported_filter", "所有支持的文件 (*.csv, *.xlsx)");

        // Status Scene translations
        chinese.put("income_and_expenses", "收支情况");
        chinese.put("start_date", "开始日期");
        chinese.put("end_date", "结束日期");
        chinese.put("select_start_date", "选择开始日期");
        chinese.put("select_end_date", "选择结束日期");
        chinese.put("chart_type", "图表类型");
        chinese.put("line_graph", "折线图");
        chinese.put("bar_graph", "柱状图");
        chinese.put("date", "日期");
        chinese.put("amount", "金额");
        chinese.put("ex_in_trend", "收支趋势");
        chinese.put("category_proportion", "类别占比分析");
        chinese.put("recent_transactions", "最近交易");
        chinese.put("manage_transactions", "管理所有交易");
        chinese.put("ask_ai_assistant", "询问 AI 助手：");
        chinese.put("type_question", "输入您的问题...");
        chinese.put("suggestion", "建议");
        chinese.put("more", "更多>");
        chinese.put("chat_history", "聊天历史");
        chinese.put("you", "您");
        chinese.put("assistant", "助手");

        // Transaction Management translations
        chinese.put("transactions", "交易");
        chinese.put("manage_transactions", "管理您的交易");
        chinese.put("filter", "筛选");
        chinese.put("reset_filter", "重置筛选");
        chinese.put("all", "全部");
        chinese.put("action", "操作");
        chinese.put("delete", "删除");

        // Add export report translations
        chinese.put("financial_report", "财务报表");
        chinese.put("user", "用户");
        chinese.put("date_range", "日期范围");
        chinese.put("to", "至");
        chinese.put("income_and_expenses", "收支趋势");
        chinese.put("category_proportion", "支出类别占比");
        chinese.put("financial_summary", "财务摘要");
        chinese.put("total_income", "总收入");
        chinese.put("total_expense", "总支出");
        chinese.put("net_balance", "净余额");
        chinese.put("top_expense_category", "主要支出类别");
        chinese.put("transaction_details", "交易明细");
        chinese.put("date", "日期");
        chinese.put("transaction_type", "类型");
        chinese.put("amount", "金额");
        chinese.put("currency", "货币");
        chinese.put("category", "类别");
        chinese.put("payment_method", "支付方式");
        chinese.put("income", "收入");
        chinese.put("expense", "支出");

        translations.put("Chinese", chinese);

        // Goals page translations
        english.put("goals", "Goals");
        english.put("no_goals_found", "No goals found. Create your first goal!");
        english.put("create_new_goal", "Create a new goal");
        english.put("click_to_create", "Click to create");
        english.put("new_financial_goal", "a new financial goal");
        english.put("target_amount", "Target Amount");
        english.put("deadline", "Deadline");
        english.put("current_savings", "Current Savings");
        english.put("total_debt_amount", "Total Debt Amount");
        english.put("repayment_deadline", "Repayment Deadline");
        english.put("amount_paid", "Amount Paid");
        english.put("budget_category", "Budget Category");
        english.put("budget_amount", "Budget Amount");
        english.put("current_expenses", "Current Expenses");
        english.put("general", "General");
        english.put("delete_goal", "Delete Goal");
        english.put("delete_goal_header", "Delete");
        english.put("delete_goal_confirmation",
                "Are you sure you want to delete this goal? This action cannot be undone.");
        english.put("failed_to_delete_goal", "Failed to delete goal");
        english.put("error_loading_transaction", "Error loading transaction data");

        // Chinese translations for Goals page
        chinese.put("goals", "目标");
        chinese.put("no_goals_found", "未找到目标。创建您的第一个目标！");
        chinese.put("create_new_goal", "创建新目标");
        chinese.put("click_to_create", "点击创建");
        chinese.put("new_financial_goal", "新的财务目标");
        chinese.put("target_amount", "目标金额");
        chinese.put("deadline", "截止日期");
        chinese.put("current_savings", "当前储蓄");
        chinese.put("total_debt_amount", "总债务金额");
        chinese.put("repayment_deadline", "还款截止日期");
        chinese.put("amount_paid", "已支付金额");
        chinese.put("budget_category", "预算类别");
        chinese.put("budget_amount", "预算金额");
        chinese.put("current_expenses", "当前支出");
        chinese.put("general", "通用");
        chinese.put("delete_goal", "删除目标");
        chinese.put("delete_goal_header", "删除");
        chinese.put("delete_goal_confirmation", "确定要删除此目标吗？此操作无法撤销。");
        chinese.put("failed_to_delete_goal", "删除目标失败");
        chinese.put("error_loading_transaction", "加载交易数据时出错");

        // Create Goal Scene translations
        english.put("saving_goal", "Saving Goal");
        english.put("debt_repayment_goal", "Debt Repayment Goal");
        english.put("budget_control_goal", "Budget Control Goal");
        english.put("goal_type", "Type of your goal:");
        english.put("goal_title", "Goal title:");
        english.put("goal_title_prompt", "Goal Title");
        english.put("target_amount_prompt", "Target Amount");
        english.put("category_prompt", "Category (for Budget Control)");
        english.put("save_goal", "Save Goal");
        english.put("cancel", "Cancel");

        // Chinese translations for Create Goal Scene
        chinese.put("saving_goal", "储蓄目标");
        chinese.put("debt_repayment_goal", "债务还款目标");
        chinese.put("budget_control_goal", "预算控制目标");
        chinese.put("goal_type", "目标类型：");
        chinese.put("goal_title", "目标标题：");
        chinese.put("goal_title_prompt", "目标标题");
        chinese.put("target_amount_prompt", "目标金额");
        chinese.put("category_prompt", "类别（用于预算控制）");
        chinese.put("save_goal", "保存目标");
        chinese.put("cancel", "取消");

        // Edit Goal Scene translations
        english.put("edit_goal", "Edit Goal");
        english.put("goal_type_cannot_modify", "Goal type cannot be modified");
        english.put("save_changes", "Save Changes");
        english.put("failed_to_update_goal", "Failed to update goal");

        // Chinese translations for Edit Goal Scene
        chinese.put("edit_goal", "编辑目标");
        chinese.put("goal_type_cannot_modify", "目标类型无法修改");
        chinese.put("save_changes", "保存更改");
        chinese.put("failed_to_update_goal", "更新目标失败");

        // 添加欢迎消息的翻译
        english.put("welcome_message",
                "Welcome to use the financial assistant. Please feel free to ask any financial questions you have.");
        chinese.put("welcome_message", "欢迎使用财务助手。请随时询问任何财务相关的问题。");
    }

    /**
     * Gets the currently selected language.
     *
     * @return The current language code (e.g., "English", "Chinese")
     */
    public String getCurrentLanguage() {
        return currentLanguage;
    }

    /**
     * Sets the current language for the application.
     * This will affect all subsequent translation requests.
     *
     * @param language The language code to set (e.g., "English", "Chinese")
     */
    public void setCurrentLanguage(String language) {
        if (translations.containsKey(language)) {
            this.currentLanguage = language;
        }
    }

    /**
     * Retrieves the translation for a given key in the current language.
     * If the key is not found, returns the key itself.
     *
     * @param key The translation key to look up
     * @return The translated string for the given key, or the key itself if not
     *         found
     */
    public String getTranslation(String key) {
        Map<String, String> currentTranslations = translations.get(currentLanguage);
        return currentTranslations != null ? currentTranslations.getOrDefault(key, key) : key;
    }
}