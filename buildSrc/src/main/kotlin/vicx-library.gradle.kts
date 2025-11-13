import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import kotlin.jvm.optionals.getOrNull

repositories {
    mavenCentral()
    maven("https://repo.spring.io/snapshot")
}

plugins {
    kotlin("jvm") apply false
}

// Shared configuration
val javaVersion = 21
val kotlinProjects = setOf("auth-server", "backend-ktor", "backend-spring-boot")
val springBootProjects = setOf("auth-server", "backend-spring-boot", "spring-boot-client", "database")
val kotlinProjectTestDependencies = setOf("kotest.runner.junit5", "kotest.assertions.core", "kotest.assertions.table", "mockk")
val springBootTestDependencies = setOf("kotest.extensions.spring", "springmockk")

// Retrieve the version catalog
val libs =
    project.extensions
        .findByType<VersionCatalogsExtension>()
        ?.named("libs")
        ?: error("Version catalog 'libs' not found")

fun String.getLibraryValue(): MinimalExternalModuleDependency =
    libs
        .findLibrary(this)
        .getOrNull()
        ?.get()
        ?: error("Library '$this' not found in version catalog")

fun String.getLibraryVersion(): String =
    libs
        .findVersion(this)
        .getOrNull()
        ?.toString()
        ?: error("Version '$this' not found in version catalog")

val ktlintVersion = "ktlint-version".getLibraryVersion()

// Spring Boot + Kotlin Projects
if (name in springBootProjects && name in kotlinProjects) {
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")

    dependencies {
        implementation("jackson.module.kotlin".getLibraryValue())
        springBootTestDependencies.forEach {
            testImplementation(it.getLibraryValue())
        }
    }
}

// Spring Boot Projects
if (name in springBootProjects) {
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    dependencies {
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }
}

// Kotlin Projects
if (name in kotlinProjects) {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    configure<KotlinJvmProjectExtension> {
        jvmToolchain(javaVersion)
        compilerOptions {
            // handling Java nullable annotations
            freeCompilerArgs.add("-Xjsr305=strict")
        }
    }

    configure<KtlintExtension> {
        version = ktlintVersion
    }

    dependencies {
        kotlinProjectTestDependencies.forEach {
            testImplementation(it.getLibraryValue())
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    jvmArgs(
        "-Xshare:off",
        "-XX:+EnableDynamicAgentLoading",
        "-Dkotest.framework.classpath.scanning.autoscan.disable=true",
    )
}
