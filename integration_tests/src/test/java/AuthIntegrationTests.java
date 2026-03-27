import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import org.hamcrest.Matchers;

public class AuthIntegrationTests {
    
    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "http://localhost:4004";
    }

    @Test
    public void shouldReturnOKWithValidToken() {
        
        // arrange
        String loginPayload =  """
            {
                "email": "testuser@test.com",
                "password": "password123"
            }

            """;


        // act
        Response response = RestAssured.given()
            .contentType("application/json")
            .body(loginPayload)
            .when()
            .post("/auth/login")
        // assert
            .then()
            .statusCode(200)
            .body("token", Matchers.notNullValue())
            .extract().response();

    System.out.println("Generated Token :" +response.jsonPath().getString("token"));
    }

    @Test
    public void shouldReturnUnauthorizedInvalidLogin() {
        String loginPayload =  """
            {
                "email": "invalid@test.com",
                "password": "invalidpassword"
            }

            """;
        RestAssured.given()
            .contentType("application/json")
            .body(loginPayload)
            .when()
            .post("/auth/login")
            .then()
            .statusCode(401);
    }
    
}
