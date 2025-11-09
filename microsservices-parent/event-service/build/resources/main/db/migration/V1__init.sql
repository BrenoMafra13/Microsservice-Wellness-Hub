CREATE TABLE events (
                        event_id SERIAL PRIMARY KEY,
                        title VARCHAR(255) NOT NULL,
                        description TEXT,
                        date DATE NOT NULL,
                        location VARCHAR(255),
                        capacity INT NOT NULL,
                        registered_students INT DEFAULT 0
);