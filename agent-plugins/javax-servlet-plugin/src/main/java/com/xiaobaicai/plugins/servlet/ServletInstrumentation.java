package com.xiaobaicai.plugins.servlet;

import com.xiaobaicai.agent.core.plugin.AbstractClassEnhancePluginDefine;
import com.xiaobaicai.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import com.xiaobaicai.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import com.xiaobaicai.agent.core.plugin.match.ClassMatch;
import com.xiaobaicai.agent.core.plugin.match.NameMatch;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * @author caijy
 * @description
 * @date 2024/11/26 星期二 18:01
 */
public class ServletInstrumentation extends AbstractClassEnhancePluginDefine {

    private static final String ENHANCE_CLASS = "org.springframework.web.servlet.DispatcherServlet";

    private static final String ENHANCE_METHOD = "doDispatch";

    @Override
    protected ClassMatch enhanceClass() {
        return NameMatch.byName(ENHANCE_CLASS);
    }

    @Override
    public InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
        return new InstanceMethodsInterceptPoint[]{
                new InstanceMethodsInterceptPoint() {

                    @Override
                    public ElementMatcher<MethodDescription> getMethodsMatcher() {
                        return ElementMatchers.named(ENHANCE_METHOD);
                    }

                    @Override
                    public String getMethodsInterceptor() {
                        return ServletInterceptor.class.getName();
                    }

                    @Override
                    public boolean isOverrideArgs() {
                        return false;
                    }
                }
        };
    }

    @Override
    public ConstructorInterceptPoint[] getConstructorsInterceptPoints() {
        return new ConstructorInterceptPoint[0];
    }

    @Override
    public boolean useEnhancedInstance() {
        return false;
    }
}
