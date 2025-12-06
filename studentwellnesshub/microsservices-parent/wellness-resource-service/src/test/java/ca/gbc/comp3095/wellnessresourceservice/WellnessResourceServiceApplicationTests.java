package ca.gbc.comp3095.wellnessresourceservice;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers
class WellnessResourceServiceApplicationTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @LocalServerPort
    private Integer port;

    private static Long createdId;

    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Test
    @Order(1)
    void testCreateResource() {
        String resourceJson = """
            {
              "title": "Mindful Breathing",
              "description": "A guided breathing exercise for relaxation.",
              "category": "mindfulness",
              "url": "https://example.com/mindful-breathing"
            }
            """;

        String response = RestAssured
                .given()
                .contentType("application/json")
                .body(resourceJson)
                .when()
                .post("/api/resources")
                .then()
                .statusCode(201)
                .extract()
                .asString();

        JsonPath json = new JsonPath(response);
        createdId = json.getLong("id");
        assertThat(json.getString("title"), Matchers.is("Mindful Breathing"));
        assertThat(createdId, Matchers.notNullValue());
    }

    @Test
    @Order(2)
    void testGetAllResources() {
        RestAssured
                .when()
                .get("/api/resources")
                .then()
                .statusCode(200)
                .body("size()", Matchers.greaterThanOrEqualTo(1));
    }

    @Test
    @Order(3)
    void testGetResourcesByCategory() {
        RestAssured
                .given()
                .queryParam("category", "mindfulness")
                .when()
                .get("/api/resources")
                .then()
                .statusCode(200)
                .body("[0].category", Matchers.equalToIgnoringCase("mindfulness"));
    }

    @Test
    @Order(4)
    void testUpdateResource() {
        String updateJson = """
            {
              "title": "Updated Breathing Guide",
              "description": "An improved guided breathing exercise.",
              "category": "mindfulness",
              "url": "https://example.com/updated-breathing"
            }
            """;

        RestAssured
                .given()
                .contentType("application/json")
                .body(updateJson)
                .when()
                .put("/api/resources/" + createdId)
                .then()
                .statusCode(200)
                .body("title", Matchers.equalTo("Updated Breathing Guide"));
    }

    @Test
    @Order(6)
    void testDeleteResource() {
        RestAssured
                .when()
                .delete("/api/resources/" + createdId)
                .then()
                .statusCode(204);
    }
}
