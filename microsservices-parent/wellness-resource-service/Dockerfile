FROM gradle:8.7-jdk17 AS build
WORKDIR /home/gradle/app
COPY build.gradle.kts settings.gradle.kts ./
COPY src ./src
RUN --mount=type=cache,target=/home/gradle/.gradle,sharing=locked gradle bootJar --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /home/gradle/app/build/libs/*.jar app.jar
EXPOSE 8081
# Force IPv4 stack so the service listens on 0.0.0.0 inside the container
ENTRYPOINT ["java","-Djava.net.preferIPv4Stack=true","-jar","/app/app.jar"]
