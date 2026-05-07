package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class DashboardPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By welcomeHeading  = By.cssSelector("h1.welcome");
    private final By configMenuLink  = By.id("nav-config");
    private final By userAvatar      = By.cssSelector(".user-avatar");

    public DashboardPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public boolean isLoaded() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(welcomeHeading))
                       .isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getWelcomeMessage() {
        return driver.findElement(welcomeHeading).getText();
    }

    public ConfigPage navigateToConfig() {
        driver.findElement(configMenuLink).click();
        return new ConfigPage(driver);
    }
}
