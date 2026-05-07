package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class ConfigPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By configNameField  = By.id("config-name");
    private final By configValueField = By.id("config-value");
    private final By saveButton       = By.id("save-config");
    private final By successToast     = By.cssSelector(".toast-success");

    public ConfigPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public ConfigPage enterConfigName(String name) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(configNameField))
            .sendKeys(name);
        return this;
    }

    public ConfigPage enterConfigValue(String value) {
        driver.findElement(configValueField).sendKeys(value);
        return this;
    }

    public String saveConfig() {
        driver.findElement(saveButton).click();
        return wait.until(ExpectedConditions.visibilityOfElementLocated(successToast))
                   .getText();
    }
}
