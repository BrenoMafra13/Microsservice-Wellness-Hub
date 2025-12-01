package ca.gbc.comp3095.goaltrackingservice;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.testcontainers.containers.MongoDBContainer;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GoalTrackingServiceApplicationTests {

	@ServiceConnection
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");

	@MockBean
	private KafkaTemplate<String, ca.gbc.comp3095.goaltrackingservice.messaging.GoalCompletedEvent> kafkaTemplate;

	@LocalServerPort
	private Integer port;

	@BeforeEach
	void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
	}

	static {
		mongoDBContainer.start();
	}

	@Test
	void createGoalTest() {
		String requestBody = """
                {
                    "title": "Meditate Daily",
                    "description": "Practice meditation for 10 minutes every day",
                    "targetDate": "2025-12-01",
                    "status": "in-progress",
                    "category": "mindfulness"
                }
                """;

		RestAssured.given()
				.contentType(ContentType.JSON)
				.body(requestBody)
				.when()
				.post("/api/goals")
				.then()
				.log().all()
				.statusCode(HttpStatus.CREATED.value())
				.body("id", Matchers.notNullValue())
				.body("title", Matchers.equalTo("Meditate Daily"))
				.body("status", Matchers.equalTo("in-progress"))
				.body("category", Matchers.equalTo("mindfulness"));
	}

	@Test
	void getAllGoalsTest() {
		String requestBody = """
                {
                    "title": "Read a Book",
                    "description": "Finish reading one book per month",
                    "targetDate": "2025-12-31",
                    "status": "in-progress",
                    "category": "growth"
                }
                """;

		RestAssured.given()
				.contentType(ContentType.JSON)
				.body(requestBody)
				.when()
				.post("/api/goals")
				.then()
				.statusCode(HttpStatus.CREATED.value());

		RestAssured.given()
				.contentType(ContentType.JSON)
				.when()
				.get("/api/goals")
				.then()
				.log().all()
				.statusCode(HttpStatus.OK.value())
				.body("size()", Matchers.greaterThan(0))
				.body("[0].title", Matchers.notNullValue());
	}

	private String createGoalAndReturnId() {
		String requestBody = """
                {
                    "title": "Exercise Weekly",
                    "description": "Go to the gym 3 times per week",
                    "targetDate": "2025-11-30",
                    "status": "in-progress",
                    "category": "fitness"
                }
                """;

		return RestAssured.given()
				.contentType(ContentType.JSON)
				.body(requestBody)
				.when()
				.post("/api/goals")
				.then()
				.statusCode(HttpStatus.CREATED.value())
				.extract()
				.path("id");
	}

	@Test
	void updateGoalTest() {
		String id = createGoalAndReturnId();

		String updatedBody = """
                {
                    "title": "Exercise Weekly",
                    "description": "Go to the gym 5 times per week",
                    "targetDate": "2025-12-15",
                    "status": "in-progress",
                    "category": "fitness"
                }
                """;

		RestAssured.given()
				.contentType(ContentType.JSON)
				.body(updatedBody)
				.when()
				.put("/api/goals/{id}", id)
				.then()
				.log().all()
				.statusCode(HttpStatus.OK.value())
				.body("description", Matchers.equalTo("Go to the gym 5 times per week"));
	}

	@Test
	void getGoalsByCategoryTest() {
		String requestBody = """
                {
                    "title": "Yoga Practice",
                    "description": "Do yoga every morning",
                    "targetDate": "2025-12-10",
                    "status": "in-progress",
                    "category": "mindfulness"
                }
                """;

		RestAssured.given()
				.contentType(ContentType.JSON)
				.body(requestBody)
				.when()
				.post("/api/goals")
				.then()
				.statusCode(HttpStatus.CREATED.value());

		RestAssured.given()
				.contentType(ContentType.JSON)
				.when()
				.get("/api/goals/category/mindfulness")
				.then()
				.log().all()
				.statusCode(HttpStatus.OK.value())
				.body("[0].category", Matchers.equalTo("mindfulness"));
	}

	@Test
	void getGoalsByStatusTest() {
		String requestBody = """
                {
                    "title": "Read Daily",
                    "description": "Read at least 10 pages of a book every day",
                    "targetDate": "2025-12-20",
                    "status": "in-progress",
                    "category": "growth"
                }
                """;

		RestAssured.given()
				.contentType(ContentType.JSON)
				.body(requestBody)
				.when()
				.post("/api/goals")
				.then()
				.statusCode(HttpStatus.CREATED.value());

		RestAssured.given()
				.contentType(ContentType.JSON)
				.when()
				.get("/api/goals/status/in-progress")
				.then()
				.log().all()
				.statusCode(HttpStatus.OK.value())
				.body("[0].status", Matchers.equalTo("in-progress"));
	}

	@Test
	void markGoalAsCompletedTest() {
		String id = createGoalAndReturnId();

		RestAssured.given()
				.contentType(ContentType.JSON)
				.when()
				.put("/api/goals/{id}/complete", id)
				.then()
				.log().all()
				.statusCode(HttpStatus.OK.value())
				.body("status", Matchers.equalTo("completed"));
	}

	@Test
	void deleteGoalTest() {
		String id = createGoalAndReturnId();

		RestAssured.given()
				.contentType(ContentType.JSON)
				.when()
				.delete("/api/goals/{id}", id)
				.then()
				.log().all()
				.statusCode(HttpStatus.NO_CONTENT.value());
	}
}
