package ca.gbc.comp3095.eventservice.listener;

import ca.gbc.comp3095.eventservice.event.GoalCompletedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GoalCompletedEventListener {

    @KafkaListener(topics = "goal-completed-events", groupId = "event-service-group")
    public void handleGoalCompletedEvent(GoalCompletedEvent event) {
        log.info("Received GoalCompletedEvent: goalId={}, category={}, userId={}", 
                event.getGoalId(), event.getCategory(), event.getUserId());
        
        // Business logic: Recommend relevant wellness events based on completed goal category
        log.info("Recommending wellness events for category: {}", event.getCategory());
        
        // In a real implementation, you would:
        // 1. Query events by category or related tags
        // 2. Send notifications to the user
        // 3. Update user preferences or recommendations
    }
}
