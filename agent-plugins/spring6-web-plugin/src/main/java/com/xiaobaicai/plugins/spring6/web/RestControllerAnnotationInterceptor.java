package com.xiaobaicai.plugins.spring6.web;

import com.xiaobaicai.agent.core.plugin.context.ContextManager;
import com.xiaobaicai.agent.core.plugin.interceptor.enhance.MethodAroundInterceptorV1;
import com.xiaobaicai.agent.core.trace.ComponentDefine;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;

/**
 * @author xiaobaicai
 * @description 关注微信公众号【程序员小白菜】领取源码
 * @date 2024/12/25 星期三 14:47
 */
public class RestControllerAnnotationInterceptor implements MethodAroundInterceptorV1 {

    @Override
    public void beforeMethod(Object obj, Class<?> clazz, Method method, Object[] allArguments, Class<?>[] argumentsTypes) {
        String url = null;
        if (ContextManager.isRoot()) {
            url = this.getControllerURL(clazz, method);
        }
        if (StringUtils.isEmpty(url)) {
            url = clazz.getName() + "." + method.getName();
            ContextManager.createSpan(ComponentDefine.SPRING, url);
            return;
        }
        ContextManager.createSpan(ComponentDefine.MVC, url);
    }

    @Override
    public void afterMethod(Object obj, Class<?> clazz, Method method, Object[] allArguments, Class<?>[] argumentsTypes) {
        ContextManager.finishSpan();
    }

    private String getControllerURL(Class<?> clazz, Method method) {
        String[] prePaths = clazz.getDeclaredAnnotation(RequestMapping.class).value();
        String prefix = "";
        if (prePaths != null && prePaths.length > 0) {
            prefix = prePaths[0];
        }
        String methodType = this.getAcceptedMethodTypes(method);
        String methodUrl = this.getRequestURL(method);
        if (StringUtils.isEmpty(methodType) && StringUtils.isEmpty(methodUrl)) {
            return "";
        }
        return methodType + " " + prefix + (methodUrl.startsWith("/") ? "" : "/") + methodUrl;
    }

    private String getRequestURL(Method method) {
        return ParsePathUtil.recursiveParseMethodAnnotation(method, m -> {
            String requestURL = null;
            GetMapping getMapping = AnnotationUtils.getAnnotation(m, GetMapping.class);
            PostMapping postMapping = AnnotationUtils.getAnnotation(m, PostMapping.class);
            PutMapping putMapping = AnnotationUtils.getAnnotation(m, PutMapping.class);
            DeleteMapping deleteMapping = AnnotationUtils.getAnnotation(m, DeleteMapping.class);
            PatchMapping patchMapping = AnnotationUtils.getAnnotation(m, PatchMapping.class);
            if (getMapping != null) {
                if (getMapping.value().length > 0) {
                    requestURL = getMapping.value()[0];
                } else if (getMapping.path().length > 0) {
                    requestURL = getMapping.path()[0];
                }
            } else if (postMapping != null) {
                if (postMapping.value().length > 0) {
                    requestURL = postMapping.value()[0];
                } else if (postMapping.path().length > 0) {
                    requestURL = postMapping.path()[0];
                }
            } else if (putMapping != null) {
                if (putMapping.value().length > 0) {
                    requestURL = putMapping.value()[0];
                } else if (putMapping.path().length > 0) {
                    requestURL = putMapping.path()[0];
                }
            } else if (deleteMapping != null) {
                if (deleteMapping.value().length > 0) {
                    requestURL = deleteMapping.value()[0];
                } else if (deleteMapping.path().length > 0) {
                    requestURL = deleteMapping.path()[0];
                }
            } else if (patchMapping != null) {
                if (patchMapping.value().length > 0) {
                    requestURL = patchMapping.value()[0];
                } else if (patchMapping.path().length > 0) {
                    requestURL = patchMapping.path()[0];
                }
            }
            return requestURL;
        });
    }

    public String getAcceptedMethodTypes(Method method) {
        return ParsePathUtil.recursiveParseMethodAnnotation(method, m -> {
            if (AnnotationUtils.getAnnotation(m, GetMapping.class) != null) {
                return "GET";
            } else if (AnnotationUtils.getAnnotation(m, PostMapping.class) != null) {
                return "POST";
            } else if (AnnotationUtils.getAnnotation(m, PutMapping.class) != null) {
                return "PUT";
            } else if (AnnotationUtils.getAnnotation(m, DeleteMapping.class) != null) {
                return "DELETE";
            } else if (AnnotationUtils.getAnnotation(m, PatchMapping.class) != null) {
                return "PATCH";
            } else {
                return null;
            }
        });
    }
}
