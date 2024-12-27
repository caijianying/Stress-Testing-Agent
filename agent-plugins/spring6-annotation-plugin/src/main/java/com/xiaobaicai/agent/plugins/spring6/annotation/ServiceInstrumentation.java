package com.xiaobaicai.agent.plugins.spring6.annotation;

import com.xiaobaicai.agent.core.plugin.interceptor.ConstructorInterceptPoint;

/**
 * @author liguang
 * @date 2022/12/30 星期五 3:41 下午
 */
public class ServiceInstrumentation extends AbstractSpringAnnotationInstrumentation{


    public static final String ENHANCE_ANNOTATION = "org.springframework.stereotype.Service";

    @Override
    protected String[] getEnhanceAnnotations() {
        return new String[]{ENHANCE_ANNOTATION};
    }

    @Override
    public ConstructorInterceptPoint[] getConstructorsInterceptPoints() {
        return new ConstructorInterceptPoint[0];
    }
}
