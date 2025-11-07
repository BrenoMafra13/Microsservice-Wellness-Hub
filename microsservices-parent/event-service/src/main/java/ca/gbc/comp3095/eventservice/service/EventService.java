package ca.gbc.comp3095.eventservice.service;

import ca.gbc.comp3095.eventservice.dto.EventRequest;
import ca.gbc.comp3095.eventservice.dto.EventResponse;

import java.time.LocalDate;
import java.util.List;

public interface EventService {
    EventResponse createEvent(EventRequest request);
    List<EventResponse> getAllEvents();
    EventResponse getEventById(Long id);
    EventResponse updateEvent(Long id, EventRequest request);
    void deleteEvent(Long id);
    List<EventResponse> getEventsByDate(LocalDate date);
    List<EventResponse> getEventsByLocation(String location);
    EventResponse registerStudent(Long id);
    EventResponse unregisterStudent(Long id);
}
