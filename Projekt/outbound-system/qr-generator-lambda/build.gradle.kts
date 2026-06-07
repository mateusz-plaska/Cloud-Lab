plugins {
    id("com.gradleup.shadow") version "9.4.2"
}

dependencies {
    implementation("com.amazonaws:aws-lambda-java-core:1.4.0")
    implementation("com.amazonaws:aws-lambda-java-events:3.16.1")

    implementation("software.amazon.awssdk:s3:2.42.41")
    implementation("software.amazon.awssdk:dynamodb:2.42.41")

    implementation("com.google.zxing:core:3.5.4")
    implementation("com.google.zxing:javase:3.5.4")

    implementation("tools.jackson.core:jackson-databind:3.1.4")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}
