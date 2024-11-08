package com.xiaobaicai.agent;

import com.xiaobaicai.agent.core.log.Logger;
import com.xiaobaicai.agent.core.log.LoggerFactory;
import com.xiaobaicai.agent.core.plugin.AbstractClassEnhancePluginDefine;
import com.xiaobaicai.agent.core.plugin.PluginFinder;
import com.xiaobaicai.agent.core.plugin.PluginUtil;
import com.xiaobaicai.agent.core.plugin.loader.AgentClassLoader;
import net.bytebuddy.agent.builder.AgentBuilder;

import java.lang.instrument.Instrumentation;
import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * @author caijy
 * @description
 * @date 2024/11/6 星期三 16:04
 */
public class StressTestingAgent {

    private static final Logger LOGGER = LoggerFactory.getLogger(StressTestingAgent.class);

    public static void premain(String agentArgs, Instrumentation inst) {
        LOGGER.info("StressTestingAgent >> premain");

        AgentClassLoader.initDefaultLoader();

        PluginFinder pluginFinder = new PluginFinder(PluginUtil.loadPlugin());

        AgentBuilder agentBuilder = new AgentBuilder.Default().ignore(
                nameStartsWith("net.bytebuddy.")
                        .or(nameStartsWith("org.slf4j."))
                        .or(nameStartsWith("org.groovy."))
                        .or(nameContains("javassist"))
                        .or(nameContains(".asm."))
                        .or(nameContains(".reflectasm."))
                        .or(nameStartsWith("sun.reflect"))
                        .or(isSynthetic()));

        agentBuilder.type(pluginFinder.buildMatch())
                .transform((builder, type, classLoader, module) -> {
                    try {
                        List<AbstractClassEnhancePluginDefine> enhancePluginDefines = pluginFinder.find(type);
                        for (AbstractClassEnhancePluginDefine pluginDefine : enhancePluginDefines) {
                            builder = pluginDefine.enhance(builder, type, classLoader);
                        }
                        return builder;
                    } catch (Throwable e) {

                    }
                    return builder;
                }).installOn(inst);

    }

    public static void agentmain(String agentArgs, Instrumentation inst) {

    }

}
