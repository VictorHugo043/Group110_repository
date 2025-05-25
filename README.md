# Group110_repository

## Members:

| Name          | BUPT ID    | QM ID    | Email                        | Username            |
|--------------|------------|-----------|-----------------------------|-----------------------------|
| Zhihan Liu   | 2022213506 | 221170836 | loveyuban@gmail.com         | Nora-LL / Aya              |
| Pengyi Liu   | 2022213512 | 221170881 | 2654753356@qq.com          | Effy4869                   |
| Zhengxu Li   | 2022213494 | 221170711 | jp2022213494@qmul.ac.uk    | ZhengxuLi96 / Li           |
| Xiangwei Gong| 2022213490 | 221170674 | luofeng2333@gmail.com      | VictorHugo043 / Fallenwind |
| Xinyu Shao   | 2022213511 | 221170870 | 2848812310@qq.com         | ROOOBUE / sstarsheep       |
| Ruixuan MA   | 2022213514 | 221170906 | 2397451568@qq.com         | MaRuixuan                  |


---

## **Finanger - Personal Finance Management Tool**

**Finanger** is an intelligent AI-driven personal finance management software that helps users easily manage income, expenses, savings goals, and provides visual analytics along with AI-powered financial advice.

---

## **Key Features**

- **Financial Overview**  
  - Interactive line and bar charts for income/expense trends
  - Pie chart for category proportion analysis
  - Customizable date range selection
  - Real-time financial status updates

- **Goal Management**  
  - Multiple goal types: Savings, Debt Repayment, Budget Control
  - Visual progress tracking with progress indicators
  - Goal creation and editing interface
  - Support for multiple currencies (CNY, USD, EUR, JPY, GBP)

- **Transaction Management**  
  - Comprehensive transaction table with filtering and sorting
  - Multiple filter options: date, type, currency, category, payment method
  - Bulk import functionality
  - Transaction categorization and tagging

- **AI Assistant**  
  - Real-time financial advice and insights
  - Natural language interaction
  - Personalized spending analysis
  - Smart transaction categorization

- **Export Financial Reports**  
  - Customizable report formats
  - Multiple export options
  - Detailed transaction history
  - Financial summary reports

- **Status Summary**  
  - Real-time financial metrics
  - Income/Expense balance tracking
  - Category-wise spending analysis
  - Recent transaction history

- **User Manual & Help**  
  - Comprehensive documentation
  - Interactive tutorials
  - Feature guides
  - Troubleshooting assistance

- **Personalized Settings**  
  - Multi-language support
  - Dark/Light theme switching
  - Currency preferences
  - Export format customization
  - Window size preferences

- **Secure Account Management**  
  - Encrypted data storage
  - Secure password hashing
  - User authentication
  - Session management

- **Legal Compliance**  
  - Privacy Policy
  - Terms of Use
  - Data protection measures
  - User agreement acceptance

---

## **Installation Instructions**

