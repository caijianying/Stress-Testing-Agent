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

    private final String className;

    private final String rootProjectName;

    public AgentLogger(Class clazz) {
        Package pkg = clazz.getPackage();
        this.className = makePackageNameShorter(clazz.getPackageName() + "." + clazz.getSimpleName());
        String projectVersion = pkg.getImplementationVersion();
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
