package ca.gbc.comp3095.wellnessresourceservice.dto;

public record WellnessResourceResponse(
        Long id,
        String title,
        String description,
        String category,
        String url
) {}
