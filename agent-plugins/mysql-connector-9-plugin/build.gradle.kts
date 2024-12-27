plugins {
    kotlin("jvm")
}

group = "com.xiaobaicai.agent.plugins"

apply(from = rootProject.file("config.gradle.kts"))
val versions = extra["versions"] as Map<*, *>

dependencies {
    implementation("com.mysql:mysql-connector-j:9.1.0")
}