package com.xiaobaicai.agent.core.plugin.interceptor.enhance;

import java.lang.reflect.Method;

/**
 * @author liguang
 * @date 2022/12/16 星期五 4:45 下午
 */
public interface MethodAroundInterceptorV1 {

    default boolean isInvalid(Object obj, Class<?> clazz, Method method, Object[] allArguments,
                              Class<?>[] argumentsTypes) {
        return false;
    }

    void beforeMethod(Object obj, Class<?> clazz, Method method, Object[] allArguments, Class<?>[] argumentsTypes)
            throws Throwable;

    void afterMethod(Object obj, Class<?> clazz, Method method, Object[] allArguments, Class<?>[] argumentsTypes)
            throws Throwable;

    default void handleResult(Object call) {
    }
}
