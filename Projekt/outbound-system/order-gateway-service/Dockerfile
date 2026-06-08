FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

COPY gradlew .
RUN chmod +x ./gradlew
COPY gradle/ gradle/

COPY build.gradle.kts settings.gradle.kts ./
COPY common/build.gradle.kts common/
COPY order-gateway-service/build.gradle.kts order-gateway-service/

RUN --mount=type=cache,target=/root/.gradle ./gradlew :order-gateway-service:dependencies --no-daemon

COPY common/src common/src
COPY order-gateway-service/src order-gateway-service/src

RUN --mount=type=cache,target=/root/.gradle ./gradlew :order-gateway-service:bootJar -x test --no-daemon


FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
EXPOSE 8081
COPY --from=builder /app/order-gateway-service/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-Dspring.output.ansi.enabled=ALWAYS", "-jar", "app.jar"]