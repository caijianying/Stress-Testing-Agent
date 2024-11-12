package com.xiaobaicai.agent.core.log;

/**
 * @author caijy
 * @description
 * @date 2024/11/6 星期三 17:52
 */
public class AgentLogger extends AbstractLogger {

    String BANNER = "                _   _                \n" +
            "                 | | (_)               \n" +
            " _ __ _   _ _ __ | |_ _ _ __ ___   ___ \n" +
            "| '__| | | | '_ \\| __| | '_ ` _ \\ / _ \\\n" +
            "| |  | |_| | | | | |_| | | | | | |  __/\n" +
            "|_|   \\__,_|_| |_|\\__|_|_| |_| |_|\\___|  \n" +
            " :: %s ::     (v%s)\n";

    public AgentLogger(Class clazz) {
        super(clazz);
        String projectVersion = clazz.getPackage().getImplementationVersion();
        System.out.printf(BANNER, rootProjectName, projectVersion);
    }
}
