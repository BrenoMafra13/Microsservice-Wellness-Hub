## Student Wellness Hub

The `microsservices-parent` workspace now mirrors the ecommerce project’s structure: each Spring Boot microservice is isolated, Flyway manages relational schemas, MongoDB backs the goal-tracking service, and a Spring Cloud API Gateway fronts every endpoint.

### Services

- `event-service` – PostgreSQL-backed event catalog.
- `wellness-resource-service` – PostgreSQL + Redis cache for curated content.
- `goal-tracking-service` – MongoDB-backed personalized goals.
- `api-gateway` – central entry point that proxies `/api/events`, `/api/resources`, and `/api/goals`.
- Tooling containers: Redis Insight (`${REDIS_INSIGHT_PORT}`), Mongo Express (`${MONGO_EXPRESS_PORT}`), and pgAdmin (`8888`).

### Running everything

```bash
cd microsservices-parent
docker compose --env-file .env up -d --build
```

Default ports (override via `.env`):

| Component | Port |
| --- | --- |
| API Gateway | `${API_GATEWAY_PORT}` |
| Event Service | `${EVENT_PORT}` |
| Wellness Resource Service | `${WELLNESS_PORT}` |
| Goal Tracking Service | `${GOAL_PORT}` |
| Redis Insight | `${REDIS_INSIGHT_PORT}` |
| Mongo Express | `${MONGO_EXPRESS_PORT}` |
| pgAdmin | `8888` |

### Datastores

- `postgres-event` → `${EVENT_DB}` (host port `${POSTGRES_EVENT_PORT}`)
- `postgres-wellness` → `${WELLNESS_DB}` (host port `${POSTGRES_WELLNESS_PORT}`)
- `mongo` → `goal-tracking-service` database (host port `${MONGO_PORT}`)
- Redis → `${REDIS_PORT}`

Credentials for all backing services live in `.env`.
