package com.xiaobaicai.plugins.spring6.webmvc;

import com.xiaobaicai.agent.core.constants.StressTestingConstant;
import com.xiaobaicai.agent.core.plugin.context.ContextManager;
import com.xiaobaicai.agent.core.plugin.interceptor.enhance.MethodAroundInterceptorV1;
import com.xiaobaicai.agent.core.trace.ComponentDefine;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author caijy
 * @description
 * @date 2024/11/26 星期二 18:11
 */
public class DispatcherServletInterceptor implements MethodAroundInterceptorV1 {

    @Override
    public void beforeMethod(Object obj, Class<?> clazz, Method method, Object[] allArguments, Class<?>[] argumentsTypes) throws Throwable {

        String url = null;
        for (Object allArgument : allArguments) {
            boolean matched = Arrays.stream(allArgument.getClass().getInterfaces()).anyMatch(t -> "jakarta.servlet.http.HttpServletRequest".equals(t.getName()));
            String headerValue = null;
            if (matched) {
                Method getHeader = allArgument.getClass().getDeclaredMethod("getHeader", String.class);
                getHeader.setAccessible(true);
                Object invoke = getHeader.invoke(allArgument, StressTestingConstant.HEADER_NAME_STRESS_TESTING_FLAG);
                headerValue = invoke == null ? null : invoke.toString();

                Method getRequestURI = allArgument.getClass().getDeclaredMethod("getRequestURI");
                getRequestURI.setAccessible(true);
                url = (String) getRequestURI.invoke(allArgument);
            }

            if (headerValue != null) {
                ContextManager.setProperty(StressTestingConstant.IN_PT_KEY, StressTestingConstant.HEADER_VALUE_STRESS_TESTING_FLAG.equals(headerValue));
            }
        }
        ContextManager.createSpan(ComponentDefine.MVC, url);
    }

    @Override
    public void afterMethod(Object obj, Class<?> clazz, Method method, Object[] allArguments, Class<?>[] argumentsTypes) {
        ContextManager.finishSpan();
    }

    @Override
    public boolean sendMeltDownMessage() {
        return true;
    }
}
