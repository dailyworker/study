plugins {
    id("java")
    id("io.spring.dependency-management") version "1.1.2"
    id("org.springframework.boot") version "3.1.2"
}

group = "io.github.brewagebear"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("org.flywaydb:flyway-core:9.21.1")
    implementation("org.flywaydb:flyway-mysql:9.21.1")

    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    implementation("org.projectlombok:lombok:1.18.28")
    implementation("org.mapstruct:mapstruct-processor:1.5.5.Final")

    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
    annotationProcessor("org.projectlombok:lombok:1.18.28")

    implementation("com.google.guava:guava:32.1.1-jre")
    implementation("org.apache.commons:commons-lang3:3.13.0")

    runtimeOnly("com.h2database:h2:2.2.220")
    runtimeOnly("com.mysql:mysql-connector-j:8.1.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}
