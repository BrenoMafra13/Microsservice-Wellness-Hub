package ca.gbc.comp3095.goaltrackingservice.dto;

import java.time.Instant;
import java.time.LocalDate;

public record GoalResponse(
        String id,
        String title,
        String description,
        LocalDate targetDate,
        String status,
        String category,
        Instant createdAt,
        Instant updatedAt
) {
}
