import com.github.gradle.node.npm.task.NpmTask

plugins {
    id("com.github.node-gradle.node") version "7.1.0"
}

node {
    version.set("20.18.0")
    npmVersion.set("10.9.0")
    download.set(true)
}

tasks.register<NpmTask>("installDependencies") {
    args.set(listOf("ci"))
}

tasks.register<NpmTask>("lint") {
    dependsOn("installDependencies")
    args.set(listOf("run", "lint"))
}

tasks.register<NpmTask>("check") {
    dependsOn("lint")
    args.set(listOf("run", "test"))
}
