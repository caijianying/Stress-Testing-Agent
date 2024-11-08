plugins {

    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "Stress-Testing-Agent"
include("agent-core")
include("agent-plugins")
include("agent-plugins:spring6-mvc-annotatoion")
findProject(":agent-plugins:spring6-mvc-annotatoion")?.name = "spring6-mvc-annotatoion"
