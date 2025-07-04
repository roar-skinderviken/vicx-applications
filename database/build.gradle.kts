plugins {
    id("vicx-library")
    java
    alias(libs.plugins.freefair.lombok)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencyManagement {
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
    }
}

// https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#0.3
val mockitoAgent = configurations.create("mockitoAgent")

dependencies {
    api("org.springframework.boot:spring-boot-starter-data-jpa")

    testCompileOnly("org.springframework.boot:spring-boot-starter-validation")
    testRuntimeOnly("org.flywaydb:flyway-database-postgresql")
    testRuntimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:postgresql")

    testImplementation(libs.mockito)
    mockitoAgent(libs.mockito) { isTransitive = false }
}

tasks.compileJava {
    options.compilerArgs.add("-parameters")
}

// intentionally overwriting jvmArgs from vicx-library plugin here.
tasks.test {
    jvmArgs("-javaagent:${mockitoAgent.asPath}")
    systemProperty("spring.profiles.active", "test")
}

tasks.bootJar {
    enabled = false
}
