import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

group = "no.javatec.vicx"
version = "0.0.1-SNAPSHOT"

repositories { mavenCentral() }

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.springframework.boot) apply false
    alias(libs.plugins.spring.dependency.management) apply false
    alias(libs.plugins.kotlin.plugin.spring) apply false
}

val javaVersion = 21
val kotlinProjects = setOf("auth-server", "backend-ktor", "backend-spring-boot")
val springBootProjects = setOf("auth-server", "backend-spring-boot", "spring-boot-client", "database")

subprojects {
    repositories { mavenCentral() }

    if (name in springBootProjects) {
        apply(plugin = "org.springframework.boot")
        apply(plugin = "io.spring.dependency-management")
    }

    if (name in kotlinProjects) {
        apply(plugin = "org.jetbrains.kotlin.jvm")
        configure<KotlinJvmProjectExtension> {
            jvmToolchain(javaVersion)
        }
    }

    if (name in springBootProjects && name in kotlinProjects) {
        apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    }
}
