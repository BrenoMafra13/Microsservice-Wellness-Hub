package ca.gbc.comp3095.goaltrackingservice.service;

import ca.gbc.comp3095.goaltrackingservice.dto.GoalTrackingRequest;
import ca.gbc.comp3095.goaltrackingservice.dto.GoalTrackingResponse;

import java.util.List;

public interface GoalTrackingService {
    GoalTrackingResponse createGoalTracking(GoalTrackingRequest goalRequest);
    GoalTrackingResponse updateGoalTracking(String id, GoalTrackingRequest goalRequest);
    void deleteGoalTracking(String id);
    List<GoalTrackingResponse> getGoalsByCategory(String category);
    List<GoalTrackingResponse> getGoalsByStatus(String status);
    GoalTrackingResponse markGoalAsCompleted(String id);
    List<GoalTrackingResponse> getAllGoals();
}
