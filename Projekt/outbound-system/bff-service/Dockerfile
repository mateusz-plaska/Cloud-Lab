FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

COPY gradlew .
RUN chmod +x ./gradlew
COPY gradle/ gradle/

COPY build.gradle.kts settings.gradle.kts ./
COPY common/build.gradle.kts common/
COPY bff-service/build.gradle.kts bff-service/

RUN ./gradlew :bff-service:dependencies --no-daemon

COPY common/src common/src
COPY bff-service/src bff-service/src

RUN ./gradlew :bff-service:bootJar -x test --no-daemon


FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
EXPOSE 8080
COPY --from=builder /app/bff-service/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-Dspring.output.ansi.enabled=ALWAYS", "-jar", "app.jar"]