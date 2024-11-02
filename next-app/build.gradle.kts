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

tasks.register<NpmTask>("runJest") {
    dependsOn("installDependencies")
    args.set(listOf("run", "test"))
}
