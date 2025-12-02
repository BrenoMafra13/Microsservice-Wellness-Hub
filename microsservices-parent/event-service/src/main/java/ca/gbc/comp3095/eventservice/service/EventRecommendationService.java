package ca.gbc.comp3095.eventservice.service;

import ca.gbc.comp3095.eventservice.messaging.GoalCompletedEvent;
import ca.gbc.comp3095.eventservice.model.Event;
import ca.gbc.comp3095.eventservice.repository.EventRepository;
import ca.gbc.comp3095.eventservice.messaging.ResourceSummary;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.Collections;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventRecommendationService {

    private final EventRepository eventRepository;
    private final RestTemplate restTemplate;
    // Cache last successful resource summaries per category; keeps service responsive during outages.
    private final Map<String, List<ResourceSummary>> resourceCache = new ConcurrentHashMap<>();

    @Value("${wellness.service.url}")
    private String wellnessServiceUrl;

    public void handleGoalCompleted(GoalCompletedEvent event) {
        String keyword = event.category() == null ? "" : event.category();
        LocalDate today = LocalDate.now();
        List<Event> matches = eventRepository
                .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword)
                .stream()
                .filter(e -> e.getDate() == null || !e.getDate().isBefore(today))
                .toList();

        log.info("Recommended {} events for goalId={} category={}", matches.size(), event.goalId(), keyword);
        matches.forEach(e -> log.info(" - {} on {} at {}", e.getTitle(), e.getDate(), e.getLocation()));

        List<ResourceSummary> resources = fetchResourcesByCategory(keyword);
        log.info("Fetched {} resources for category={}", resources.size(), keyword);
    }

    @CircuitBreaker(name = "wellnessResources", fallbackMethod = "fallbackResources")
    public List<ResourceSummary> fetchResourcesByCategory(String category) {
        String url = String.format("%s/api/resources?category=%s", wellnessServiceUrl, category);
        ResponseEntity<ResourceSummary[]> response = restTemplate.getForEntity(url, ResourceSummary[].class);
        ResourceSummary[] body = response.getBody();
        List<ResourceSummary> resources = body == null ? Collections.emptyList() : List.of(body);
        resourceCache.put(category, resources); // cache happy path
        return resources;
    }

    public List<ResourceSummary> fallbackResources(String category, Throwable throwable) {
        log.warn("Fallback resources for category {}: {}", category, throwable.getMessage());
        // Serve last known good data if available; otherwise empty list.
        return resourceCache.getOrDefault(category, Collections.emptyList());
    }
}
