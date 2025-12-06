package ca.gbc.comp3095.goaltrackingservice.service;

import ca.gbc.comp3095.goaltrackingservice.dto.GoalRequest;
import ca.gbc.comp3095.goaltrackingservice.dto.GoalResponse;
import java.util.List;

public interface GoalService {
    GoalResponse createGoal(GoalRequest request);
    List<GoalResponse> getAllGoals();
    GoalResponse getGoalById(String id);
    GoalResponse updateGoal(String id, GoalRequest request);
    GoalResponse markGoalAsCompleted(String id);
    List<GoalResponse> getGoalsByCategory(String category);
    List<GoalResponse> getGoalsByStatus(String status);
    void deleteGoal(String id);
}
