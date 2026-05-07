package tests.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.ConfigReader;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Test(groups = "api")
public class AuthApiTest {

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = ConfigReader.get("api.base.url");
    }

    @Test(description = "Valid login returns 200 with JWT token")
    public void testLoginSuccess() {
        given()
            .contentType(ContentType.JSON)
            .body("{ \"username\": \"testuser@example.com\", \"password\": \"Test@1234\" }")
        .when()
            .post("/api/auth/login")
        .then()
            .statusCode(200)
            .body("token", notNullValue())
            .body("user.email", equalTo("testuser@example.com"));
    }

    @Test(description = "Wrong password returns 401")
    public void testLoginInvalidCredentials() {
        given()
            .contentType(ContentType.JSON)
            .body("{ \"username\": \"testuser@example.com\", \"password\": \"wrongpass\" }")
        .when()
            .post("/api/auth/login")
        .then()
            .statusCode(401)
            .body("error", equalTo("Invalid credentials"));
    }

    @Test(description = "Missing password field returns 400")
    public void testLoginMissingFields() {
        given()
            .contentType(ContentType.JSON)
            .body("{ \"username\": \"testuser@example.com\" }")
        .when()
            .post("/api/auth/login")
        .then()
            .statusCode(400)
            .body("error", containsString("password"));
    }

    @Test(description = "Request without token returns 403")
    public void testUnauthorizedAccess() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/api/config")
        .then()
            .statusCode(403);
    }
}
