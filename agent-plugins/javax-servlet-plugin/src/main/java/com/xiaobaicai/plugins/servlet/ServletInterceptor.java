package com.xiaobaicai.plugins.servlet;

import com.xiaobaicai.agent.core.constants.StressTestingConstant;
import com.xiaobaicai.agent.core.log.Logger;
import com.xiaobaicai.agent.core.log.LoggerFactory;
import com.xiaobaicai.agent.core.plugin.context.ContextManager;
import com.xiaobaicai.agent.core.plugin.interceptor.enhance.MethodAroundInterceptorV1;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author caijy
 * @description
 * @date 2024/11/26 星期二 18:11
 */
public class ServletInterceptor implements MethodAroundInterceptorV1 {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServletInterceptor.class);

    @Override
    public void beforeMethod(Object obj, Class<?> clazz, Method method, Object[] allArguments, Class<?>[] argumentsTypes) throws Throwable {

        for (Object allArgument : allArguments) {
            Class<?>[] interfaces = allArgument.getClass().getInterfaces();
            boolean matched = Arrays.stream(interfaces).anyMatch(c -> c.getName().equals("javax.servlet.http.HttpServletRequest") || c.getName().equals("jakarta.servlet.http.HttpServletRequest"));

            String headerValue = null;
            if (matched) {
                Method getHeader = allArgument.getClass().getDeclaredMethod("getHeader", String.class);
                getHeader.setAccessible(true);
                Object invoke = getHeader.invoke(allArgument, StressTestingConstant.HEADER_NAME_STRESS_TESTING_FLAG);
                headerValue = invoke == null ? null : invoke.toString();
            }

            if (headerValue != null) {
                LOGGER.info("检测到压测流量. ");
                ContextManager.setProperty(StressTestingConstant.HEADER_NAME_STRESS_TESTING_FLAG, StressTestingConstant.HEADER_VALUE_STRESS_TESTING_FLAG.equals(headerValue));
                ContextManager.setProperty(StressTestingConstant.SHADOW_MODE_KEY, StressTestingConstant.SHADOW_MODE_DEFAULT_VALUE);
            }
        }
    }

    @Override
    public void afterMethod(Object obj, Class<?> clazz, Method method, Object[] allArguments, Class<?>[] argumentsTypes) {

    }
}
