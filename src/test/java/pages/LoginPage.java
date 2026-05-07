package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class LoginPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Locators — update here if the UI changes, nowhere else
    private final By usernameField = By.id("username");
    private final By passwordField = By.id("password");
    private final By loginButton   = By.id("login-btn");
    private final By errorMessage  = By.cssSelector(".error-message");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public LoginPage enterUsername(String username) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(usernameField))
            .clear();
        driver.findElement(usernameField).sendKeys(username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        driver.findElement(passwordField).sendKeys(password);
        return this;
    }

    public DashboardPage clickLogin() {
        driver.findElement(loginButton).click();
        return new DashboardPage(driver);
    }

    public String getErrorMessage() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage))
                   .getText();
    }

    public boolean isErrorDisplayed() {
        return driver.findElements(errorMessage).size() > 0;
    }
}
