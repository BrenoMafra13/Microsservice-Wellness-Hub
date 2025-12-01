package ca.gbc.comp3095.eventservice.messaging;

import java.time.Instant;

public record GoalCompletedEvent(
        String goalId,
        String title,
        String category,
        Instant completedAt
) {
}
