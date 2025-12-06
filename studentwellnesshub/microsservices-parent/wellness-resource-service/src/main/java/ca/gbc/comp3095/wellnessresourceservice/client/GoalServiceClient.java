package ca.gbc.comp3095.wellnessresourceservice.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoalServiceClient {

    @Value("${goal.service.url:http://localhost:8086}")
    private String goalServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @CircuitBreaker(name = "goalService", fallbackMethod = "getGoalsByUserIdFallback")
    public List<String> getGoalsByUserId(String userId) {
        log.info("Calling Goal Service for user: {}", userId);
        String url = goalServiceUrl + "/api/goals?userId=" + userId;
        
        try {
            // In a real implementation, you would deserialize to proper DTOs
            return List.of("Goal 1", "Goal 2", "Goal 3");
        } catch (Exception e) {
            log.error("Error calling Goal Service", e);
            throw e;
        }
    }

    public List<String> getGoalsByUserIdFallback(String userId, Exception ex) {
        log.warn("Circuit breaker activated for Goal Service. Returning cached/default data. Error: {}", 
                ex.getMessage());
        return Collections.emptyList();
    }
}
