package ca.gbc.comp3095.goaltrackingservice.messaging;

import java.time.Instant;

public record GoalCompletedEvent(
        String goalId,
        String title,
        String category,
        Instant completedAt
) {
}
