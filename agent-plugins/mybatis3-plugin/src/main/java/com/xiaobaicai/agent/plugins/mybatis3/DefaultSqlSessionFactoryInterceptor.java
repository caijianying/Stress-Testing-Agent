package com.xiaobaicai.agent.plugins.mybatis3;

import com.xiaobaicai.agent.core.constants.StressTestingConstant;
import com.xiaobaicai.agent.core.log.Logger;
import com.xiaobaicai.agent.core.log.LoggerFactory;
import com.xiaobaicai.agent.core.plugin.context.ContextManager;
import com.xiaobaicai.agent.core.plugin.interceptor.enhance.MethodAroundInterceptorV1;
import org.apache.ibatis.session.SqlSession;

import java.lang.reflect.Method;
import java.sql.SQLException;

/**
 * @author caijy
 * @description
 * @date 2024/11/27 星期三 17:21
 */
public class DefaultSqlSessionFactoryInterceptor implements MethodAroundInterceptorV1 {

    private final static Logger LOGGER = LoggerFactory.getLogger(DefaultSqlSessionFactoryInterceptor.class);

    @Override
    public void beforeMethod(Object obj, Class<?> clazz, Method method, Object[] allArguments, Class<?>[] argumentsTypes) throws Throwable {

    }

    @Override
    public void handleResult(Object call) {
        SqlSession sqlSession = (SqlSession) call;
        try {
            ContextManager.setProperty(StressTestingConstant.DATABASE_NAME_KEY, sqlSession.getConnection().getCatalog());
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public void afterMethod(Object obj, Class<?> clazz, Method method, Object[] allArguments, Class<?>[] argumentsTypes) throws Throwable {

    }
}
