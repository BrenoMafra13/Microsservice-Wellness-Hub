package ca.gbc.comp3095.goaltrackingservice.controller;

import ca.gbc.comp3095.goaltrackingservice.dto.GoalTrackingRequest;
import ca.gbc.comp3095.goaltrackingservice.dto.GoalTrackingResponse;
import ca.gbc.comp3095.goaltrackingservice.service.GoalTrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class GoalTrackingController {

    private final GoalTrackingService goalTrackingService;

    @PostMapping
    public ResponseEntity<GoalTrackingResponse> createGoalTracking(@RequestBody GoalTrackingRequest request) {
        GoalTrackingResponse response = goalTrackingService.createGoalTracking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GoalTrackingResponse> updateGoalTracking(
            @PathVariable String id,
            @RequestBody GoalTrackingRequest request
    ) {
        GoalTrackingResponse response = goalTrackingService.updateGoalTracking(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(@PathVariable String id) {
        goalTrackingService.deleteGoalTracking(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<GoalTrackingResponse>> getAllGoals() {
        return ResponseEntity.ok(goalTrackingService.getAllGoals());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<GoalTrackingResponse>> getGoalsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(goalTrackingService.getGoalsByCategory(category));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<GoalTrackingResponse>> getGoalsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(goalTrackingService.getGoalsByStatus(status));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<GoalTrackingResponse> markGoalAsCompleted(@PathVariable String id) {
        GoalTrackingResponse response = goalTrackingService.markGoalAsCompleted(id);
        return ResponseEntity.ok(response);
    }
}
