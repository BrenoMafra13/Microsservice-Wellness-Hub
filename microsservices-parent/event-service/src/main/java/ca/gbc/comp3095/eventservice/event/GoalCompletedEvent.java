package ca.gbc.comp3095.eventservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalCompletedEvent {
    private String goalId;
    private String title;
    private String category;
    private Instant completedAt;
    private String userId;
}
