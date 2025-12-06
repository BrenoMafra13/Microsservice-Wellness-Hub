package ca.gbc.comp3095.goaltrackingservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record GoalRequest(
        @NotBlank(message = "Title is required") String title,
        @NotBlank(message = "Description is required") String description,
        @NotNull(message = "Target date is required") LocalDate targetDate,
        String status,
        @NotBlank(message = "Category is required") String category
) {
}
