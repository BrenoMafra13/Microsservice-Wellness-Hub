# Build stage (optional if you build outside Docker)
# FROM gradle:8.8-jdk-21-alpine AS builder
# WORKDIR /workspace
# COPY . .
# RUN gradle -p product-service bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY build/libs/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java","-jar","/app/app.jar"]
