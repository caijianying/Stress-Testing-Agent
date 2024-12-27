package com.xiaobaicai.agent;

import com.xiaobaicai.agent.core.config.AgentConfig;
import com.xiaobaicai.agent.core.log.Logger;
import com.xiaobaicai.agent.core.log.LoggerFactory;
import com.xiaobaicai.agent.core.plugin.PluginUtil;
import com.xiaobaicai.agent.core.plugin.loader.AgentClassLoader;
import net.bytebuddy.agent.builder.AgentBuilder;
import java.lang.instrument.Instrumentation;
import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * @author caijy
 * @description 关注微信公众号【程序员小白菜】领取源码
 * @date 2024/11/6 星期三 16:04
 */
public class StressTestingAgent {

    private static final Logger LOGGER = LoggerFactory.getLogger(StressTestingAgent.class);

    public static void premain(String agentArgs, Instrumentation inst) {
        LOGGER.info("StressTestingAgent >> premain");

        AgentConfig.readArgs(agentArgs);

        AgentClassLoader.initDefaultLoader();

        AgentBuilder agentBuilder = new AgentBuilder.Default().ignore(
                nameStartsWith("net.bytebuddy.")
                        .or(nameStartsWith("org.slf4j."))
                        .or(nameStartsWith("org.groovy."))
                        .or(nameContains("javassist"))
                        .or(nameContains(".asm."))
                        .or(nameContains(".reflectasm."))
                        .or(nameStartsWith("sun.reflect"))
                        .or(isSynthetic()));

        agentBuilder = PluginUtil.loadPluginsThenTransfer(agentBuilder);


        agentBuilder.with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .installOn(inst);

    }

    public static void agentmain(String agentArgs, Instrumentation inst) {

    }
}
