package com.xiaobaicai.agent.core.boot;

import com.xiaobaicai.agent.core.log.Logger;
import com.xiaobaicai.agent.core.log.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author xiaobaicai
 * @date 2022/12/19 星期一 10:02 上午
 */
public class AgentPkgPath {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgentPkgPath.class);

    private static File AGENT_PKG_PATH;

    public static File getPath() {
        if (AGENT_PKG_PATH == null) {
            AGENT_PKG_PATH = findPath();
        }
        return AGENT_PKG_PATH;
    }

    public static boolean isPathFound() {
        return AGENT_PKG_PATH != null;
    }

    private static File findPath() {
        String classResourcePath = AgentPkgPath.class.getName().replaceAll("\\.", "/") + ".class";

        URL resource = ClassLoader.getSystemClassLoader().getResource(classResourcePath);
        if (resource != null) {
            String urlString = resource.toString();
            int insidePathIndex = urlString.indexOf('!');
            boolean isInJar = insidePathIndex > -1;

            if (isInJar) {
                urlString = urlString.substring(urlString.indexOf("file:"), insidePathIndex);
                File agentJarFile = null;
                try {
                    agentJarFile = new File(new URL(urlString).toURI());
                } catch (MalformedURLException | URISyntaxException e) {
                    System.err.println(String.format("Can not locate agent jar file by url: %s.", urlString));
                }
                assert agentJarFile != null;
                if (agentJarFile.exists()) {
                    return agentJarFile.getParentFile();
                }
            } else {
                int prefixLength = "file:".length();
                String classLocation = urlString.substring(
                        prefixLength, urlString.length() - classResourcePath.length());
                return new File(classLocation);
            }
        }
        LOGGER.error("Can not locate agent jar file.");
        return null;
    }
}
