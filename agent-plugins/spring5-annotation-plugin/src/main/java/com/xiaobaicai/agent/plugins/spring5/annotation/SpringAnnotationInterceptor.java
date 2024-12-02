package com.xiaobaicai.agent.plugins.spring5.annotation;

import com.xiaobaicai.agent.core.log.Logger;
import com.xiaobaicai.agent.core.log.LoggerFactory;
import com.xiaobaicai.agent.core.plugin.context.ContextManager;
import com.xiaobaicai.agent.core.plugin.interceptor.enhance.MethodAroundInterceptorV1;
import com.xiaobaicai.agent.core.trace.ComponentDefine;

import java.lang.reflect.Method;


/**
 * @author liguang
 * @date 2022/12/21 星期三 3:57 下午
 */
public class SpringAnnotationInterceptor implements MethodAroundInterceptorV1 {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringAnnotationInterceptor.class);

    @Override
    public void beforeMethod(Object obj, Class<?> clazz, Method method, Object[] allArguments,
                             Class<?>[] argumentsTypes) {

        String methodName = clazz.getName() + "." + method.getName();
        ContextManager.createSpan(ComponentDefine.SPRING, methodName);
    }

    @Override
    public void afterMethod(Object obj, Class<?> clazz, Method method, Object[] allArguments, Class<?>[] argumentsTypes) {
        ContextManager.stopSpan();
    }

}
