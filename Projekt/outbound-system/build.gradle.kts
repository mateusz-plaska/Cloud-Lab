import com.diffplug.gradle.spotless.SpotlessExtension

plugins {
    id("java")
    id("com.diffplug.spotless") version "8.2.1" apply false
    id("org.springframework.boot") version "4.0.5" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
    id("io.freefair.lombok") version "9.2.0" apply false
}

allprojects {
    group = "org.pwr.cloud.lab"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }

    apply(plugin = "com.diffplug.spotless")

    configure<SpotlessExtension> {
        java {
            targetExclude("**/build/**")
            removeUnusedImports()
            trimTrailingWhitespace()
            palantirJavaFormat()
            importOrder("", "java|javax", "\\#")
            formatAnnotations()
        }

        kotlinGradle {
            target("**/*.gradle.kts")
            targetExclude("**/build/**")
            ktlint()
        }
    }
}

subprojects {
    apply(plugin = "java")
    java.sourceCompatibility = JavaVersion.VERSION_21

    tasks.test {
        useJUnitPlatform()
    }
}

val springBootProjects = subprojects.filter { it.name !in listOf("qr-generator-lambda") }

configure(springBootProjects) {
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "io.freefair.lombok")

    dependencies {
        implementation("org.slf4j:slf4j-api")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
    }
}
