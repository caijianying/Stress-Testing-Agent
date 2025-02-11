package com.xiaobaicai.agent.core.plugin;

import com.xiaobaicai.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import com.xiaobaicai.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import com.xiaobaicai.agent.core.plugin.match.ClassMatch;

/**
 * @author liguang
 * @date 2022/12/30 星期五 1:35 下午
 */
public abstract class AbstractClassEnhancePluginDefine {

    protected abstract ClassMatch enhanceClass();

    public abstract InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints();

    public abstract ConstructorInterceptPoint[] getConstructorsInterceptPoints();

    public abstract boolean useEnhancedInstance();
}
