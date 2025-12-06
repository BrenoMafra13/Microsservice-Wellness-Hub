package ca.gbc.comp3095.eventservice;

import ca.gbc.comp3095.eventservice.dto.EventRequest;
import ca.gbc.comp3095.eventservice.dto.EventResponse;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class EventServiceKafkaIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Container
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));

    @LocalServerPort
    private Integer port;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Test
    void shouldCreateAndRetrieveEvent() {
        EventRequest request = new EventRequest(
                "Wellness Workshop",
                "Learn stress management techniques",
                LocalDate.now().plusDays(7),
                "Room 101",
                30,
                0
        );

        EventResponse response = given()
                .contentType("application/json")
                .body(request)
                .when()
                .post("/api/events")
                .then()
                .statusCode(201)
                .body("title", equalTo("Wellness Workshop"))
                .body("capacity", equalTo(30))
                .extract()
                .as(EventResponse.class);

        given()
                .when()
                .get("/api/events/" + response.id())
                .then()
                .statusCode(200)
                .body("title", equalTo("Wellness Workshop"))
                .body("location", equalTo("Room 101"));
    }

    @Test
    void shouldRegisterStudentForEvent() {
        // Create event
        EventRequest request = new EventRequest(
                "Yoga Session",
                "Beginner friendly yoga",
                LocalDate.now().plusDays(3),
                "Gym",
                20,
                0
        );

        EventResponse created = given()
                .contentType("application/json")
                .body(request)
                .when()
                .post("/api/events")
                .then()
                .statusCode(201)
                .extract()
                .as(EventResponse.class);

        // Register student
        given()
                .when()
                .put("/api/events/" + created.id() + "/register")
                .then()
                .statusCode(200)
                .body("registeredStudents", equalTo(1));
    }

    @Test
    void shouldGetEventsByLocation() {
        EventRequest request1 = new EventRequest(
                "Meditation Class",
                "Mindfulness meditation",
                LocalDate.now().plusDays(5),
                "Library",
                15,
                0
        );

        EventRequest request2 = new EventRequest(
                "Study Group",
                "Group study session",
                LocalDate.now().plusDays(6),
                "Library",
                25,
                0
        );

        given().contentType("application/json").body(request1).post("/api/events");
        given().contentType("application/json").body(request2).post("/api/events");

        given()
                .when()
                .get("/api/events/location/Library")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(2));
    }
}
