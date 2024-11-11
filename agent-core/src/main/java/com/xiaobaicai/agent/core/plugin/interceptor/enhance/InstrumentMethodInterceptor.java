package com.xiaobaicai.agent.core.plugin.interceptor.enhance;

import com.xiaobaicai.agent.core.log.Logger;
import com.xiaobaicai.agent.core.log.LoggerFactory;
import com.xiaobaicai.agent.core.utils.IgnoredUtils;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * @author liguang
 * @date 2022/12/16 星期五 4:43 下午
 */
public class InstrumentMethodInterceptor {

    protected static final Logger LOGGER = LoggerFactory.getLogger(InstrumentMethodInterceptor.class);

    private MethodAroundInterceptorV1 interceptor;

    public InstrumentMethodInterceptor(MethodAroundInterceptorV1 interceptor) {
        this.interceptor = interceptor;
    }

    @RuntimeType
    public Object intercept(@This Object obj, @Origin Class<?> clazz, @AllArguments Object[] allArguments,
                            @Origin Method method,
                            @SuperCall Callable<?> callable) throws Throwable {

        if (IgnoredUtils.ignoredMethods(method.getName())) {
            return callable.call();
        }

        if (interceptor.isInvalid(obj, clazz, method, allArguments, method.getParameterTypes())) {
            return callable.call();
        }

        LOGGER.info(clazz.getName() + "." + method.getName() + " start");

        try {
            interceptor.beforeMethod(obj, clazz, method, allArguments, method.getParameterTypes());
        } catch (Throwable e) {

        }
        Object call = null;
        try {
            call = callable.call();
        } catch (Throwable e) {
            throw e;
        }

        try {
            LOGGER.info(clazz.getName() + "." + method.getName() + "  end");
            interceptor.afterMethod(obj, clazz, method, allArguments, method.getParameterTypes());
        } catch (Throwable e) {

        }
        return call;
    }
}
