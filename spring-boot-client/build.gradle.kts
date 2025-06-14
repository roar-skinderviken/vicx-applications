plugins {
    java
    id("org.springframework.boot") version "3.5.0"
    id("io.spring.dependency-management") version "1.1.7"
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
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework:spring-webflux")
    implementation("org.webjars:webjars-locator-core")
    implementation("org.webjars:bootstrap:5.3.6")
    compileOnly("org.webjars:popper.js:2.11.7")
    compileOnly("org.webjars:jquery:3.7.1")
}

springBoot {
    mainClass = "sample.DemoClientApplication"
}

tasks.jar {
    enabled = false
}