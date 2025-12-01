package ca.gbc.comp3095.eventservice.service;

import ca.gbc.comp3095.eventservice.messaging.GoalCompletedEvent;
import ca.gbc.comp3095.eventservice.model.Event;
import ca.gbc.comp3095.eventservice.repository.EventRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventRecommendationService {

    private final EventRepository eventRepository;

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
    }
}
