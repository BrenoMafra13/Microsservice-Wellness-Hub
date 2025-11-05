CREATE TABLE t_resources (
     id SERIAL PRIMARY KEY,
     title VARCHAR(255) NOT NULL,
     description TEXT,
     category VARCHAR(100) NOT NULL,
     url VARCHAR(500) NOT NULL
);