1. **Prerequisites**
   - Ensure Java 21 is installed
     - Windows: Download and install from [Oracle website](https://www.oracle.com/java/technologies/downloads/#java21)
     - Verify installation: Run `java -version` in terminal
   - Install Maven 3.8.6 or later
     - Windows: Download from [Maven website](https://maven.apache.org/download.cgi)
     - Set up environment variables:
       - Add `MAVEN_HOME` pointing to Maven installation directory
       - Add `%MAVEN_HOME%\bin` to `PATH` environment variable
     - Verify installation: Run `mvn -version` in terminal
   - Ensure system environment variables are correctly set:
     - `JAVA_HOME` pointing to Java installation directory
     - `PATH` containing Java and Maven bin directories

2. **Project Configuration**
   - Clone the repository:
     ```bash
     git clone https://github.com/your-username/Group110_repository.git
     ```
   - Navigate to project directory:
     ```bash
     cd Group110_repository
     ```
   - Check if dependency versions in `pom.xml` are compatible with your environment
   - Ensure all dependencies can be downloaded (may need to configure Maven mirror)

3. **Build Project**
   ```bash
   # Standard build (with tests)
   mvn clean install
   
   # Quick build (skip tests)
   mvn clean install -DskipTests
   
   # Run tests in headless mode (recommended for CI/CD)
   mvn clean install -Dtestfx.headless=true
   
   # If encountering dependency download issues, try using local repository
   mvn clean install -Dmaven.repo.local=./m2/repository
   
   # If tests fail, try running tests separately
   mvn clean test -Dtestfx.headless=true
   ```

   Notes:
   - During the first build, some tests might fail due to JavaFX test environment initialization
   - If tests fail, you can:
     1. Try running the build command again as the JavaFX environment might need time to initialize properly
     2. If still failing, run `mvn clean install -DskipTests` to skip tests
     3. Then run `mvn test -Dtestfx.headless=true` to run tests separately
   - Ensure JavaFX environment is properly configured
   - If using an IDE (like IntelliJ IDEA), configure VM options: `--add-modules javafx.controls,javafx.fxml`
   - Recommended IDE: IntelliJ IDEA is recommended for this project as it handles JavaFX test environment initialization better than VSCode, reducing the likelihood of first-build test failures

4. **Run Application**
   ```bash
   # Run using Maven
   mvn javafx:run
   
   # Or run jar file directly (in target directory)
   java -jar target/finanger-1.0-SNAPSHOT.jar
   ```

5. **Troubleshooting**
   - If encountering JavaFX related errors:
     - Ensure complete JDK is installed (including JavaFX)
     - Check JavaFX dependency configuration in `pom.xml`
   - If encountering dependency download failures:
     - Check network connection
     - Configure Maven mirror (recommended: Aliyun mirror)
   - If encountering insufficient memory errors:
     - Increase JVM memory parameter: `mvn javafx:run -Djavafx.memory=2g`

6. **Development Environment Setup**
   - Recommended IDEs: IntelliJ IDEA or Eclipse
   - Ensure correct JDK version (Java 21) is configured in IDE
   - Import project as Maven project
   - Verify all dependencies are downloaded before running

---

## **Software Dependencies**

| Dependency                              | Version     | Purpose                                    |
|-----------------------------------------|-------------|--------------------------------------------|
| **Java**                                | `21`        | Core runtime environment                   |
| **Maven**                               | `3.8.6`     | Build and dependency management           |
| **JavaFX Controls** (`javafx-controls`) | `21`        | UI components and controls                |
| **JavaFX FXML** (`javafx-fxml`)         | `21`        | UI layout and design                      |
| **JavaFX Swing** (`javafx-swing`)       | `21`        | Legacy UI component support               |
| **JavaFX Web** (`javafx-web`)           | `21`        | Web content rendering                     |
| **Gson** (`gson`)                       | `2.10.1`    | JSON data processing                      |
| **Jackson Databind** (`jackson-databind`) | `2.13.0`  | Advanced JSON processing                  |
| **Jackson JSR310** (`jackson-datatype-jsr310`) | `2.13.0` | Date/time handling in JSON |
| **SLF4J API** (`slf4j-api`)             | `1.7.36`    | Logging framework                         |
| **Logback Classic** (`logback-classic`) | `1.2.11`    | Logging implementation                    |
| **JSON** (`org.json`)                   | `20231013`  | JSON data handling                        |
| **BC Provider** (`bcprov-jdk18on`)     | `1.77`      | Cryptography and security                 |
| **Argon2 for JVM** (`argon2-jvm`)       | `2.11`      | Password hashing                          |
| **Flexmark Markdown Parser** (`flexmark-all`) | `0.64.8` | Documentation rendering      |
| **iText 7 Core** (`itext7-core`)        | `7.2.5`     | PDF report generation                     |

### **Test Dependencies**

| Dependency                               | Version     | Purpose                                    |
|------------------------------------------|-------------|--------------------------------------------|
| **JUnit 4** (`junit`)                    | `4.13.2`    | Legacy unit testing                       |
| **JUnit 5** (`junit-jupiter`)            | `RELEASE`   | Modern unit testing framework             |
| **Mockito Core** (`mockito-core`)        | `5.7.0`     | Mocking framework                         |
| **Mockito JUnit** (`mockito-junit-jupiter`) | `5.8.0` | JUnit 5 integration for Mockito          |
| **TestFX Core** (`testfx-core`)          | `4.0.18`    | JavaFX UI testing                         |
| **TestFX JUnit5** (`testfx-junit5`)      | `4.0.18`    | JUnit 5 integration for TestFX           |
| **TestFX JUnit** (`testfx-junit`)        | `4.0.18`    | JUnit 4 integration for TestFX           |


---

## **Project Structure**
```
Finanger/
│
├── src/
│   ├── main/
│   │   ├── java/com/myfinanceapp/
│   │   │   ├── model/                 # Data models and entities
│   │   │   │   ├── Goal.java         # Goal data model
│   │   │   │   ├── Transaction.java  # Transaction data model
│   │   │   │   └── User.java         # User data model
│   │   │   │
│   │   │   ├── security/             # Security and encryption
│   │   │   │   ├── EncryptionConfig.java
│   │   │   │   ├── EncryptionService.java
│   │   │   │   └── PasswordHashService.java
│   │   │   │
│   │   │   ├── service/              # Business logic services
│   │   │   │   ├── AIChatService.java
│   │   │   │   ├── AISortingService.java
│   │   │   │   ├── ChartService.java
│   │   │   │   ├── CurrencyService.java
│   │   │   │   ├── GoalService.java
│   │   │   │   ├── LanguageService.java
│   │   │   │   ├── StatusService.java
│   │   │   │   ├── ThemeService.java
│   │   │   │   ├── TransactionService.java
│   │   │   │   └── UserService.java
│   │   │   │
│   │   │   ├── ui/                   # User Interface components
│   │   │   │   ├── common/          # Shared UI components
│   │   │   │   │   ├── AnimationUtils.java
│   │   │   │   │   ├── LeftSidebarFactory.java
│   │   │   │   │   ├── SceneManager.java
│   │   │   │   │   └── SettingsTopBarFactory.java
│   │   │   │   │
│   │   │   │   ├── goalsscene/      # Goal management UI
│   │   │   │   │   ├── CreateGoalScene.java
│   │   │   │   │   ├── EditGoalScene.java
│   │   │   │   │   └── Goals.java
│   │   │   │   │
│   │   │   │   ├── loginscene/      # Login and authentication UI
│   │   │   │   │   └── LoginScene.java
│   │   │   │   │
│   │   │   │   ├── mainwindow/      # Main application window
│   │   │   │   │   └── MainWindow.java
│   │   │   │   │
│   │   │   │   ├── registrationterms/ # Legal agreements UI
│   │   │   │   │   ├── PrivacyPolicy.java
│   │   │   │   │   └── TermOfUse.java
│   │   │   │   │
│   │   │   │   ├── settingscene/    # Settings and preferences UI
│   │   │   │   │   ├── About.java
│   │   │   │   │   └── SystemSettings.java
│   │   │   │   │
│   │   │   │   ├── signupscene/     # User registration UI
│   │   │   │   │   └── SignUp.java
│   │   │   │   │
│   │   │   │   ├── statusscene/     # Financial status display
│   │   │   │   │   ├── StatusScene.java
│   │   │   │   │   └── StatusService.java
│   │   │   │   │
│   │   │   │   ├── transactionscene/ # Transaction management UI
│   │   │   │   │   ├── TransactionManagementScene.java
│   │   │   │   │   └── TransactionScene.java
│   │   │   │   │
│   │   │   │   └── usermanual/      # Help and documentation UI
│   │   │   │       └── UserManual.java
│   │   │   │
│   │   │   └── FinanceApp.java      # Application entry point
│   │   │
│   │   └── resources/
│   │       ├── css/                 # Stylesheets
│   │       │   ├── manual-style.css
│   │       │   └── markdown-day.css
│   │       │
│   │       ├── goals/              # Goal data storage
│   │       │   └── *.json
│   │       │
│   │       ├── pictures/           # UI assets and images
│   │       │   ├── logo_day.png
│   │       │   └── logo_night.png
│   │       │
│   │       ├── template/          # Report templates
│   │       │   └── *.html
│   │       │
│   │       ├── terms/             # Legal documents
│   │       │   ├── PrivacyPolicy.txt
│   │       │   └── TermOfUse.txt
│   │       │
│   │       ├── transaction/       # Transaction data storage
│   │       │   └── *.json
│   │       │
│   │       └── users.json         # User data storage
│   │
│   └── test/
│       └── java/com/myfinanceapp/
│           ├── model/             # Model unit tests
│           │   ├── GoalTest.java
│           │   ├── TransactionTest.java
│           │   └── UserTest.java
│           │
│           ├── security/          # Security component tests
│           │   ├── EncryptionServiceTest.java
│           │   └── PasswordHashServiceTest.java
│           │
│           ├── service/           # Service layer tests
│           │   ├── AIChatServiceTest.java
│           │   ├── GoalServiceTest.java
│           │   ├── TransactionServiceTest.java
│           │   └── UserServiceTest.java
│           │
│           └── ui/               # UI component tests
│               ├── common/
│               ├── goalsscene/
│               ├── loginscene/
│               ├── statusscene/
│               └── transactionscene/
│
├── target/                      # Compiled output
├── .gitignore                  # Git ignore rules
├── pom.xml                     # Maven configuration
└── README.md                   # Project documentation
```

## **License**

This project is licensed under the MIT License - see the LICENSE file for details.

## **Contact**

For any questions or concerns, please contact the team members listed above or create an issue in the repository.

