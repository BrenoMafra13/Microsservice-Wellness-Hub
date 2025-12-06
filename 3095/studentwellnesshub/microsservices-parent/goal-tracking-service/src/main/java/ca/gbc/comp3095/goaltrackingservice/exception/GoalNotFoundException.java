package ca.gbc.comp3095.goaltrackingservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class GoalNotFoundException extends RuntimeException {
    public GoalNotFoundException(String id) {
        super("Goal not found: " + id);
    }
}
