# Group110_repository

## Members:
### Zhihan Liu,2022213506,221170836,loveyuban@gmail.com (Nora-LL/Aya)
### Pengyi Liu,2022213512,221170881,2654753356@qq.com (Effy4869)
### Zhengxu Li,2022213494,221170711,jp2022213494@qmul.ac.uk (ZhengxuLi96/Li)
### Xiangwei Gong,2022213490,221170674,luofeng2333@gmail.com (VictorHugo043/Fallenwind)
### Xinyu Shao,2022213511,221170870,2848812310@qq.com (ROOOBUE/sstarsheep)
### Ruixuan MA,2022213514,221170906,2397451568@qq.com (MaRuixuan)

---

## **Finanger - Personal Finance Management Tool**

**Finanger** is an intelligent AI-driven personal finance management software that helps users easily manage income, expenses, savings goals, and provides visual analytics along with AI-powered financial advice.

---

## **Key Features**
-  **Financial Overview**: View monthly income and expense trends with graphical analysis
-  **Goal Management**: Set savings goals and track progress
-  **Transaction Management**: Manually enter or bulk import transaction records
-  **AI Assistant**: Provides intelligent suggestions based on spending patterns
-  **Personalized Settings**: Supports multiple languages, dark mode, and window resizing
-  **Account Management**: Supports account registration, login, password recovery, and more

---

## **Software Dependencies**
| Dependency | Version  |
|------------|----------|
| **Java** | `21`     |
|**maven** | `3.8.6`  |
| **JavaFX Controls** (`javafx-controls`) | `21`     |
| **JavaFX FXML** (`javafx-fxml`) | `21`     |
| **Gson** (`gson`) | `2.10.1` |
| **Jackson Databind** (`jackson-databind`) | `2.13.0` |
| **Jackson JSR310** (`jackson-datatype-jsr310`) | `2.13.0` |
| **SLF4J API** (`slf4j-api`) | `1.7.36` |
| **Logback Classic** (`logback-classic`) | `1.2.11` |

---

## **Project Structure**
```bash
Finanger/
│── src/main/java/com.myfinanceapp/
│   ├── model/              # Data models (User, Transaction, Goal)
│   ├── service/            # Business logic (AiChatService, TransactionService, etc.)
│   ├── ui/
│   │   ├── loginscene/      # Login & password reset page
│   │   ├── goalsscene/      # Goal management page
│   │   ├── settingscene/    # Settings page
│   │   ├── transactionscene # Transaction management page
│   ├── mainwindow/         # Main application window
│── resources/
│   ├── pictures/           # UI-related image assets
│   ├── transaction/        # Transaction data (JSON)
│── pom.xml                 # Maven configuration file
│── README.md               # Documentation
```

