package com.xiaobaicai.agent.core.log;

import java.time.LocalDateTime;

/**
 * @author caijy
 * @description
 * @date 2024/11/6 星期三 17:52
 */
public class AgentLogger implements Logger {

    String BANNER = "                _   _                \n" +
            "                 | | (_)               \n" +
            " _ __ _   _ _ __ | |_ _ _ __ ___   ___ \n" +
            "| '__| | | | '_ \\| __| | '_ ` _ \\ / _ \\\n" +
            "| |  | |_| | | | | |_| | | | | | |  __/\n" +
            "|_|   \\__,_|_| |_|\\__|_|_| |_| |_|\\___|  \n" +
            " :: %s ::     (v%s)\n";

    private String className;

    private String projectVersion;

    private String rootProjectName;

    public AgentLogger(Class clazz) {
        Package pkg = clazz.getPackage();
        this.className = clazz.getPackageName() + "." + clazz.getSimpleName();
        this.projectVersion = pkg.getImplementationVersion();
        this.rootProjectName = pkg.getImplementationTitle();
        System.out.printf(BANNER, rootProjectName, projectVersion);
    }

    @Override
    public void info(String msg) {
        System.out.printf("%s INFO  --- [ %s ] %s             :%s\n", LocalDateTime.now(), rootProjectName, className, msg);
    }

    @Override
    public void error(String msg) {
        System.err.printf("%s ERROR  --- [ %s ] %s             :%s\n", LocalDateTime.now(), rootProjectName, className, msg);
    }
}
