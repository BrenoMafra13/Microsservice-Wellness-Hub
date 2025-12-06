# Student Wellness Hub - Assignment 2

## Overview

This project implements all requirements for **Assignment 2** of COMP3095, extending the Student Wellness Hub platform with enterprise-grade microservice capabilities.

## Assignment 2 Features Implemented

### ✅ Part 1: Secure API Gateway and Authentication
- **Spring Cloud Gateway** configured as the central entry point
- **Keycloak** integration for authentication and role-based authorization
- JWT token validation through the Gateway
- Role-based endpoint restrictions:
  - `student` role: can create goals and register for events
  - `staff` role: can manage wellness resources
  - `admin` role: full access to all endpoints

### ✅ Part 2: Kafka and Schema Registry
- **Apache Kafka** configured for asynchronous event-driven communication
- **Schema Registry** for message schema management
- Event flow implemented:
  - `goal-tracking-service` publishes `GoalCompletedEvent` when a goal is marked as completed
  - `event-service` consumes the event to recommend relevant wellness events
- JSON serialization with schema compatibility

### ✅ Part 3: Circuit Breaker with Resilience4J
- **Resilience4J** Circuit Breaker pattern implemented
- Applied to inter-service REST calls (wellness-resource-service → goal-tracking-service)
- Fallback methods configured to serve cached/default responses
- Circuit Breaker metrics exposed via `/actuator/metrics` for Prometheus monitoring

### ✅ Part 4: Swagger/OpenAPI Documentation
- **Springdoc OpenAPI** integrated into all microservices
- Swagger UI available for each service:
  - Event Service: http://localhost:8085/swagger-ui.html
  - Wellness Resource Service: http://localhost:8081/swagger-ui.html
  - Goal Tracking Service: http://localhost:8086/swagger-ui.html
  - API Gateway: http://localhost:9000/swagger-ui.html (admin only)
- Detailed API documentation with request/response schemas and security requirements

### ✅ Part 5: Testing and Containerization
- All components fully containerized with Docker
- Single `docker-compose.yml` to launch the entire system
- Integration tests with TestContainers (see test directories in each service)

## Architecture

```
┌─────────────┐
│   Client    │
└──────┬──────┘
       │
       ▼
┌─────────────────────────────┐
│     API Gateway (9000)      │
│   + Keycloak Security       │
│   + Circuit Breaker         │
└──────┬──────────────────────┘
       │
       ├──────────────┬──────────────┬──────────────┐
       ▼              ▼              ▼              ▼
┌──────────┐   ┌──────────┐   ┌──────────┐   ┌──────────┐
│  Event   │   │ Wellness │   │   Goal   │   │ Keycloak │
│ Service  │   │ Resource │   │ Tracking │   │  (8080)  │
│  (8085)  │   │ Service  │   │ Service  │   └──────────┘
│          │   │  (8081)  │   │  (8086)  │
└────┬─────┘   └────┬─────┘   └────┬─────┘
     │              │              │
     │              │              │ Kafka Producer
     │              │              ▼
     │              │         ┌─────────┐
     │              │         │  Kafka  │
     │              │         │ (9092)  │
     │              │         └────┬────┘
     │              │              │
     │ Kafka Consumer              │
     │◄────────────────────────────┘
     │
     ▼              ▼              ▼
┌──────────┐   ┌──────────┐   ┌──────────┐
│PostgreSQL│   │PostgreSQL│   │ MongoDB  │
│  Event   │   │ Wellness │   │  Goals   │
│  (5435)  │   │  (5436)  │   │ (27017)  │
└──────────┘   └────┬─────┘   └──────────┘
                    │
                    ▼
               ┌──────────┐
               │  Redis   │
               │  Cache   │
               │  (6379)  │
               └──────────┘
```

## Services and Ports

| Service | Port | Description |
|---------|------|-------------|
| API Gateway | 9000 | Central entry point with security |
| Event Service | 8085 | Wellness events management |
| Wellness Resource Service | 8082 | Resources with Redis cache |
| Goal Tracking Service | 8086 | Personal goals with Kafka |
| Keycloak | 8080 | Authentication server |
| Kafka | 9092 | Message broker |
| Schema Registry | 8083 | Kafka schema management |
| PostgreSQL (Event) | 5435 | Event database |
| PostgreSQL (Wellness) | 5436 | Wellness database |
| MongoDB | 27017 | Goals database |
| Redis | 6379 | Cache |
| pgAdmin | 8888 | PostgreSQL admin UI |
| Mongo Express | 8087 | MongoDB admin UI |
| Redis Insight | 8001 | Redis admin UI |
| Kafka UI | 8090 | Kafka admin UI |

## Getting Started

### Prerequisites
- Docker Desktop installed and running
- At least 8GB RAM allocated to Docker
- Ports 8080-9000, 5435-5436, 6379, 27017, 2181 available

### Running the System

1. **Navigate to the project directory:**
   ```bash
   cd microsservices-parent
   ```

2. **Start all services:**
   ```bash
   docker compose --env-file .env up -d --build
   ```

3. **Wait for all services to be healthy** (approximately 2-3 minutes):
   ```bash
   docker compose ps
   ```

4. **Verify services are running:**
   ```bash
   # Check API Gateway
   curl http://localhost:9000/actuator/health
   
   # Check Keycloak
   curl http://localhost:8080/health/ready
   ```

### Stopping the System

```bash
docker compose down
```

To remove all data volumes:
```bash
docker compose down -v
```

## Authentication with Keycloak

### Pre-configured Users

| Username | Password | Role | Description |
|----------|----------|------|-------------|
| student1 | student123 | student | Can create goals and register for events |
| staff1 | staff123 | staff | Can manage wellness resources |
| admin1 | admin123 | admin | Full access to all endpoints |

