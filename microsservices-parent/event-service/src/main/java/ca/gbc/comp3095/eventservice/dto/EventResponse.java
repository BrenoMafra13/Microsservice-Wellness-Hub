package ca.gbc.comp3095.eventservice.dto;

import java.time.LocalDate;

public record EventResponse(
        Long eventId,
        String title,
        String description,
        LocalDate date,
        String location,
        int capacity,
        int registeredStudents
) {}