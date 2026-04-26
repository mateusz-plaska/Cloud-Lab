dependencies {
    implementation(project(":common"))
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("software.amazon.awssdk:s3:2.42.40")
    runtimeOnly("org.postgresql:postgresql")
}
