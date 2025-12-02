package ca.gbc.comp3095.eventservice.controller;

import ca.gbc.comp3095.eventservice.dto.EventRequest;
import ca.gbc.comp3095.eventservice.dto.EventResponse;
import ca.gbc.comp3095.eventservice.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponse createEvent(@RequestBody EventRequest request) {
        return eventService.createEvent(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventResponse> getAllEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventResponse getEventById(@PathVariable Long id) {
        return eventService.getEventById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventResponse updateEvent(@PathVariable Long id, @RequestBody EventRequest request) {
        return eventService.updateEvent(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
    }

    @GetMapping("/date/{date}")
    @ResponseStatus(HttpStatus.OK)
    public List<EventResponse> getEventsByDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return eventService.getEventsByDate(date);
    }

    @GetMapping("/location/{location}")
    @ResponseStatus(HttpStatus.OK)
    public List<EventResponse> getEventsByLocation(@PathVariable String location) {
        return eventService.getEventsByLocation(location);
    }

    @PutMapping("/{id}/register")
    @ResponseStatus(HttpStatus.OK)
    public EventResponse registerStudent(@PathVariable Long id) {
        return eventService.registerStudent(id);
    }

    @PutMapping("/{id}/unregister")
    @ResponseStatus(HttpStatus.OK)
    public EventResponse unregisterStudent(@PathVariable Long id) {
        return eventService.unregisterStudent(id);
    }
}
