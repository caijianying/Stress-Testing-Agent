package com.xiaobaicai.agent.plugins.mybatis3;

import com.xiaobaicai.agent.core.plugin.AbstractClassEnhancePluginDefine;
import com.xiaobaicai.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import com.xiaobaicai.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import com.xiaobaicai.agent.core.plugin.match.ClassMatch;
import com.xiaobaicai.agent.core.plugin.match.NameMatch;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import org.apache.ibatis.session.SqlSession;

import static net.bytebuddy.matcher.ElementMatchers.returns;

/**
 * @author caijy
 * @description
 * @date 2024/11/27 星期三 17:18
 */
public class DefaultSqlSessionFactoryInstrumentation extends AbstractClassEnhancePluginDefine {

    private static final String ENHANCE_CLASS = "org.apache.ibatis.session.defaults.DefaultSqlSessionFactory";

    private static final String ENHANCE_METHOD = "openSession";

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
                        return ElementMatchers.named(ENHANCE_METHOD).and(returns(SqlSession.class));
                    }

                    @Override
                    public String getMethodsInterceptor() {
                        return DefaultSqlSessionFactoryInterceptor.class.getName();
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
