plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    // plugins
    implementation(libs.kotlin.jvm)
    implementation(libs.springframework.boot)
    implementation(libs.spring.dependency.management)
    implementation(libs.kotlin.plugin.spring)
}