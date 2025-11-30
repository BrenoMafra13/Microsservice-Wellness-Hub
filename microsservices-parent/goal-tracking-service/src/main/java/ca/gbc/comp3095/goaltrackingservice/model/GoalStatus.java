package ca.gbc.comp3095.goaltrackingservice.model;

import java.util.Arrays;

public enum GoalStatus {
    NOT_STARTED("not-started"),
    IN_PROGRESS("in-progress"),
    COMPLETED("completed");

    private final String value;

    GoalStatus(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static GoalStatus fromValue(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return IN_PROGRESS;
        }

        String normalized = normalize(rawValue);
        return Arrays.stream(values())
                .filter(status -> status.value.equals(normalized))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported goal status: " + rawValue));
    }

    private static String normalize(String input) {
        return input.trim()
                .toLowerCase()
                .replace('_', '-')
                .replace(' ', '-');
    }
}
