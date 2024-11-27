package com.xiaobaicai.plugins.servlet;

import com.xiaobaicai.agent.core.constants.StressTestingConstant;
import com.xiaobaicai.agent.core.log.Logger;
import com.xiaobaicai.agent.core.log.LoggerFactory;
import com.xiaobaicai.agent.core.plugin.context.ContextManager;
import com.xiaobaicai.agent.core.plugin.interceptor.enhance.MethodAroundInterceptorV1;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

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
            LOGGER.info(allArgument.getClass().getName() + ":   " + (allArgument instanceof HttpServletRequest));
            if (allArgument instanceof HttpServletRequest) {
                HttpServletRequest request = (HttpServletRequest) allArgument;
                String headerValue = request.getHeader(StressTestingConstant.HEADER_NAME_STRESS_TESTING_FLAG);
                ContextManager.setProperty(StressTestingConstant.HEADER_NAME_STRESS_TESTING_FLAG, StressTestingConstant.HEADER_VALUE_STRESS_TESTING_FLAG.equals(headerValue));
                ContextManager.setProperty(StressTestingConstant.SHADOW_MODE_KEY, StressTestingConstant.SHADOW_MODE_DEFAULT_VALUE);
                ContextManager.setProperty(StressTestingConstant.SHADOW_MODE_KEY, StressTestingConstant.SHADOW_MODE_DATABASE_VALUE);
            }
        }
    }

    @Override
    public void afterMethod(Object obj, Class<?> clazz, Method method, Object[] allArguments, Class<?>[] argumentsTypes) throws Throwable {

    }
}
