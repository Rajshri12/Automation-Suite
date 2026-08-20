<div align="center">

# 🧪 Automation-Suite

**End-to-End Test Automation Framework for Web Applications & Microservices**

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen?style=for-the-badge&logo=github-actions)](https://github.com/Rajshri12/Automation-Suite/actions)
[![Java](https://img.shields.io/badge/Java-11-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/11/)
[![Selenium](https://img.shields.io/badge/Selenium-4.15-43B02A?style=for-the-badge&logo=selenium&logoColor=white)](https://www.selenium.dev/)
[![TestNG](https://img.shields.io/badge/TestNG-7.8-FF6C37?style=for-the-badge)](https://testng.org/)
[![RestAssured](https://img.shields.io/badge/RestAssured-5.3-4A90E2?style=for-the-badge)](https://rest-assured.io/)
[![Maven](https://img.shields.io/badge/Maven-3.9-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)](https://maven.apache.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)](LICENSE)

<br/>

> *Most projects test UI and API in silos. This framework validates complete business workflows — one action on the UI gets confirmed through the API, and every API state change is reflected correctly in the browser.*

<br/>

</div>

---

## 📌 Overview

Automation-Suite is a multi-layer test automation framework built in Java. It covers three distinct testing concerns under a single Maven project:

| Layer | Tool | What it validates |
|-------|------|--------------------|
| **UI** | Selenium + TestNG | Browser interactions, visual flows, form validation |
| **API** | RestAssured + TestNG | HTTP contracts, status codes, payload schema |
| **E2E** | Selenium + RestAssured | Full business workflows crossing both layers |

The key principle: **UI tests and API tests don't live in separate repos.** A single E2E test can log in via the REST API, navigate the browser to a settings page, create a config via the UI, then assert the backend persists it correctly — all in one test method.

---

## 🏗️ System Architecture

```mermaid
flowchart TD
    subgraph Entry["🚀 Test Entry Point"]
        A["testng.xml\nSuite Definition"]
    end

    subgraph UILayer["🖥️ UI Layer — Selenium WebDriver"]
        B["LoginPage"]
        C["DashboardPage"]
        D["ConfigPage"]
        E["BaseTest\nWebDriver lifecycle"]
        B & C & D --> E
    end

    subgraph APILayer["🔌 API Layer — RestAssured"]
        F["AuthApiTest\nPOST /auth/login"]
        G["ConfigApiTest\nGET /config"]
        H["ReportApiTest\nGET /report"]
    end

    subgraph E2ELayer["🔄 E2E Layer — Cross-Validation"]
        I["EndToEndWorkflowTest\nUI action → API assertion"]
    end

    subgraph CI["⚙️ CI/CD"]
        J["Jenkins Pipeline"]
        K["GitHub Actions"]
    end

    A --> UILayer
    A --> APILayer
    A --> E2ELayer

    UILayer --> L["📊 TestNG Reports\n+ Screenshots"]
    APILayer --> L
    E2ELayer --> L
    L --> CI

    CI --> M{Result}
    M -->|✅ Pass| N["Green Build\nNotify Team"]
    M -->|❌ Fail| O["Logs + Screenshots\nArchived"]
```

---

## 🗂️ Page Object Model

Each screen in the application has exactly one Java class. Tests interact with methods, not with raw Selenium locators. When a locator changes, you edit one file — not every test that touches that screen.

```mermaid
flowchart LR
    subgraph Tests["Test Classes"]
        T1["LoginTest"]
        T2["EndToEndTest"]
    end

    subgraph Pages["Page Objects"]
        P1["LoginPage\n─────────────\nenterUsername()\nenterPassword()\nclickLogin()\ngetErrorMessage()"]
        P2["DashboardPage\n─────────────\nisLoaded()\ngetWelcomeMessage()\nnavigateToConfig()"]
        P3["ConfigPage\n─────────────\nenterConfigName()\nenterConfigValue()\nsaveConfig()"]
    end

    subgraph Base["Infrastructure"]
        B["BaseTest\n─────────────\n@BeforeMethod setUp()\n@AfterMethod tearDown()\nWebDriverManager"]
    end

    T1 --> P1
    T1 --> P2
    T2 --> P1
    T2 --> P2
    T2 --> P3
    P1 & P2 & P3 --> B
```

---

## 🔌 API Test Coverage Map

```mermaid
flowchart TD
    subgraph Auth["POST /api/auth/login"]
        A1["✅ 200 — valid credentials\ntoken returned"]
        A2["❌ 401 — wrong password\nerror message returned"]
        A3["❌ 400 — missing field\nvalidation error"]
        A4["❌ 403 — locked account\naccess denied"]
    end

    subgraph Config["GET /api/config"]
        C1["✅ 200 — valid Bearer token\nconfiguration payload"]
        C2["❌ 401 — no token\nunauthorized"]
        C3["❌ 403 — expired token\nforbidden"]
    end

    subgraph Design["Design Rule"]
        D["Every endpoint tested with:\n• 1 happy path\n• 2+ negative paths\n• edge cases where relevant"]
    end

    Auth & Config --> Design
```

---

## 🔄 End-to-End Sequence

The E2E test validates that what a user does in the browser is accurately persisted and queryable through the API. This catches integration bugs that neither UI-only nor API-only tests can find.

```mermaid
sequenceDiagram
    participant T as E2E Test
    participant B as Browser (Selenium)
    participant A as REST API

    Note over T: Step 1 — Authenticate
    T->>B: Navigate to /login
    T->>B: enterUsername() + enterPassword() + clickLogin()
    B-->>T: Dashboard loaded ✅

    Note over T: Step 2 — Create config via UI
    T->>B: navigateToConfig()
    T->>B: enterConfigName("E2E_Config_123")
    T->>B: saveConfig()
    B-->>T: Toast: "Configuration saved" ✅

    Note over T: Step 3 — Validate via API
    T->>A: POST /api/auth/login → get JWT
    T->>A: GET /api/config?name=E2E_Config_123
    A-->>T: 200 + { value: "automated-test-value" }

    Note over T: Step 4 — Cross-assert
    T->>T: Assert API value == UI input ✅
```

---

## ⚙️ Jenkins CI Pipeline

```mermaid
flowchart LR
    A(["👨‍💻 Developer\nPushes Code"]) --> B["Git Trigger"]
    B --> C["📥 Checkout\ngit clone"]
    C --> D["🔨 Build\nmvn clean compile"]
    D --> E["🧪 Run Suite\nmvn test"]
    E --> F{"All Tests\nPassed?"}

    F -->|Yes ✅| G["📊 Publish TestNG Report"]
    F -->|No ❌| H["📎 Archive Logs\n+ Screenshots"]

    G --> I(["🟢 Green Build\nTeam Notified"])
    H --> J(["🔴 Failed Build\nDev Notified"])
```

---

## 📁 Project Structure

```
Automation-Suite/
│
├── 📄 pom.xml                          ← Maven deps (Selenium, TestNG, RestAssured)
├── 📄 Jenkinsfile                       ← Declarative CI pipeline
│
├── src/test/
│   ├── java/
│   │   ├── base/
│   │   │   └── BaseTest.java           ← WebDriver init, teardown, screenshots
│   │   ├── pages/                      ← Page Object Model
│   │   │   ├── LoginPage.java
│   │   │   ├── DashboardPage.java
│   │   │   └── ConfigPage.java
│   │   ├── tests/
│   │   │   ├── ui/
│   │   │   │   └── LoginTest.java      ← UI test cases (positive + negative)
│   │   │   ├── api/
│   │   │   │   └── AuthApiTest.java    ← API contract tests
│   │   │   └── e2e/
│   │   │       └── EndToEndWorkflowTest.java ← Cross-layer validation
│   │   └── utils/
│   │       └── ConfigReader.java       ← Reads config.properties
│   └── resources/
│       ├── config.properties           ← URLs, credentials, browser
│       └── testng.xml                  ← Suite definition + groups
│
├── docs/
│   └── TEST_STRATEGY.md
└── .github/workflows/ci.yml            ← GitHub Actions (API tests on push)
```

---

## 🚀 Running Tests

```bash
# Clone
git clone https://github.com/Rajshri12/Automation-Suite.git
cd Automation-Suite

# Full suite
mvn clean test

# By layer
mvn clean test -Dgroups=ui
mvn clean test -Dgroups=api
mvn clean test -Dgroups=e2e

# Custom suite XML
mvn clean test -DsuiteXmlFile=src/test/resources/testng.xml
```

---

## ⚙️ Configuration

`src/test/resources/config.properties`

```properties
base.url         = http://localhost:3000
api.base.url     = http://localhost:8080
browser          = chrome
implicit.wait    = 10
valid.username   = testuser@example.com
valid.password   = Test@1234
```

---

## 🧪 Test Coverage Summary

| Layer | Test | Type | Status |
|-------|------|------|--------|
| UI | Valid login → dashboard | Positive | ✅ |
| UI | Invalid password → error msg | Negative | ✅ |
| UI | Empty form submit | Negative | ✅ |
| API | `POST /auth/login` 200 | Positive | ✅ |
| API | `POST /auth/login` 401 | Negative | ✅ |
| API | `POST /auth/login` 400 | Negative | ✅ |
| API | `GET /config` no token 403 | Negative | ✅ |
| E2E | Login → create config via UI → validate via API | Workflow | ✅ |

---

## 🤔 Design Decisions

**Why Page Object Model?**
Centralises all locators. When the UI ships a locator change, you update one page class — not every test that touches that screen.

**Why TestNG over JUnit?**
Native grouping (`ui`, `api`, `e2e`), parallel execution config in XML, and data providers are all first-class — no extra libraries needed.

**Why RestAssured?**
The fluent DSL reads almost like a spec: `given().body(payload).when().post("/login").then().statusCode(200).body("token", notNullValue())`. It doubles as documentation.

**Why E2E tests on top of UI + API tests?**
Both layers can pass individually while the integration breaks. E2E tests exist specifically to catch that gap.

---

## 🗺️ Roadmap

- [x] UI automation with POM
- [x] REST API test suite
- [x] Cross-layer E2E tests
- [x] Jenkins CI pipeline
- [ ] Allure HTML reporting
- [ ] Parallel cross-browser (Chrome + Firefox)
- [ ] Playwright migration path
- [ ] AI-assisted test generation for new endpoints

---

<div align="center">

Made with ☕ and too many `mvn clean test` runs.

</div>
