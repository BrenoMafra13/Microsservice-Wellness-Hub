FROM gradle:8.7-jdk21-alpine AS build
WORKDIR /home/gradle/app
COPY build.gradle.kts ./
COPY src ./src
RUN --mount=type=cache,target=/home/gradle/.gradle gradle bootJar --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /home/gradle/app/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/app.jar"]
