FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

COPY gradlew .
RUN chmod +x ./gradlew
COPY gradle/ gradle/

COPY build.gradle.kts settings.gradle.kts ./
COPY common/build.gradle.kts common/
COPY shipping-service/build.gradle.kts shipping-service/

RUN ./gradlew :shipping-service:dependencies --no-daemon

COPY common/src common/src
COPY shipping-service/src shipping-service/src

RUN ./gradlew :shipping-service:bootJar -x test --no-daemon


FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
EXPOSE 8085
COPY --from=builder /app/shipping-service/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-Dspring.output.ansi.enabled=ALWAYS", "-jar", "app.jar"]