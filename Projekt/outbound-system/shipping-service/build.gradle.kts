dependencies {
    implementation(project(":common"))
    implementation("software.amazon.awssdk:dynamodb-enhanced:2.42.41")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql")
}
