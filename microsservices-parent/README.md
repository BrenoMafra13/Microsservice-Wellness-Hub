# Student Wellness Hub (Microservices)
Author: Breno Lopes Mafra, Computer Programmer and Analyst

Project Description  
Microservice backend for wellness resources, goal tracking, and campus events. API Gateway fronts three Spring Boot services, secured with Keycloak; Kafka links goal completions to event recommendations; Redis caches resource lookups; PostgreSQL and MongoDB store domain data.

Demo video  
- Walkthrough: https://youtu.be/hgXgkUZDjoY?si=nExK1KRWYAPgjmEl

Tech stack  
- Services/ports: api-gateway (9000), wellness-resource-service (8081), event-service (8085), goal-tracking-service (8086)  
- Auth: Keycloak 24 (realm export in `docker/keycloak/realm-export.json`; admin/password)  
- Messaging: Kafka 7.7 + Schema Registry (9092/29092, 18081); topic `goal-completed-events`  
- Data: PostgreSQL 16 (wellness DB 5436, event DB 5435, Keycloak DB 5434), MongoDB 6 (27017), Redis 7 (6379)  
- Tooling: Mongo Express (8087), Redis Insight (8001), pgAdmin (8888)  
- Frameworks: Java 17, Spring Boot 3.5 (Web, Actuator, Data JPA, Data Mongo, Data Redis, Security), Spring Cloud Gateway MVC, Spring Kafka, Resilience4j, Flyway, Lombok  
- Testing/build: JUnit 5, RestAssured, Testcontainers; Gradle 8; Docker/Docker Compose

Application flow  
Client → api-gateway (JWT via Keycloak) → routed to services:  
- Wellness resources: CRUD on Postgres (`/api/resources`), cached in Redis (cache name `resources`).  
- Goal tracking: Mongo-backed goals (`/api/goals`); completing a goal publishes `GoalCompletedEvent` to Kafka.  
- Events: Postgres-backed events (`/api/events`); consumes `goal-completed-events`, uses Resilience4j circuit breaker to call wellness-resource-service for category-based resource summaries, and recommends upcoming events.  
Gateway enforces roles: `student` for goal create/update/delete and event register/unregister; `staff` for event/resource management; health endpoints are open.

Quick start (Docker Compose - recommended)  
1) From repo root, ensure `.env` is present (already committed with defaults).  
2) Start everything:  
   ```bash
   docker-compose --env-file .env up -d --build
   ```  
3) Hit services through the gateway: http://localhost:9000  
   - Wellness resources: `/api/resources`  
   - Goals: `/api/goals`  
   - Events: `/api/events`  
4) Tooling: Keycloak http://localhost:18080 (admin/password), pgAdmin http://localhost:8888, Mongo Express http://localhost:8087, Redis Insight http://localhost:8001, Schema Registry http://localhost:18081.  
5) Stop and clean up:  
   ```bash
   docker-compose --env-file .env down
   ```

Quick start (local dev without containers)  
Prereqs: Java 17; Postgres DBs for wellness (5436) and events (5435); MongoDB 27017; Redis 6379; Kafka + Schema Registry (9092/18081); Keycloak 18080 with realm `wellness-hub` and client `api-gateway`.  
- Update each `application.properties` if your ports/hosts differ.  
- Run infra via Docker only (optional): `docker compose -f docker/integrated/docker-compose.yml up -d`  
- Start services in separate terminals:  
  ```bash
  ./gradlew :wellness-resource-service:bootRun
  ./gradlew :goal-tracking-service:bootRun
  ./gradlew :event-service:bootRun
  ./gradlew :api-gateway:bootRun
  ```  
- Call everything through http://localhost:9000.

Main endpoints  
- Wellness resources (`/api/resources`):  
  - `POST /api/resources` create  
  - `GET /api/resources` list (optional `category` query)  
  - `GET /api/resources/{id}` fetch one  
  - `PUT /api/resources/{id}` update  
  - `DELETE /api/resources/{id}` delete  
- Goals (`/api/goals`):  
  - `POST /api/goals` create  
  - `GET /api/goals` list; `GET /api/goals/{id}` fetch one  
  - `PUT /api/goals/{id}` update  
  - `PUT /api/goals/{id}/complete` mark completed (publishes Kafka event)  
  - `GET /api/goals/category/{category}`, `GET /api/goals/status/{status}` filters  
  - `DELETE /api/goals/{id}` delete  
- Events (`/api/events`):  
  - `POST /api/events` create  
  - `GET /api/events` list; `GET /api/events/{id}` fetch one  
  - `PUT /api/events/{id}` update; `DELETE /api/events/{id}` delete  
  - `GET /api/events/date/{yyyy-MM-dd}`, `GET /api/events/location/{location}` filters  
  - `PUT /api/events/{id}/register` or `/unregister` adjust registrations

Build and tests  
- Build all: `./gradlew clean build`  
- Service tests (Testcontainers): `./gradlew test`

Data and seeds  
- Flyway migrations: `wellness-resource-service/src/main/resources/db/migration`, `event-service/src/main/resources/db/migration`.  
- Keycloak realm import: `docker/keycloak/realm-export.json` (roles `student`, `staff`, client `api-gateway`).  
- Kafka topic auto-created (`goal-completed-events`) by broker config.  
- Docker volumes persist Postgres, Mongo, and Redis data under `docker/integrated`.
