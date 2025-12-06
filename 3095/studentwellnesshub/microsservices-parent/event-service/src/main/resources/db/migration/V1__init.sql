CREATE TABLE IF NOT EXISTS events (
    event_id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    date DATE NOT NULL,
    location VARCHAR(255),
    capacity INT NOT NULL,
    registered_students INT DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_events_date ON events(date);
CREATE INDEX IF NOT EXISTS idx_events_location ON events ((lower(location)));
