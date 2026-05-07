package tests.ui;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.DashboardPage;
import pages.LoginPage;
import utils.ConfigReader;

@Test(groups = "ui")
public class LoginTest extends BaseTest {

    @Test(description = "Valid credentials should land on dashboard")
    public void testValidLogin() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboard = loginPage
            .enterUsername(ConfigReader.get("valid.username"))
            .enterPassword(ConfigReader.get("valid.password"))
            .clickLogin();

        Assert.assertTrue(dashboard.isLoaded(), "Dashboard did not load after valid login");
        Assert.assertTrue(dashboard.getWelcomeMessage().contains("Welcome"));
    }

    @Test(description = "Wrong password should show error")
    public void testInvalidLogin() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage
            .enterUsername(ConfigReader.get("valid.username"))
            .enterPassword("wrongpassword")
            .clickLogin();

        Assert.assertTrue(loginPage.isErrorDisplayed(), "Error message was not shown");
        Assert.assertEquals(loginPage.getErrorMessage(), "Invalid username or password.");
    }

    @Test(description = "Empty form should not submit")
    public void testEmptyCredentials() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.clickLogin();

        Assert.assertTrue(loginPage.isErrorDisplayed(), "Validation error not shown for empty form");
    }
}
