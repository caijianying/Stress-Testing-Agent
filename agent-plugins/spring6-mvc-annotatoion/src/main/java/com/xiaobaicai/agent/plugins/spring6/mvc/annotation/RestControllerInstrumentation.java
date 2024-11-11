package com.xiaobaicai.agent.plugins.spring6.mvc.annotation;

import com.xiaobaicai.agent.core.plugin.AbstractClassEnhancePluginDefine;
import com.xiaobaicai.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import com.xiaobaicai.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import com.xiaobaicai.agent.core.plugin.match.ClassAnnotationMatch;
import com.xiaobaicai.agent.core.plugin.match.ClassMatch;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

/**
 * @author liguang
 * @date 2022/12/30 星期五 11:43 上午
 */
public class RestControllerInstrumentation extends AbstractClassEnhancePluginDefine {

    public static final String ENHANCE_ANNOTATION = "org.springframework.web.bind.annotation.RestController";

    public static final String INTERCEPTOR_CLASS = "com.xiaobaicai.agent.plugins.spring6.mvc.annotation.MvcAnnotationInterceptor";

    @Override
    protected ClassMatch enhanceClass() {
        return ClassAnnotationMatch.byClassAnnotationMatch(new String[]{ENHANCE_ANNOTATION});
    }

    @Override
    public ConstructorInterceptPoint[] getConstructorsInterceptPoints() {
        return new ConstructorInterceptPoint[0];
    }

    @Override
    public boolean useEnhancedInstance() {
        return false;
    }

    @Override
    public InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
        return new InstanceMethodsInterceptPoint[]{
                new InstanceMethodsInterceptPoint() {
                    @Override
                    public ElementMatcher<MethodDescription> getMethodsMatcher() {
                        return null;
                    }

                    @Override
                    public String getMethodsInterceptor() {
                        return INTERCEPTOR_CLASS;
                    }

                    @Override
                    public boolean isOverrideArgs() {
                        return false;
                    }
                }
        };
    }
}