### Getting an Access Token

Use Postman or curl to get a JWT token:

```bash
curl -X POST 'http://localhost:8080/realms/studentwellnesshub/protocol/openid-connect/token' \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'grant_type=password' \
  -d 'client_id=wellness-hub-client' \
  -d 'client_secret=wellness-hub-secret' \
  -d 'username=student1' \
  -d 'password=student123'
```

### Using the Token

Include the token in the Authorization header:

```bash
curl -X GET 'http://localhost:9000/api/goals' \
  -H 'Authorization: Bearer YOUR_ACCESS_TOKEN'
```

## Testing the System

### 1. Create a Goal (as student)

```bash
curl -X POST 'http://localhost:9000/api/goals' \
  -H 'Authorization: Bearer YOUR_STUDENT_TOKEN' \
  -H 'Content-Type: application/json' \
  -d '{
    "title": "Exercise 3 times a week",
    "description": "Improve physical health",
    "targetDate": "2025-12-31",
    "status": "in-progress",
    "category": "fitness"
  }'
```

### 2. Mark Goal as Completed (triggers Kafka event)

```bash
curl -X PUT 'http://localhost:9000/api/goals/{goalId}/complete' \
  -H 'Authorization: Bearer YOUR_STUDENT_TOKEN'
```

This will:
- Update the goal status to COMPLETED
- Publish a `GoalCompletedEvent` to Kafka
- Event Service will consume the event and log recommendations

### 3. Create a Wellness Resource (as staff)

```bash
curl -X POST 'http://localhost:9000/api/resources' \
  -H 'Authorization: Bearer YOUR_STAFF_TOKEN' \
  -H 'Content-Type: application/json' \
  -d '{
    "title": "Meditation Guide",
    "description": "10-minute daily meditation",
    "category": "mental-health",
    "url": "https://example.com/meditation"
  }'
```

### 4. Register for an Event (as student)

```bash
curl -X PUT 'http://localhost:9000/api/events/{eventId}/register' \
  -H 'Authorization: Bearer YOUR_STUDENT_TOKEN'
```

### 5. Test Circuit Breaker

Stop the goal-tracking-service:
```bash
docker stop goal-tracking-service
```

Try to access wellness resources (which calls goal service):
```bash
curl -X GET 'http://localhost:9000/api/resources' \
  -H 'Authorization: Bearer YOUR_TOKEN'
```

The circuit breaker will activate and return cached/fallback data.

Check circuit breaker status:
```bash
curl http://localhost:8082/actuator/circuitbreakers
```

## Monitoring and Observability

### Actuator Endpoints

All services expose actuator endpoints:
- Health: `/actuator/health`
- Metrics: `/actuator/metrics`
- Prometheus: `/actuator/prometheus`
- Circuit Breakers: `/actuator/circuitbreakers`

### Kafka Monitoring

Access Kafka UI at http://localhost:8090 to:
- View topics and messages
- Monitor consumer groups
- Check schema registry

### Database Management

- **pgAdmin**: http://localhost:8888 (user@domain.ca / password)
- **Mongo Express**: http://localhost:8087
- **Redis Insight**: http://localhost:8001

## Swagger/OpenAPI Documentation

Access Swagger UI for each service:

- **Event Service**: http://localhost:8085/swagger-ui.html
- **Wellness Resource Service**: http://localhost:8082/swagger-ui.html
- **Goal Tracking Service**: http://localhost:8086/swagger-ui.html
- **API Gateway**: http://localhost:9000/swagger-ui.html (requires admin role)

## Project Structure

```
microsservices-parent/
├── api-gateway/              # Spring Cloud Gateway with security
├── event-service/            # Event management with Kafka consumer
├── wellness-resource-service/# Resources with Redis cache and Circuit Breaker
├── goal-tracking-service/    # Goals with MongoDB and Kafka producer
├── docker/
│   └── keycloak/
│       └── realm-export.json # Keycloak realm configuration
├── docker-compose.yml        # Complete system orchestration
├── .env                      # Environment variables
└── README_ASSIGNMENT2.md     # This file
```

## Technologies Used

- **Spring Boot 3.5.7** - Microservices framework
- **Spring Cloud Gateway** - API Gateway
- **Keycloak 23.0** - Authentication and authorization
- **Apache Kafka 7.5.0** - Event streaming
- **Resilience4J 2.1.0** - Circuit Breaker
- **Springdoc OpenAPI 2.3.0** - API documentation
- **PostgreSQL 16** - Relational database
- **MongoDB 6.0** - Document database
- **Redis 7** - Caching
- **Docker & Docker Compose** - Containerization

## Troubleshooting

### Services not starting

1. Check Docker resources (at least 8GB RAM)
2. Ensure all ports are available
3. Check logs: `docker compose logs [service-name]`

### Keycloak not accessible

Wait 30-60 seconds after startup for Keycloak to fully initialize.

### Kafka connection issues

Ensure Zookeeper and Kafka are healthy:
```bash
docker compose ps zookeeper kafka
```

### Circuit Breaker not working

Check Resilience4J configuration in application.properties and verify actuator endpoints are exposed.

## Assignment Deliverables Checklist

- ✅ Private GitLab repository with all code
- ✅ Docker Compose file to launch entire system
- ✅ All components containerized
- ✅ Keycloak authentication with role-based access
- ✅ Kafka event-driven communication
- ✅ Resilience4J Circuit Breaker with fallbacks
- ✅ Swagger/OpenAPI documentation for all services
- ✅ Integration tests with TestContainers
- ✅ README with complete instructions

## Team Information

**Course**: COMP3095 - Enterprise Application Development  
**Assignment**: Assignment 2 - Enterprise Microservices  
**Institution**: George Brown College

## License

This project is for educational purposes as part of COMP3095 coursework.
