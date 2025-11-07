package ca.gbc.comp3095.eventservice.service;

import ca.gbc.comp3095.eventservice.dto.EventRequest;
import ca.gbc.comp3095.eventservice.dto.EventResponse;
import ca.gbc.comp3095.eventservice.model.Event;
import ca.gbc.comp3095.eventservice.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Event event = repository.findById(id).orElseThrow(() -> new RuntimeException("Event not found"));
        return mapToResponse(event);
    }

    @Override
    public EventResponse updateEvent(Long id, EventRequest request) {
        Event event = repository.findById(id).orElseThrow(() -> new RuntimeException("Event not found"));
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
        repository.deleteById(id);
    }

    @Override
    public List<EventResponse> getEventsByDate(LocalDate date) {
        return repository.findAll().stream()
                .filter(e -> e.getDate().isEqual(date))
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<EventResponse> getEventsByLocation(String location) {
        return repository.findAll().stream()
                .filter(e -> e.getLocation().equalsIgnoreCase(location))
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public EventResponse registerStudent(Long id) {
        Event event = repository.findById(id).orElseThrow(() -> new RuntimeException("Event not found"));
        if (event.getRegisteredStudents() < event.getCapacity()) {
            event.setRegisteredStudents(event.getRegisteredStudents() + 1);
            repository.save(event);
        }
        return mapToResponse(event);
    }

    @Override
    public EventResponse unregisterStudent(Long id) {
        Event event = repository.findById(id).orElseThrow(() -> new RuntimeException("Event not found"));
        if (event.getRegisteredStudents() > 0) {
            event.setRegisteredStudents(event.getRegisteredStudents() - 1);
            repository.save(event);
        }
        return mapToResponse(event);
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
