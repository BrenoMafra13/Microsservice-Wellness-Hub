# microservices-parent / product-service (COMP3095 ICE1)

- **Build**: Gradle (Kotlin DSL), Java 21, Spring Boot 3.5.5
- **Module**: `product-service` (package `ca.gbc.productservice`)
- **MongoDB**: localhost:27017 (`product-service` DB)
- **Port**: 8084
- **Endpoints**:
  - `GET    /api/product` → 200 OK
  - `GET    /api/product/{id}` → 200/404
  - `POST   /api/product` → 201 Created
  - `PUT    /api/product/{id}` → 204 No Content (404 if not found)
  - `DELETE /api/product/{id}` → 204 No Content (404 if not found)

## Run locally (IntelliJ)
1. Start MongoDB (Docker):
   ```bash
   docker run -d --name comp3095-mongodb -p 27017:27017 mongo:7
   ```
2. Open project in IntelliJ → run `ProductServiceApplication`.
3. Import Postman collection: `postman/product-service.postman_collection.json`.
4. Test CRUD (port **8084**).

## Tests (Testcontainers)
```bash
./gradlew :product-service:test
```
(Spins up **mongo:7** automatically.)

## Docker Compose (full stack)
```bash
docker compose up --build -d
```
- API: http://localhost:8084
- Mongo Express: http://localhost:8081

## Git
```bash
git init
git add .
git commit -m "ICE1: product-service (Gradle)"
git branch -M main
git remote add origin <YOUR_PRIVATE_GITLAB_REPO_URL>
git push -u origin main
```

---
Generated 2025-10-03.
