package com.xiaobaicai.agent.core.utils;

import com.xiaobaicai.agent.core.log.Logger;
import com.xiaobaicai.agent.core.log.LoggerFactory;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.jar.JarFile;

/**
 * @author caijy
 * @description
 * @date 2024/11/18 星期一 17:37
 */
public class PathUtil {

    private static final Logger logger = LoggerFactory.getLogger(PathUtil.class);

    public static JarFile findJarFile(String className) {
        String classResourcePath = className.replaceAll("\\.", "/") + ".class";
        URL resource = ClassLoader.getSystemClassLoader().getResource(classResourcePath);

        if (resource != null) {
            String urlString = resource.toString();
            int insidePathIndex = urlString.indexOf('!');
            boolean isInJar = insidePathIndex > -1;
            if (isInJar) {
                try {
                    urlString = urlString.substring(urlString.indexOf("file:"), insidePathIndex);
                    URI jarUri = new URL(urlString).toURI();
                    return new JarFile(new File(jarUri));
                } catch (Throwable ex) {
                    logger.error("读取jar出现错误，className： " + className);
                }
            }
        }
        return null;
    }

}
