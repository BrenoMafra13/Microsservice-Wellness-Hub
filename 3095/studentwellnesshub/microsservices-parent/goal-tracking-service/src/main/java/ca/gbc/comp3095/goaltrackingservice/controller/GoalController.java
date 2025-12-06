package ca.gbc.comp3095.goaltrackingservice.controller;

import ca.gbc.comp3095.goaltrackingservice.dto.GoalRequest;
import ca.gbc.comp3095.goaltrackingservice.dto.GoalResponse;
import ca.gbc.comp3095.goaltrackingservice.service.GoalService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
@Validated
public class GoalController {

    private final GoalService goalService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GoalResponse createGoal(@Valid @RequestBody GoalRequest request) {
        return goalService.createGoal(request);
    }

    @GetMapping
    public List<GoalResponse> getAllGoals() {
        return goalService.getAllGoals();
    }

    @GetMapping("/{id}")
    public GoalResponse getGoal(@PathVariable String id) {
        return goalService.getGoalById(id);
    }

    @PutMapping("/{id}")
    public GoalResponse updateGoal(@PathVariable String id, @Valid @RequestBody GoalRequest request) {
        return goalService.updateGoal(id, request);
    }

    @PutMapping("/{id}/complete")
    public GoalResponse markGoalAsCompleted(@PathVariable String id) {
        return goalService.markGoalAsCompleted(id);
    }

    @GetMapping("/category/{category}")
    public List<GoalResponse> getByCategory(@PathVariable String category) {
        return goalService.getGoalsByCategory(category);
    }

    @GetMapping("/status/{status}")
    public List<GoalResponse> getByStatus(@PathVariable String status) {
        return goalService.getGoalsByStatus(status);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGoal(@PathVariable String id) {
        goalService.deleteGoal(id);
    }
}
