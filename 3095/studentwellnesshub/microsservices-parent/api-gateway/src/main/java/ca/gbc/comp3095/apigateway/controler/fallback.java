package ca.gbc.comp3095.apigateway.controler;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
class FallbackController {

    @GetMapping("/wellness")
    public Map<String, Object> wellnessFallback() {
        return Map.of(
                "error", "Wellness Resource Service is temporarily unavailable",
                "message", "Please try again later",
                "status", 503,
                "data", Collections.emptyList()
        );
    }

    @GetMapping("/goals")
    public Map<String, Object> goalsFallback() {
        return Map.of(
                "error", "Goal Tracking Service is temporarily unavailable",
                "message", "Please try again later",
                "status", 503,
                "data", Collections.emptyList()
        );
    }
}