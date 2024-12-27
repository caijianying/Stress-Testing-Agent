package com.xiaobaicai.agent.plugins.spring6.annotation;

import com.xiaobaicai.agent.core.plugin.context.ContextManager;
import com.xiaobaicai.agent.core.plugin.interceptor.enhance.MethodAroundInterceptorV1;
import com.xiaobaicai.agent.core.trace.ComponentDefine;

import java.lang.reflect.Method;


/**
 * @author liguang
 * @date 2022/12/21 星期三 3:57 下午
 */
public class SpringAnnotationInterceptor implements MethodAroundInterceptorV1 {

    @Override
    public void beforeMethod(Object obj, Class<?> clazz, Method method, Object[] allArguments,
                             Class<?>[] argumentsTypes) {

        String methodName = clazz.getName() + "." + method.getName();
        ContextManager.createSpan(ComponentDefine.SPRING, methodName);
    }

    @Override
    public void afterMethod(Object obj, Class<?> clazz, Method method, Object[] allArguments, Class<?>[] argumentsTypes) {
        ContextManager.finishSpan();
    }

}
