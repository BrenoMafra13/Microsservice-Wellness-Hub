package ca.gbc.comp3095.eventservice.service;

import ca.gbc.comp3095.eventservice.dto.EventRequest;
import ca.gbc.comp3095.eventservice.dto.EventResponse;
import ca.gbc.comp3095.eventservice.exception.EventNotFoundException;
import ca.gbc.comp3095.eventservice.model.Event;
import ca.gbc.comp3095.eventservice.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository repository;

    @Override
    public EventResponse createEvent(EventRequest request) {
        Event event = Event.builder()
                .title(request.title())
                .description(request.description())
                .date(request.date())
                .location(request.location())
                .capacity(request.capacity())
                .registeredStudents(request.registeredStudents())
                .build();
        repository.save(event);
        return mapToResponse(event);
    }

    @Override
    public List<EventResponse> getAllEvents() {
        return repository.findAll().stream().map(this::mapToResponse).toList();
    }

    @Override
    public EventResponse getEventById(Long id) {
        return mapToResponse(findEvent(id));
    }

    @Override
    public EventResponse updateEvent(Long id, EventRequest request) {
        Event event = findEvent(id);
        event.setTitle(request.title());
        event.setDescription(request.description());
        event.setDate(request.date());
        event.setLocation(request.location());
        event.setCapacity(request.capacity());
        event.setRegisteredStudents(request.registeredStudents());
        repository.save(event);
        return mapToResponse(event);
    }

    @Override
    public void deleteEvent(Long id) {
        repository.delete(findEvent(id));
    }

    @Override
    public List<EventResponse> getEventsByDate(LocalDate date) {
        return repository.findByDate(date).stream().map(this::mapToResponse).toList();
    }

    @Override
    public List<EventResponse> getEventsByLocation(String location) {
        return repository.findByLocationIgnoreCase(location).stream().map(this::mapToResponse).toList();
    }

    @Override
    public EventResponse registerStudent(Long id) {
        Event event = findEvent(id);
        if (event.getRegisteredStudents() >= event.getCapacity()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Event is at capacity");
        }
        event.setRegisteredStudents(event.getRegisteredStudents() + 1);
        repository.save(event);
        return mapToResponse(event);
    }

    @Override
    public EventResponse unregisterStudent(Long id) {
        Event event = findEvent(id);
        if (event.getRegisteredStudents() == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No registrations to remove");
        }
        event.setRegisteredStudents(event.getRegisteredStudents() - 1);
        repository.save(event);
        return mapToResponse(event);
    }

    private Event findEvent(Long id) {
        return repository.findById(id).orElseThrow(() -> new EventNotFoundException(id));
    }

    private EventResponse mapToResponse(Event event) {
        return new EventResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getDate(),
                event.getLocation(),
                event.getCapacity(),
                event.getRegisteredStudents()
        );
    }
}
