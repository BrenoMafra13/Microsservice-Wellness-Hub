package ca.gbc.comp3095.eventservice.repository;

import ca.gbc.comp3095.eventservice.model.Event;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByDate(LocalDate date);
    List<Event> findByLocationIgnoreCase(String location);

    List<Event> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description);
}
