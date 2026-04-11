plugins {
    id("java-library")
}

dependencies {
    api("org.springframework.boot:spring-boot-starter-amqp")
    api("com.fasterxml.jackson.core:jackson-databind")
    api("jakarta.validation:jakarta.validation-api:3.1.1")
}
