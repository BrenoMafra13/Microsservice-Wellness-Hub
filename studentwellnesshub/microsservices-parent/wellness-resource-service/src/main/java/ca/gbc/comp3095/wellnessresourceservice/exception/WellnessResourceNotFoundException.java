package ca.gbc.comp3095.wellnessresourceservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class WellnessResourceNotFoundException extends RuntimeException {
    public WellnessResourceNotFoundException(Long id) {
        super("Resource not found: " + id);
    }
}
