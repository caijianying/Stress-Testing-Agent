package com.xiaobaicai.agent.core.plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.xiaobaicai.agent.core.log.Logger;
import com.xiaobaicai.agent.core.log.LoggerFactory;
import com.xiaobaicai.agent.core.plugin.loader.AgentClassLoader;

/**
 * @author liguang
 * @date 2022/12/30 星期五 4:02 下午
 */
public class PluginUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(PluginUtil.class);

    public static List<AbstractClassEnhancePluginDefine> loadPlugin() {
        List<PluginDefine> pluginClassList = new LinkedList<>();
        List<AbstractClassEnhancePluginDefine> plugins = new ArrayList<>();
        try {
            List<URL> resources = getResources();
            if (CollectionUtil.isNotEmpty(resources)) {
                for (URL url : resources) {
                    load(url.openStream(), pluginClassList);
                }
            }
            for (PluginDefine pluginDefine : pluginClassList) {
                AbstractClassEnhancePluginDefine plugin = (AbstractClassEnhancePluginDefine) Class.forName(
                        pluginDefine.getDefineClass(), true, AgentClassLoader
                                .getDefault()).newInstance();
                plugins.add(plugin);
            }
        } catch (Throwable e) {
            LOGGER.error("load plugin failure.");
        }
        return plugins;
    }

    public static List<URL> getResources() {
        List<URL> cfgUrlPaths = new ArrayList<URL>();
        Enumeration<URL> urls;
        try {
            urls = AgentClassLoader.getDefault().getResources("plugin.def");

            while (urls.hasMoreElements()) {
                URL pluginUrl = urls.nextElement();
                cfgUrlPaths.add(pluginUrl);
                LOGGER.info("load plugin url: " + pluginUrl);
            }

            return cfgUrlPaths;
        } catch (Throwable e) {
            LOGGER.error("read resources failure.");
        }
        return null;
    }

    public static void load(InputStream input, List<PluginDefine> pluginClassList) throws IOException {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String pluginDefine;
            while ((pluginDefine = reader.readLine()) != null) {
                try {
                    if (pluginDefine.trim().length() == 0 || pluginDefine.startsWith("#")) {
                        continue;
                    }
                    PluginDefine plugin = PluginDefine.build(pluginDefine);
                    pluginClassList.add(plugin);
                } catch (IllegalStateException e) {
                    LOGGER.error("Failed to format plugin(" + pluginDefine + ") define.");
                }
            }
        } finally {
            input.close();
        }
    }

}
