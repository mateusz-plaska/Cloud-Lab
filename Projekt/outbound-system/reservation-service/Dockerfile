FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

COPY gradlew .
RUN chmod +x ./gradlew
COPY gradle/ gradle/

COPY build.gradle.kts settings.gradle.kts ./
COPY common/build.gradle.kts common/
COPY reservation-service/build.gradle.kts reservation-service/

RUN --mount=type=cache,target=/root/.gradle ./gradlew :reservation-service:dependencies --no-daemon

COPY common/src common/src
COPY reservation-service/src reservation-service/src

RUN --mount=type=cache,target=/root/.gradle ./gradlew :reservation-service:bootJar -x test --no-daemon


FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
EXPOSE 8084
COPY --from=builder /app/reservation-service/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-Dspring.output.ansi.enabled=ALWAYS", "-jar", "app.jar"]