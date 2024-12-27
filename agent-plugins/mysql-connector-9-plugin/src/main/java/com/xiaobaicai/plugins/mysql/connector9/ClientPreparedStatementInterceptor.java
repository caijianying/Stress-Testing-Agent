package com.xiaobaicai.plugins.mysql.connector9;

import com.mysql.cj.jdbc.ClientPreparedStatement;
import com.xiaobaicai.agent.core.plugin.context.ContextManager;
import com.xiaobaicai.agent.core.plugin.interceptor.enhance.MethodAroundInterceptorV1;
import com.xiaobaicai.agent.core.trace.ComponentDefine;

import java.lang.reflect.Method;

/**
 * @author xiaobaicai
 * @description 关注微信公众号【程序员小白菜】领取源码
 * @date 2024/12/25 星期三 15:03
 */
public class ClientPreparedStatementInterceptor implements MethodAroundInterceptorV1 {

    @Override
    public void beforeMethod(Object obj, Class<?> clazz, Method method, Object[] allArguments, Class<?>[] argumentsTypes) {
        String preparedSql = null;
        try {
            if (obj instanceof ClientPreparedStatement) {
                ClientPreparedStatement stmt = (ClientPreparedStatement) obj;
                preparedSql = stmt.getPreparedSql();
            }
        } catch (Throwable ignored) {

        }

        ContextManager.createSpan(ComponentDefine.SQL, method.getName() + " Execute SQL. " + (preparedSql == null ? "" : preparedSql));
    }

    @Override
    public void afterMethod(Object obj, Class<?> clazz, Method method, Object[] allArguments, Class<?>[] argumentsTypes) {
        ContextManager.finishSpan();
    }
}
