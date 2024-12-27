plugins {

    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "Stress-Testing-Agent"
include("agent-core")
include("agent-plugins")
include("agent-plugins:spring6-annotation-plugin")
findProject(":agent-plugins:spring6-annotation-plugin")?.name = "spring6-annotation-plugin"
include("agent-plugins:mybatis3-plugin")
findProject(":agent-plugins:mybatis3-plugin")?.name = "mybatis3-plugin"
include("agent-plugins:spring6-web-plugin")
findProject(":agent-plugins:spring6-web-plugin")?.name = "spring6-web-plugin"
include("agent-plugins:mysql-connector-9-plugin")
findProject(":agent-plugins:mysql-connector-9-plugin")?.name = "mysql-connector-9-plugin"
include("agent-plugins:spring6-webmvc-plugin")
findProject(":agent-plugins:spring6-webmvc-plugin")?.name = "spring6-webmvc-plugin"
