FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

COPY gradlew .
RUN chmod +x ./gradlew
COPY gradle/ gradle/

COPY build.gradle.kts settings.gradle.kts ./
COPY common/build.gradle.kts common/
COPY picking-service/build.gradle.kts picking-service/

RUN ./gradlew :picking-service:dependencies --no-daemon

COPY common/src common/src
COPY picking-service/src picking-service/src

RUN ./gradlew :picking-service:bootJar -x test --no-daemon


FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
EXPOSE 8083
COPY --from=builder /app/picking-service/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-Dspring.output.ansi.enabled=ALWAYS", "-jar", "app.jar"]