import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Calendar

// 定义一个函数来获取当前日期的版本号
fun getProjectVersion(): String {
    val df = SimpleDateFormat("yyyyMMdd", Locale.CHINA)
    return df.format(Calendar.getInstance().time)
}

var versions = mapOf(
    "guava" to "com.google.guava:guava:31.0.1-jre",
    "hutool-all" to "cn.hutool:hutool-all:5.8.25",
    "javax-servlet" to "javax.servlet:javax.servlet-api:4.0.1",
    "apache-dubbo" to "org.apache.dubbo:dubbo:2.7.0",
    "spring-web" to "org.springframework:spring-web:5.3.22",
    "slf4j" to "org.slf4j:slf4j-api:1.7.36",
    "lombok" to "org.projectlombok:lombok:1.18.34",
    "fastjson" to "com.alibaba:fastjson:1.2.79",
    "javassist" to "javassist:javassist:3.12.1.GA",
    "byte-buddy" to "net.bytebuddy:byte-buddy:1.12.8",
    "byte-buddy-agent" to "net.bytebuddy:byte-buddy-agent:1.12.8",
    "junit" to "junit:junit:4.13.1",
    "junit.jupiter.api" to "org.junit.jupiter:junit-jupiter-api:5.8.2",
    "junit.jupiter.engine" to "org.junit.jupiter:junit-jupiter-engine:5.8.2",
    "mybatis3" to "org.mybatis:mybatis:3.5.9",
    "sqlParser" to "com.github.jsqlparser:jsqlparser:4.5"
)
extra["versions"] = versions

val currentVersion = getProjectVersion()
println("Project Version: $currentVersion")
extra["projectVersion"] = currentVersion

