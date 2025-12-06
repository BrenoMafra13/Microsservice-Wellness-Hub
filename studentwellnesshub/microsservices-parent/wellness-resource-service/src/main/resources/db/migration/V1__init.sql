CREATE TABLE IF NOT EXISTS t_resources (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(100) NOT NULL,
    url VARCHAR(500) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_resources_category ON t_resources ((lower(category)));
CREATE INDEX IF NOT EXISTS idx_resources_title ON t_resources ((lower(title)));
