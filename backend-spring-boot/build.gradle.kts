plugins {
    java
    id("org.springframework.boot") version "3.3.5"
    id("io.spring.dependency-management") version "1.1.6"
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
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    //implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
    runtimeOnly("dev.akkinoc.spring.boot:logback-access-spring-boot-starter:4.3.2")

    implementation("org.apache.tika:tika-core:2.9.2")

    implementation(project(":database"))
    implementation("org.springframework.boot:spring-boot-starter-validation")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.flywaydb:flyway-core")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testRuntimeOnly("org.flywaydb:flyway-database-postgresql")
    testRuntimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

springBoot {
    mainClass = "no.vicx.backend.VicxBackendApplication"
}

tasks.test {
    useJUnitPlatform()
    systemProperty("spring.profiles.active", "test")
}

tasks.jar {
    enabled = false
}

tasks.register<Copy>("processFrontendResources") {
    val backendTargetDir = project.layout.buildDirectory.dir("resources/main/static")
    val frontendBuildDir =
        project(":frontend-vite").layout.projectDirectory.dir("dist")

    group = "Frontend"
    description = "Process frontend resources"
    dependsOn(":frontend-vite:assembleFrontend")

    from(frontendBuildDir)
    into(backendTargetDir)
}

tasks.processResources {
    dependsOn("processFrontendResources")
    //dependsOn(":next-app:runJest")
}