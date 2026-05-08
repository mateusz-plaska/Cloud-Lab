FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

COPY gradlew .
RUN chmod +x ./gradlew
COPY gradle/ gradle/

COPY build.gradle.kts settings.gradle.kts ./
COPY common/build.gradle.kts common/
COPY packing-service/build.gradle.kts packing-service/

RUN ./gradlew :packing-service:dependencies --no-daemon

COPY common/src common/src
COPY packing-service/src packing-service/src

RUN ./gradlew :packing-service:bootJar -x test --no-daemon


FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
EXPOSE 8082
COPY --from=builder /app/packing-service/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-Dspring.output.ansi.enabled=ALWAYS", "-jar", "app.jar"]