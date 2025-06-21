plugins {
    id("vicx-library")
    java
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework:spring-webflux")
    implementation("org.webjars:webjars-locator-core")
    implementation(libs.webjars.bootstrap)
    implementation(libs.webjars.bootstrap)
    compileOnly(libs.webjars.popper.js)
    compileOnly(libs.webjars.jquery)
}

springBoot {
    mainClass = "sample.DemoClientApplication"
}

tasks.jar {
    enabled = false
}