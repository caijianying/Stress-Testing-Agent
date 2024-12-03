plugins {
    kotlin("jvm")
}

group = "com.xiaobaicai.agent.plugins"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}

subprojects{
    apply(plugin = "java")
    apply(from = rootProject.file("config.gradle.kts"))
    val versions = extra["versions"] as Map<*, *>

    repositories {
        mavenCentral()
    }

    dependencies {
        testImplementation(kotlin("test"))
        implementation(project(":agent-core"))
        implementation("${versions["byte-buddy"]}")
        implementation("${versions["byte-buddy-agent"]}")
    }

    tasks.test {
        useJUnitPlatform()
    }
}