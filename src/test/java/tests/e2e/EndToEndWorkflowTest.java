package tests.e2e;

import base.BaseTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.ConfigPage;
import pages.DashboardPage;
import pages.LoginPage;
import utils.ConfigReader;

import static io.restassured.RestAssured.given;

/**
 * End-to-end tests validate complete business workflows across both UI and API.
 * These catch integration failures that unit tests and isolated UI/API tests miss.
 */
@Test(groups = "e2e")
public class EndToEndWorkflowTest extends BaseTest {

    @Test(description = "Create config via UI — confirm it exists via API")
    public void testCreateConfigE2E() {
        // Step 1: Login via UI
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboard = loginPage
            .enterUsername(ConfigReader.get("valid.username"))
            .enterPassword(ConfigReader.get("valid.password"))
            .clickLogin();

        Assert.assertTrue(dashboard.isLoaded(), "Dashboard failed to load");

        // Step 2: Navigate to config page and create a config
        ConfigPage configPage = dashboard.navigateToConfig();
        String configName  = "E2E_Config_" + System.currentTimeMillis();
        String configValue = "automated-test-value";

        String toast = configPage
            .enterConfigName(configName)
            .enterConfigValue(configValue)
            .saveConfig();

        Assert.assertTrue(toast.contains("saved"), "UI did not confirm config save");

        // Step 3: Confirm the config exists via API
        RestAssured.baseURI = ConfigReader.get("api.base.url");

        // Get auth token
        String token = given()
            .contentType(ContentType.JSON)
            .body("{ \"username\": \"" + ConfigReader.get("valid.username") + "\", "
                + "\"password\": \"" + ConfigReader.get("valid.password") + "\" }")
            .post("/api/auth/login")
            .jsonPath().getString("token");

        // Validate config via API
        Response apiResponse = given()
            .header("Authorization", "Bearer " + token)
            .queryParam("name", configName)
            .get("/api/config");

        Assert.assertEquals(apiResponse.statusCode(), 200,
            "API did not return 200 for the config created via UI");
        Assert.assertEquals(apiResponse.jsonPath().getString("value"), configValue,
            "API config value doesn't match what was entered in UI");
    }
}

// v2: API config validation step
