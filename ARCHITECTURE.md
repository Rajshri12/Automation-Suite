# Automation-Suite — Architecture Documentation

## Overview

Automation-Suite is structured as a layered test automation framework. The design separates concerns into distinct layers: **configuration**, **infrastructure**, **page objects**, and **tests**. This separation makes the framework maintainable, extensible, and resistant to UI churn.

---

## Layered Architecture

```mermaid
flowchart TB
    subgraph Config["⚙️ Configuration Layer"]
        CP[config.properties]
        CR[ConfigReader.java]
        TNG[testng.xml]
    end

    subgraph Infra["🏗️ Infrastructure Layer"]
        BT[BaseTest.java\nWebDriver Lifecycle]
        WDM[WebDriverManager\nAuto Driver Binaries]
        RA[RestAssured\nHTTP Client]
    end

    subgraph POM["📄 Page Object Layer"]
        LP[LoginPage.java]
        DP[DashboardPage.java]
        CFP[ConfigPage.java]
    end

    subgraph Tests["🧪 Test Layer"]
        subgraph UI["UI Tests"]
            LT[LoginTest.java]
        end
        subgraph API["API Tests"]
            AT[AuthApiTest.java]
        end
        subgraph E2E["E2E Tests"]
            ET[EndToEndWorkflowTest.java]
        end
    end

    subgraph CI["🚀 CI/CD Layer"]
        JNK[Jenkinsfile]
        GHA[.github/workflows/ci.yml]
        MVN[Maven Surefire Plugin]
    end

    CP --> CR
    CR --> BT
    CR --> AT
    WDM --> BT
    BT --> POM
    RA --> AT
    RA --> ET
    POM --> UI
    POM --> E2E
    TNG --> MVN
    MVN --> Tests
    JNK --> MVN
    GHA --> MVN
```

---

## Component Descriptions

### Configuration Layer

| Component | Responsibility |
|---|---|
| `config.properties` | Stores environment-specific values: URLs, credentials, timeouts, browser |
| `ConfigReader.java` | Static utility that loads properties at class init, checks JVM system properties first to allow CI override |
| `testng.xml` | Defines test suites, groups, parallelism, and per-suite parameters |

### Infrastructure Layer

| Component | Responsibility |
|---|---|
| `BaseTest.java` | Manages `WebDriver` and `WebDriverWait` lifecycle via `@BeforeMethod` / `@AfterMethod`; uses `ThreadLocal` for parallel safety |
| `WebDriverManager` | Automatically downloads and caches the correct `chromedriver` / `geckodriver` binary for the host OS |
| `RestAssured` | Provides a fluent DSL for HTTP requests; configured with `baseURI` in API test `@BeforeClass` |

### Page Object Layer

Each class represents one screen of the application:

| Page Object | Mapped Screen | Key Methods |
|---|---|---|
| `LoginPage` | `/login` | `enterUsername()`, `enterPassword()`, `clickLogin()`, `getErrorMessage()` |
| `DashboardPage` | `/dashboard` | `isLoaded()`, `getWelcomeMessage()`, `navigateToConfig()` |
| `ConfigPage` | `/config` | `fillForm()`, `submit()`, `getSuccessMessage()` |

### Test Layer

| Package | Test Type | Runs Against |
|---|---|---|
| `tests.ui` | Browser functional | Web UI via Selenium |
| `tests.api` | HTTP contract | REST API via RestAssured |
| `tests.e2e` | Full workflow | Both UI and API in sequence |

---

## WebDriver Thread Safety

```mermaid
sequenceDiagram
    participant T1 as Thread 1 (Test A)
    participant T2 as Thread 2 (Test B)
    participant TL as ThreadLocal<WebDriver>

    T1->>TL: set(new ChromeDriver())
    T2->>TL: set(new ChromeDriver())

    Note over T1,T2: Both threads run concurrently

    T1->>TL: get() → driver1
    T2->>TL: get() → driver2

    T1->>T1: test actions on driver1
    T2->>T2: test actions on driver2

    T1->>TL: remove() → driver1.quit()
    T2->>TL: remove() → driver2.quit()
```

Each test thread gets its own isolated `WebDriver` instance. Tests never share browser state.

---

## Page Object Model Design

