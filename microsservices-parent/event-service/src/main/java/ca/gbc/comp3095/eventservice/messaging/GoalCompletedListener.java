package ca.gbc.comp3095.eventservice.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ca.gbc.comp3095.eventservice.service.EventRecommendationService;

@Component
@Slf4j
public class GoalCompletedListener {

    private final EventRecommendationService recommendationService;

    public GoalCompletedListener(EventRecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @KafkaListener(topics = "${goal.events.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void handle(GoalCompletedEvent event) {
        log.info("Received GoalCompletedEvent goalId={} title={} category={} completedAt={}",
                event.goalId(), event.title(), event.category(), event.completedAt());
        recommendationService.handleGoalCompleted(event);
    }
}
