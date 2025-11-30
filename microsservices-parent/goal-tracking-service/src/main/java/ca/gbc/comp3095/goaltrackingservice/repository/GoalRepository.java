package ca.gbc.comp3095.goaltrackingservice.repository;

import ca.gbc.comp3095.goaltrackingservice.model.Goal;
import ca.gbc.comp3095.goaltrackingservice.model.GoalStatus;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GoalRepository extends MongoRepository<Goal, String> {
    List<Goal> findByCategoryIgnoreCase(String category);
    List<Goal> findByStatus(GoalStatus status);
}
