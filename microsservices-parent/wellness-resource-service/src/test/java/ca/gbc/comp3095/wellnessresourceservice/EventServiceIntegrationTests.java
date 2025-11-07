package ca.gbc.comp3095.eventservice;

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
class EventServiceIntegrationTests {

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
    void createEvent() {
        String json = """
          {
            "title": "Yoga @ GBC",
            "description": "Free class in the gym",
            "location": "Campus A",
            "startTime": "2030-01-01T10:00:00",
            "endTime": "2030-01-01T11:00:00"
          }
        """;

        String response = RestAssured.given()
                .contentType("application/json")
                .body(json)
                .when().post("/api/events")
                .then().statusCode(201)
                .extract().asString();

        JsonPath jp = new JsonPath(response);
        createdId = jp.getLong("id");
        assertThat(jp.getString("title"), Matchers.is("Yoga @ GBC"));
        assertThat(createdId, Matchers.notNullValue());
    }

    @Test @Order(2)
    void listEvents() {
        RestAssured.when().get("/api/events")
                .then().statusCode(200)
                .body("size()", Matchers.greaterThanOrEqualTo(1));
    }

    @Test @Order(3)
    void updateEvent() {
        String upd = """
          { "title": "Yoga (Updated)", "description": "Bring your mat",
            "location": "Campus A", "startTime":"2030-01-01T10:00:00",
            "endTime":"2030-01-01T11:00:00" }
        """;

        RestAssured.given().contentType("application/json").body(upd)
                .when().put("/api/events/" + createdId)
                .then().statusCode(200)
                .body("title", Matchers.equalTo("Yoga (Updated)"));
    }

    @Test @Order(4)
    void deleteEvent() {
        RestAssured.when().delete("/api/events/" + createdId)
                .then().statusCode(204);
    }
}
