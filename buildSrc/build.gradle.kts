plugins {
    `kotlin-dsl`
}

repositories {
    maven(url = "https://repo.spring.io/snapshot")
    maven(url = "https://repo.spring.io/milestone")
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    // plugins
    implementation(libs.kotlin.jvm)
    implementation(libs.springframework.boot)
    implementation(libs.spring.dependency.management)
    implementation(libs.kotlin.plugin.spring)
    implementation(libs.ktlint.gradle.plugin)
}
