package ca.gbc.comp3095.goaltrackingservice.service;

import ca.gbc.comp3095.goaltrackingservice.dto.GoalTrackingRequest;
import ca.gbc.comp3095.goaltrackingservice.dto.GoalTrackingResponse;
import ca.gbc.comp3095.goaltrackingservice.model.GoalTracking;
import ca.gbc.comp3095.goaltrackingservice.repository.GoalTrackingRepository;
import ca.gbc.comp3095.goaltrackingservice.service.GoalTrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoalTrackingServiceImpl implements GoalTrackingService {

    private final GoalTrackingRepository goalRepository;

    @Override
    public GoalTrackingResponse createGoalTracking(GoalTrackingRequest goalRequest) {
        GoalTracking goal = GoalTracking.builder()
                .title(goalRequest.title())
                .description(goalRequest.description())
                .targetDate(goalRequest.targetDate())
                .status(goalRequest.status())
                .category(goalRequest.category())
                .build();

        goalRepository.save(goal);
        log.info("Created goal: {}", goal.getTitle());
        return mapToResponse(goal);
    }

    @Override
    public GoalTrackingResponse updateGoalTracking(String id, GoalTrackingRequest goalRequest) {
        GoalTracking goal = goalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found: " + id));

        goal.setTitle(goalRequest.title());
        goal.setDescription(goalRequest.description());
        goal.setTargetDate(goalRequest.targetDate());
        goal.setStatus(goalRequest.status());
        goal.setCategory(goalRequest.category());

        goalRepository.save(goal);
        log.info("Updated goal with id {}", id);
        return mapToResponse(goal);
    }

    @Override
    public void deleteGoalTracking(String id) {
        if (!goalRepository.existsById(id)) {
            throw new RuntimeException("Goal not found: " + id);
        }
        goalRepository.deleteById(id);
        log.info("Deleted goal with id {}", id);
    }

    @Override
    public List<GoalTrackingResponse> getGoalsByCategory(String category) {
        log.info("Fetching goals by category: {}", category);
        return goalRepository.findAll().stream()
                .filter(g -> g.getCategory().equalsIgnoreCase(category))
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<GoalTrackingResponse> getGoalsByStatus(String status) {
        log.info("Fetching goals by status: {}", status);
        return goalRepository.findAll().stream()
                .filter(g -> g.getStatus().equalsIgnoreCase(status))
                .map(this::mapToResponse)
                .toList();
    }


    @Override
    public GoalTrackingResponse markGoalAsCompleted(String id) {
        GoalTracking goal = goalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found: " + id));

        goal.setStatus("completed");
        goalRepository.save(goal);
        log.info("Marked goal {} as completed", id);
        return mapToResponse(goal);
    }

    @Override
    public List<GoalTrackingResponse> getAllGoals() {
        return goalRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    private GoalTrackingResponse mapToResponse(GoalTracking goal) {
        return new GoalTrackingResponse(
                goal.getId(),
                goal.getTitle(),
                goal.getDescription(),
                goal.getTargetDate(),
                goal.getStatus(),
                goal.getCategory()
        );
    }
}
