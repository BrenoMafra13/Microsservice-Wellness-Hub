package ca.gbc.productservice;

import ca.gbc.productservice.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class ProductServiceApplicationTests {

    @Container
    static MongoDBContainer mongo = new MongoDBContainer("mongo:7");

    @DynamicPropertySource
    static void mongoProps(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> mongo.getConnectionString() + "/test_db");
    }

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate rest;

    private String baseUrl() {
        return "http://localhost:" + port + "/api/product";
    }

    @Test
    void crud_flow() {
        ResponseEntity<Product[]> empty = rest.getForEntity(baseUrl(), Product[].class);
        assertThat(empty.getStatusCode()).isEqualTo(HttpStatus.OK);

        Product createBody = new Product("MacBook Pro", "Apple MacBook Pro 16-inch", new BigDecimal("2499.99"));
        ResponseEntity<Product> createdResp = rest.postForEntity(baseUrl(), createBody, Product.class);
        assertThat(createdResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Product created = createdResp.getBody();
        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotBlank();

        ResponseEntity<Product[]> afterCreate = rest.getForEntity(baseUrl(), Product[].class);
        assertThat(afterCreate.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(afterCreate.getBody()).isNotNull();
        assertThat(afterCreate.getBody().length).isGreaterThanOrEqualTo(1);

        Product update = new Product("MacBook Pro M4 Max", "Updated", new BigDecimal("7849.00"));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Product> putReq = new HttpEntity<>(update, headers);
        ResponseEntity<Void> putResp = rest.exchange(baseUrl() + "/" + created.getId(), HttpMethod.PUT, putReq, Void.class);
        assertThat(putResp.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<Void> delResp = rest.exchange(baseUrl() + "/" + created.getId(), HttpMethod.DELETE, new HttpEntity<>(headers), Void.class);
        assertThat(delResp.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
