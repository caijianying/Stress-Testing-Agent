import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.9.23"
    id("java")
    id("distribution")
    id("com.github.johnrengelman.shadow") version ("8.1.1")
}

apply(from = rootProject.file("config.gradle.kts"))
val versions = extra["versions"] as Map<*, *>

group = "com.xiaobaicai.agent"
version = "${extra["projectVersion"]}"

repositories {
    mavenCentral()
}

dependencies {
    implementation("${versions["javax-servlet"]}")
    implementation(project(":agent-core"))
    implementation("${versions["byte-buddy"]}")
    implementation("${versions["byte-buddy-agent"]}")
    annotationProcessor("${versions["lombok"]}")
    compileOnly("${versions["lombok"]}")
    testAnnotationProcessor("${versions["lombok"]}")
    testCompileOnly("${versions["lombok"]}")
    // 单测
    testCompileOnly("${versions["junit"]}")
    testImplementation("${versions["junit.jupiter.api"]}")
    testImplementation("${versions["junit.jupiter.engine"]}")
    // 兼容 mybatis 3.5.9
    implementation("${versions["sqlParser"]}")


}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}

tasks.withType<ShadowJar> {
    manifest {
        attributes(
            "Implementation-Version" to version,
            "Implementation-Title" to rootProject.name,
            "Manifest-Version" to version,
            "Premain-Class" to "com.xiaobaicai.agent.StressTestingAgent",
            "Agent-Class" to "com.xiaobaicai.agent.StressTestingAgent",
            "Can-Redefine-Classes" to true,
            "Can-Retransform-Classes" to true
        )
    }
}


// 设置 build 任务依赖于 shadowJar 任务
tasks.named("build") {
    dependsOn.clear()
    dependsOn("multiModuleZip")
}


tasks.register<Zip>("multiModuleZip") {
    dependsOn("shadowJar") // 确保 shadowJar 先执行

    from("$projectDir/build/libs") {
        into("/libs")
    }
    // 为每个子模块添加 JAR 文件到 ZIP 包中
    subprojects.forEach { subproject ->
        if (subproject.name == "agent-plugins") {
            subproject.subprojects.forEach { sub ->
                from(sub.tasks.named("jar")) {
                    into("/libs/plugins")
                }
            }
        }
    }

    // ZIP 文件的输出目录和文件名
    destinationDirectory.set(file("$projectDir/build/distributions"))
    archiveFileName.set("${project.name}-$version.zip")
}