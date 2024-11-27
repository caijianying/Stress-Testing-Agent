plugins {
    kotlin("jvm")
}

apply(from = rootProject.file("config.gradle.kts"))
val versions = extra["versions"] as Map<*, *>

group = "com.xiaobaicai.agent.plugins"

dependencies {
    implementation("${versions["spring-web"]}")
}