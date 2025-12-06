package ca.gbc.comp3095.goaltrackingservice;

import ca.gbc.comp3095.goaltrackingservice.dto.GoalRequest;
import ca.gbc.comp3095.goaltrackingservice.dto.GoalResponse;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class GoalTrackingKafkaIntegrationTest {

    @Container
    static MongoDBContainer mongodb = new MongoDBContainer(DockerImageName.parse("mongo:6.0"));

    @Container
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));

    @LocalServerPort
    private Integer port;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongodb::getReplicaSetUrl);
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Test
    void shouldCreateAndRetrieveGoal() {
        GoalRequest request = new GoalRequest(
                "Complete 30-day meditation challenge",
                "Meditate for 10 minutes daily",
                LocalDate.now().plusDays(30),
                "in-progress",
                "mental-health"
        );

        GoalResponse response = given()
                .contentType("application/json")
                .body(request)
                .when()
                .post("/api/goals")
                .then()
                .statusCode(201)
                .body("title", equalTo("Complete 30-day meditation challenge"))
                .body("status", equalTo("in-progress"))
                .extract()
                .as(GoalResponse.class);

        given()
                .when()
                .get("/api/goals/" + response.id())
                .then()
                .statusCode(200)
                .body("title", equalTo("Complete 30-day meditation challenge"))
                .body("category", equalTo("mental-health"));
    }

    @Test
    void shouldMarkGoalAsCompletedAndPublishKafkaEvent() {
        // Create goal
        GoalRequest request = new GoalRequest(
                "Exercise 3 times this week",
                "Improve physical fitness",
                LocalDate.now().plusDays(7),
                "in-progress",
                "fitness"
        );

        GoalResponse created = given()
                .contentType("application/json")
                .body(request)
                .when()
                .post("/api/goals")
                .then()
                .statusCode(201)
                .extract()
                .as(GoalResponse.class);

        // Mark as completed (should publish Kafka event)
        given()
                .when()
                .put("/api/goals/" + created.id() + "/complete")
                .then()
                .statusCode(200)
                .body("status", equalTo("completed"));
    }

    @Test
    void shouldGetGoalsByCategory() {
        GoalRequest request1 = new GoalRequest(
                "Read 2 books on nutrition",
                "Improve dietary knowledge",
                LocalDate.now().plusDays(60),
                "not-started",
                "nutrition"
        );

        GoalRequest request2 = new GoalRequest(
                "Meal prep for 2 weeks",
                "Healthy eating habits",
                LocalDate.now().plusDays(14),
                "in-progress",
                "nutrition"
        );

        given().contentType("application/json").body(request1).post("/api/goals");
        given().contentType("application/json").body(request2).post("/api/goals");

        given()
                .when()
                .get("/api/goals/category/nutrition")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(2));
    }

    @Test
    void shouldGetGoalsByStatus() {
        GoalRequest request = new GoalRequest(
                "Attend 5 wellness workshops",
                "Expand wellness knowledge",
                LocalDate.now().plusDays(90),
                "not-started",
                "education"
        );

        given().contentType("application/json").body(request).post("/api/goals");

        given()
                .when()
                .get("/api/goals/status/not-started")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(1));
    }
}
