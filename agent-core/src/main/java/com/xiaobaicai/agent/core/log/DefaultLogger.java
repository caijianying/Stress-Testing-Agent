package com.xiaobaicai.agent.core.log;

import java.time.LocalDateTime;

/**
 * @author caijy
 * @description
 * @date 2024/10/17 星期四 09:33
 */
public class DefaultLogger implements Logger {

    private String className;

    private String projectVersion;

    private String rootProjectName;

    public DefaultLogger(Class clazz) {
        Package pkg = clazz.getPackage();
        this.className = clazz.getPackageName() + "." + clazz.getSimpleName();
        this.projectVersion = pkg.getImplementationVersion();
        this.rootProjectName = pkg.getImplementationTitle();
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
