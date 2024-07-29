plugins {
    java
    id("org.springframework.boot") version "3.3.1"
    id("io.spring.dependency-management") version "1.1.6"
    id("com.google.cloud.tools.jib") version "3.4.3"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

springBoot {
    mainClass = "no.javatec.calc.CalculatorApplication"
}

tasks.test {
    useJUnitPlatform()
}

tasks.register<Copy>("processFrontendResources") {
    val backendTargetDir = project.layout.buildDirectory.dir("resources/main/static")
    val frontendBuildDir =
        project(":frontend").layout.projectDirectory.dir("dist")

    group = "Frontend"
    description = "Process frontend resources"
    dependsOn(":frontend:assembleFrontend")

    from(frontendBuildDir)
    into(backendTargetDir)
}

tasks.processResources {
    dependsOn("processFrontendResources")
}

jib {
    from.image = "bellsoft/liberica-openjdk-alpine:21"
    container { creationTime = "USE_CURRENT_TIMESTAMP" }
}