plugins {
    kotlin("jvm")
}

apply(from = rootProject.file("config.gradle.kts"))
val versions = extra["versions"] as Map<*, *>

group = "com.xiaobaicai.agent.plugins"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(project(":agent-core"))
    implementation("${versions["byte-buddy"]}")
    implementation("${versions["byte-buddy-agent"]}")
    implementation("${versions["spring-web"]}")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}