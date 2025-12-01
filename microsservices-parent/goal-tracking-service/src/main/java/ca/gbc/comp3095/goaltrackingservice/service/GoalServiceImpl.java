package ca.gbc.comp3095.goaltrackingservice.service;

import ca.gbc.comp3095.goaltrackingservice.dto.GoalRequest;
import ca.gbc.comp3095.goaltrackingservice.dto.GoalResponse;
import ca.gbc.comp3095.goaltrackingservice.exception.GoalNotFoundException;
import ca.gbc.comp3095.goaltrackingservice.messaging.GoalEventPublisher;
import ca.gbc.comp3095.goaltrackingservice.model.Goal;
import ca.gbc.comp3095.goaltrackingservice.model.GoalStatus;
import ca.gbc.comp3095.goaltrackingservice.repository.GoalRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;
    private final GoalEventPublisher goalEventPublisher;

    @Override
    public GoalResponse createGoal(GoalRequest request) {
        Goal goal = Goal.builder()
                .title(request.title())
                .description(request.description())
                .targetDate(request.targetDate())
                .status(resolveStatus(request.status()))
                .category(request.category())
                .build();

        Goal saved = goalRepository.save(goal);
        log.info("Created goal {}", saved.getId());
        return mapToResponse(saved);
    }

    @Override
    public List<GoalResponse> getAllGoals() {
        return goalRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    @Override
    public GoalResponse getGoalById(String id) {
        return mapToResponse(findGoal(id));
    }

    @Override
    public GoalResponse updateGoal(String id, GoalRequest request) {
        Goal goal = findGoal(id);
        goal.setTitle(request.title());
        goal.setDescription(request.description());
        goal.setTargetDate(request.targetDate());
        goal.setCategory(request.category());
        goal.setStatus(resolveStatus(request.status()));

        Goal updated = goalRepository.save(goal);
        log.info("Updated goal {}", updated.getId());
        return mapToResponse(updated);
    }

    @Override
    public GoalResponse markGoalAsCompleted(String id) {
        Goal goal = findGoal(id);
        goal.setStatus(GoalStatus.COMPLETED);
        Goal updated = goalRepository.save(goal);
        goalEventPublisher.publishCompleted(updated);
        return mapToResponse(updated);
    }

    @Override
    public List<GoalResponse> getGoalsByCategory(String category) {
        return goalRepository.findByCategoryIgnoreCase(category).stream().map(this::mapToResponse).toList();
    }

    @Override
    public List<GoalResponse> getGoalsByStatus(String status) {
        GoalStatus goalStatus = resolveStatus(status);
        return goalRepository.findByStatus(goalStatus).stream().map(this::mapToResponse).toList();
    }

    @Override
    public void deleteGoal(String id) {
        Goal goal = findGoal(id);
        goalRepository.delete(goal);
        log.info("Deleted goal {}", id);
    }

    private Goal findGoal(String id) {
        return goalRepository.findById(id).orElseThrow(() -> new GoalNotFoundException(id));
    }

    private GoalStatus resolveStatus(String status) {
        try {
            return GoalStatus.fromValue(status);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }
    }

    private GoalResponse mapToResponse(Goal goal) {
        GoalStatus status = goal.getStatus() == null ? GoalStatus.IN_PROGRESS : goal.getStatus();
        return new GoalResponse(
                goal.getId(),
                goal.getTitle(),
                goal.getDescription(),
                goal.getTargetDate(),
                status.value(),
                goal.getCategory(),
                goal.getCreatedAt(),
                goal.getUpdatedAt()
        );
    }
}
