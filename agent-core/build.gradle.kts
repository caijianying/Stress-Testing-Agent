plugins {
    kotlin("jvm") version "1.9.23"
}

apply(from = rootProject.file("config.gradle.kts"))
val versions = extra["versions"] as Map<*, *>


group = "com.xiaobaicai.agent"

repositories {
    mavenCentral()
}

dependencies {
    implementation("${versions["byte-buddy"]}")
    implementation("${versions["byte-buddy-agent"]}")
    implementation("${versions["guava"]}")
    implementation("${versions["hutool-all"]}")
    annotationProcessor("${versions["lombok"]}")
    compileOnly("${versions["lombok"]}")
    testAnnotationProcessor("${versions["lombok"]}")
    testCompileOnly("${versions["lombok"]}")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(11)
}