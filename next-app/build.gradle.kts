import com.github.gradle.node.npm.task.NpmTask

plugins {
    id("com.github.node-gradle.node") version "7.1.0"
}

node {
    version.set("24.11.0")
    npmVersion.set("11.6.1")
    download.set(true)
}

tasks.register<NpmTask>("installDependencies") {
    workingDir.set(file("."))
    args.set(listOf("ci"))
}

tasks.register<NpmTask>("eslint") {
    dependsOn("installDependencies")
    workingDir.set(file("."))
    args.set(listOf("run", "eslint"))
}

tasks.register<NpmTask>("check") {
    dependsOn("eslint")
    workingDir.set(file("."))
    args.set(listOf("run", "test"))
}
