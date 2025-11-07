package ca.gbc.comp3095.goaltrackingservice;

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
class GoalTrackingIntegrationTests {

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

    @Test @Order(1)
    void createGoal() {
        String json = """
          { "title":"Run 5K", "category":"fitness", "target":"5000", "unit":"meters" }
        """;

        String response = RestAssured.given()
                .contentType("application/json")
                .body(json)
                .when().post("/api/goals")
                .then().statusCode(201)
                .extract().asString();

        JsonPath jp = new JsonPath(response);
        createdId = jp.getLong("id");
        assertThat(jp.getString("title"), Matchers.is("Run 5K"));
    }

    @Test @Order(2)
    void listGoals() {
        RestAssured.when().get("/api/goals")
                .then().statusCode(200)
                .body("size()", Matchers.greaterThanOrEqualTo(1));
    }

    @Test @Order(3)
    void updateGoal() {
        String upd = """
          { "title":"Run 10K", "category":"fitness", "target":"10000", "unit":"meters" }
        """;

        RestAssured.given().contentType("application/json").body(upd)
                .when().put("/api/goals/" + createdId)
                .then().statusCode(200)
                .body("title", Matchers.equalTo("Run 10K"));
    }

    @Test @Order(4)
    void deleteGoal() {
        RestAssured.when().delete("/api/goals/" + createdId)
                .then().statusCode(204);
    }
}