```mermaid
classDiagram
    class BaseTest {
        -ThreadLocal~WebDriver~ driverHolder
        -ThreadLocal~WebDriverWait~ waitHolder
        +setup(browser)
        +tearDown()
        +getDriver() WebDriver
        +getWait() WebDriverWait
    }

    class LoginPage {
        -WebDriver driver
        -WebDriverWait wait
        -By usernameField
        -By passwordField
        -By loginButton
        -By errorMessage
        +enterUsername(String) LoginPage
        +enterPassword(String) LoginPage
        +clickLogin()
        +loginWith(String, String)
        +getErrorMessage() String
        +isLoaded() boolean
    }

    class DashboardPage {
        -WebDriver driver
        -WebDriverWait wait
        -By welcomeMessage
        -By configMenuLink
        -By logoutButton
        +isLoaded() boolean
        +getWelcomeMessage() String
        +navigateToConfig()
        +logout()
    }

    class LoginTest {
        +testValidLogin()
        +testInvalidLogin()
        +testEmptyCredentials()
    }

    class EndToEndWorkflowTest {
        -String authToken
        -String userId
        +step1_apiLoginAndGetToken()
        +step2_uiLogin()
        +step3_createConfigViaUi()
        +step4_validateConfigViaApi()
        +step5_assertReportGeneration()
    }

    BaseTest <|-- LoginTest
    BaseTest <|-- EndToEndWorkflowTest
    LoginTest ..> LoginPage : creates
    LoginTest ..> DashboardPage : creates
    EndToEndWorkflowTest ..> LoginPage : creates
    EndToEndWorkflowTest ..> DashboardPage : creates
```

---

## API Testing Architecture

```mermaid
flowchart LR
    subgraph RestAssuredLayer["RestAssured DSL"]
        GVN[given\n.contentType\n.body\n.header]
        WHN[when\n.get / .post\n.put / .delete]
        THN[then\n.statusCode\n.body\n.time]
    end

    subgraph Assertions["Hamcrest Matchers"]
        M1[statusCode 200]
        M2[body notNullValue]
        M3[time lessThan 2000ms]
        M4[body hasKey]
    end

    GVN --> WHN --> THN
    THN --> Assertions
    THN --> EXT[.extract.response\nfor further assertions]
```

---

## CI/CD Integration

```mermaid
flowchart TB
    subgraph GitHub["GitHub Repository"]
        PR[Pull Request\nor Push to main]
    end

    subgraph GHA["GitHub Actions"]
        GHA_CHECKOUT[Checkout]
        GHA_JAVA[Setup Java 11]
        GHA_CACHE[Cache Maven deps]
        GHA_TEST[mvn clean test]
        GHA_REPORT[Upload artifacts]
    end

    subgraph Jenkins["Jenkins Server"]
        J_CHECKOUT[Checkout SCM]
        J_BUILD[Maven Build]
        J_TEST[Run Test Suite]
        J_PUBLISH[Publish HTML Reports]
        J_NOTIFY[Notify: Slack + Email]
    end

    PR --> GHA_CHECKOUT --> GHA_JAVA --> GHA_CACHE --> GHA_TEST --> GHA_REPORT
    PR --> J_CHECKOUT --> J_BUILD --> J_TEST --> J_PUBLISH --> J_NOTIFY
```

---

## Design Decisions

### Why TestNG over JUnit?

- Native support for test grouping (`groups = {"ui", "smoke"}`)
- Built-in `@DataProvider` for parameterised tests
- Suite-level XML configuration for parallel execution
- `@Parameters` injection for browser/environment switching per suite

### Why WebDriverManager?

Eliminates the need to manually download and version-match `chromedriver` / `geckodriver` binaries. The library resolves the correct binary for the host OS and Chrome/Firefox version automatically, making the framework zero-config for new team members.

### Why ThreadLocal for WebDriver?

TestNG can run test methods in parallel across threads. A plain `static WebDriver` field would be shared and cause race conditions. `ThreadLocal<WebDriver>` gives each thread its own isolated browser instance with no synchronisation overhead.

### Why separate API and UI layers?

- API tests run in milliseconds; UI tests take seconds. Separating them allows the API suite to run first as a fast sanity check.
- E2E tests deliberately cross both layers to validate the contract between UI actions and backend state — something neither pure API nor pure UI tests can do alone.

### Explicit Waits over Implicit Waits

`WebDriverWait` with `ExpectedConditions` is used for all synchronisation. Implicit waits are set conservatively (10s) as a fallback only. Mixing both can cause wait time to double; explicit waits are preferred because they target a specific condition rather than a blanket timeout.
