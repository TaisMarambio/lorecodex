FROM eclipse-temurin:21-jdk AS build

WORKDIR /workspace

# Gradle wrapper + build scripts (mejor cache)
COPY gradlew build.gradle settings.gradle ./
COPY gradle gradle
RUN chmod +x ./gradlew

# Source
COPY src src

# Build (tests off by default in Docker build)
RUN ./gradlew bootJar -x test

FROM eclipse-temurin:21-jre

WORKDIR /app
COPY --from=build /workspace/build/libs/*.jar /app/app.jar

# App listens on APP_PORT (default 8081)
EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
