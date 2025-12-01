package ca.gbc.comp3095.eventservice.messaging;

public record ResourceSummary(
        Long id,
        String title,
        String description,
        String category
) {
}
