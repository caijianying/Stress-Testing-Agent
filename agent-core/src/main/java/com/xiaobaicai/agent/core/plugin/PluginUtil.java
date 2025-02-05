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
import com.xiaobaicai.agent.core.log.Logger;
import com.xiaobaicai.agent.core.log.LoggerFactory;
import com.xiaobaicai.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import com.xiaobaicai.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import com.xiaobaicai.agent.core.plugin.interceptor.enhance.InstrumentMethodInterceptor;
import com.xiaobaicai.agent.core.plugin.interceptor.enhance.MethodAroundInterceptorV1;
import com.xiaobaicai.agent.core.plugin.loader.AgentClassLoader;
import com.xiaobaicai.agent.core.plugin.match.ClassMatch;
import com.xiaobaicai.agent.core.plugin.match.IndirectMatch;
import com.xiaobaicai.agent.core.plugin.match.NameMatch;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import static net.bytebuddy.jar.asm.Opcodes.ACC_PRIVATE;
import static net.bytebuddy.jar.asm.Opcodes.ACC_VOLATILE;

/**
 * @author liguang
 * @date 2022/12/30 星期五 4:02 下午
 */
public class PluginUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(PluginUtil.class);

    public static final String CONTEXT_ATTR_NAME = "_$EnhancedClassField_ws";

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
            e.printStackTrace();
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

    public static AgentBuilder loadPluginsThenTransfer(AgentBuilder initialAgentBuilder) {
        AgentBuilder agentBuilder = initialAgentBuilder;
        for (AbstractClassEnhancePluginDefine pluginDefine : loadPlugin()) {
            ElementMatcher.Junction typeJunction = null;
            ClassMatch classMatch = pluginDefine.enhanceClass();
            if (classMatch instanceof NameMatch) {
                NameMatch nameMatch = (NameMatch) classMatch;
                typeJunction = ElementMatchers.named(nameMatch.getClassName());
            }
            if (classMatch instanceof IndirectMatch) {
                IndirectMatch indirectMatch = (IndirectMatch) classMatch;
                typeJunction = indirectMatch.buildJunction();
            }
            if (typeJunction == null) {
                continue;
            }

            InstanceMethodsInterceptPoint[] interceptPoints = pluginDefine.getInstanceMethodsInterceptPoints();
            for (InstanceMethodsInterceptPoint point : interceptPoints) {
                Object newInstance = null;
                try {
                    newInstance = AgentClassLoader.getDefault().loadClass(point.getMethodsInterceptor()).newInstance();
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                Object finalNewInstance = newInstance;
                agentBuilder = agentBuilder.type(typeJunction)
                        .transform((builder, typeDescription, classLoader, module) ->
                                {
                                    if (pluginDefine.useEnhancedInstance()) {
                                        if (!typeDescription.isAssignableTo(EnhancedInstance.class)) {
                                            builder = builder.defineField(
                                                            CONTEXT_ATTR_NAME, Object.class, ACC_PRIVATE | ACC_VOLATILE)
                                                    .implement(EnhancedInstance.class)
                                                    .intercept(FieldAccessor.ofField(CONTEXT_ATTR_NAME));
                                        }
                                    }
                                    builder = builder.method(point.getMethodsMatcher())
                                            .intercept(MethodDelegation.to(new InstrumentMethodInterceptor((MethodAroundInterceptorV1) finalNewInstance)));
                                    return builder;
                                }
                        );
            }
        }

        return agentBuilder;
    }

}
