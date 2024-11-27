plugins {

    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "Stress-Testing-Agent"
include("agent-core")
include("agent-plugins")
include("agent-plugins:spring5-annotation-plugin")
findProject(":agent-plugins:spring5-annotation-plugin")?.name = "spring5-annotation-plugin"
include("agent-plugins:spring5-mvc-annotation-plugin")
findProject(":agent-plugins:spring5-mvc-annotation-plugin")?.name = "spring5-mvc-annotation-plugin"
include("agent-plugins:mybatis3-plugin")
findProject(":agent-plugins:mybatis3-plugin")?.name = "mybatis3-plugin"
include("agent-plugins:javax-servlet-plugin")
findProject(":agent-plugins:javax-servlet-plugin")?.name = "javax-servlet-plugin"
