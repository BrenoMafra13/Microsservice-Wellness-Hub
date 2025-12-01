package ca.gbc.comp3095.eventservice;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EventServiceIntegrationTests {

    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");
    static { postgres.start(); }

    @MockBean
    private RestTemplate restTemplate;

    @LocalServerPort
    private Integer port;

    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Test
    void createAndListEvents() {
        String body = """
      { "title":"Yoga Workshop","description":"Relaxing session",
        "date":"2025-12-01","location":"Room A","capacity":50 }
      """;

        // create
        RestAssured.given().contentType(ContentType.JSON).body(body)
                .when().post("/api/events")
                .then().statusCode(201).body("id", Matchers.notNullValue());

        // list
        RestAssured.when().get("/api/events")
                .then().statusCode(200).body("size()", Matchers.greaterThan(0));
    }
}
