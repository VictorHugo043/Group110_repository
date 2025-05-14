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
  View monthly income and expense trends through interactive charts and graphs.

- **Goal Management**  
  Set, edit, and monitor savings goals with dedicated goal creation and progress tracking interfaces.

- **Transaction Management**  
  Manually input transactions or import them in bulk. Manage and visualize financial activity in an organized UI.

- **AI Assistant**  
  Receive personalized suggestions and spending insights based on your financial habits using the built-in `AIChatService` and `AISortingService`.

- **Export Financial Reports**  
  Generate and export detailed financial reports for personal tracking or sharing.

- **Status Summary**  
  Quickly view your financial status and summaries via the `StatusScene` interface.

- **User Manual & Help**  
  Built-in help and guidance system through a dedicated `UserManual` section.

- **Personalized Settings**  
  Customize your experience with support for:
    - Multiple languages
    - Dark mode
    - Export preferences
    - Window resizing

- **Secure Account Management**  
  Secure user registration, login, and password recovery using encryption and password hashing (`EncryptionService`, `PasswordHashService`).

- **Legal Compliance**  
  Clearly presented Privacy Policy and Terms of Use are shown during account registration (`PrivacyPolicy`, `TermOfUse`).

---

## **Software Dependencies**

| Dependency                              | Version     |
|-----------------------------------------|-------------|
| **Java**                                | `21`        |
| **Maven**                               | `3.8.6`     |
| **JavaFX Controls** (`javafx-controls`) | `21`        |
| **JavaFX FXML** (`javafx-fxml`)         | `21`        |
| **JavaFX Swing** (`javafx-swing`)       | `21`        |
| **JavaFX Web** (`javafx-web`)           | `21`        |
| **Gson** (`gson`)                       | `2.10.1`    |
| **Jackson Databind** (`jackson-databind`) | `2.13.0`  |
| **Jackson JSR310** (`jackson-datatype-jsr310`) | `2.13.0` |
| **SLF4J API** (`slf4j-api`)             | `1.7.36`    |
| **Logback Classic** (`logback-classic`) | `1.2.11`    |
| **JSON** (`org.json`)                   | `20231013`  |
| **BC Provider** (`bcprov-jdk18on`)     | `1.77`      |
| **Argon2 for JVM** (`argon2-jvm`)       | `2.11`      |
| **Flexmark Markdown Parser** (`flexmark-all`) | `0.64.8` |
| **iText 7 Core** (`itext7-core`)        | `7.2.5`     |

### **Test Dependencies**

| Dependency                               | Version     |
|------------------------------------------|-------------|
| **JUnit 4** (`junit`)                    | `4.13.2`    |
| **JUnit 5** (`junit-jupiter`)            | `RELEASE`   |
| **Mockito Core** (`mockito-core`)        | `5.7.0`     |
| **Mockito JUnit** (`mockito-junit-jupiter`) | `5.8.0` |
| **TestFX Core** (`testfx-core`)          | `4.0.18`    |
| **TestFX JUnit5** (`testfx-junit5`)      | `4.0.18`    |
| **TestFX JUnit** (`testfx-junit`)        | `4.0.18`    |


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
│   │   │   │   ├── TransactionService.java
│   │   │   │   └── UserService.java
│   │   │   │
│   │   │   ├── ui/                   # User Interface components
│   │   │   │   ├── common/          # Shared UI components
│   │   │   │   ├── goalsscene/      # Goal management UI
│   │   │   │   ├── loginscene/      # Login and authentication UI
│   │   │   │   ├── mainwindow/      # Main application window
│   │   │   │   ├── registrationterms/ # Legal agreements UI
│   │   │   │   ├── settingscene/    # Settings and preferences UI
│   │   │   │   ├── signupscene/     # User registration UI
│   │   │   │   ├── statusscene/     # Financial status display
│   │   │   │   ├── transactionscene/ # Transaction management UI
│   │   │   │   └── usermanual/      # Help and documentation UI
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
│   │       │
│   │       ├── terms/             # Legal documents
│   │       │   ├── PrivacyPolicy.txt
│   │       │   └── TermOfUse.txt
│   │       │
│   │       └── transaction/       # Transaction data storage
│   │           └── *.json
│   │
│   └── test/
│       └── java/com/myfinanceapp/
│           ├── model/             # Model unit tests
│           ├── security/          # Security component tests
│           ├── service/           # Service layer tests
│           ├── ui/               # UI component tests
│           └── FinanceAppTest.java
│
├── target/                      # Compiled output
├── .gitignore                  # Git ignore rules
├── pom.xml                     # Maven configuration
└── README.md                   # Project documentation
```

## **Installation Instructions**

1. **Prerequisites**
   - Ensure you have Java 21 installed
   - Install Maven 3.8.6 or later
   - Set up your Java environment variables

2. **Build and Run**
   ```bash
   # Clone the repository
   git clone https://github.com/your-username/Group110_repository.git
   
   # Navigate to project directory
   cd Group110_repository
   
   # Build the project
   mvn clean install
   
   # Run the application
   mvn javafx:run
   ```

## **License**

This project is licensed under the MIT License - see the LICENSE file for details.

## **Contact**

For any questions or concerns, please contact the team members listed above or create an issue in the repository.

