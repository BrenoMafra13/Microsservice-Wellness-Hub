package ca.gbc.comp3095.wellnessresourceservice.dto;

import java.io.Serializable;

public record WellnessResourceRequest(
        Long resourceId,
        String title,
        String description,
        String category,
        String url
) implements Serializable {}