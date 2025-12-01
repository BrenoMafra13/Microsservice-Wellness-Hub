package ca.gbc.comp3095.goaltrackingservice.messaging;

import ca.gbc.comp3095.goaltrackingservice.model.Goal;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoalEventPublisher {

    private final KafkaTemplate<String, GoalCompletedEvent> kafkaTemplate;

    @Value("${goal.events.topic}")
    private String goalCompletedTopic;

    public void publishCompleted(Goal goal) {
        GoalCompletedEvent event = new GoalCompletedEvent(
                goal.getId(),
                goal.getTitle(),
                goal.getCategory(),
                goal.getUpdatedAt() == null ? Instant.now() : goal.getUpdatedAt()
        );

        kafkaTemplate.send(goalCompletedTopic, goal.getId(), event);
        log.info("Published GoalCompletedEvent for goal {}", goal.getId());
    }
}
