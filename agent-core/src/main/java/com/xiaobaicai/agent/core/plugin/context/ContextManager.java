package com.xiaobaicai.agent.core.plugin.context;

import cn.hutool.core.collection.CollectionUtil;
import com.xiaobaicai.agent.core.boot.BootService;
import com.xiaobaicai.agent.core.log.Logger;
import com.xiaobaicai.agent.core.log.LoggerFactory;
import com.xiaobaicai.agent.core.plugin.span.LocalSpan;
import com.xiaobaicai.agent.core.trace.ComponentDefine;

import java.util.*;

/**
 * @author caijy
 * @description 上下文管理
 * @date 2024/1/26 星期五 4:04 下午
 */
public class ContextManager implements BootService {

    public static final Logger LOGGER = LoggerFactory.getLogger(ContextManager.class);

    public static final ThreadLocal<Map<String, Object>> LOCAL_PROPERTY = new ThreadLocal<>();

    public static final ThreadLocal<String> LOCAL_TRACE_ID = new ThreadLocal<>();

    public static final ThreadLocal<LinkedList<String>> LOCAL_SPAN_ID_STACK = new ThreadLocal<>();

    public static final ThreadLocal<Map<String, LocalSpan>> LOCAL_SPAN_CACHE = new ThreadLocal<>();

    public static final ThreadLocal<LocalSpan> LOCAL_ROOT_SPAN = new ThreadLocal<>();

    private static final LinkedList<String> activeSpanIdMap = new LinkedList<>();

    private static final ThreadLocal<Stack> STACK_THREAD_LOCAL = new ThreadLocal<>();

    public static void createSpan(ComponentDefine component, String operatorName) {
        createSpan(component, operatorName, null);
    }

    public static void createSpan(ComponentDefine component, String operateName, ContextSnapshot snapshot) {
        boolean root = isRoot();
        if (root) {
            init();
        }
        String spanId = java.util.UUID.randomUUID().toString();
        LocalSpan localSpan = LocalSpan.builder()
                .isRoot(root)
                .traceId(getGlobalTraceId())
                .spanId(spanId)
                .parentSpanId(getParentSpanId())
                .operateName(operateName)
                .componentDefine(component)
                .build();
        register(localSpan);
        localSpan.start();
    }

    public static void finishSpan() {
        // 获取将要出栈的Span
        LinkedList<String> methodStack = LOCAL_SPAN_ID_STACK.get();
        String spanId = methodStack.peekLast();
        LocalSpan localSpan = LOCAL_SPAN_CACHE.get().get(spanId);
        // 耗时记录
        localSpan.finish();
        // 出栈
        methodStack.removeLast();

        if (methodStack.isEmpty()) {
            // 打印trace链路
            printTraceInformation();
            // 释放资源
            remove();
        }
    }

    public static void printTraceInformation() {
        Map<String, LocalSpan> spanMap = LOCAL_SPAN_CACHE.get();
        LocalSpan rootSpan = LOCAL_ROOT_SPAN.get();
        int depth = 0;
        String spanId = rootSpan.getSpanId();
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append("【ROOT】").append(rootSpan.getOperateName()).append("  ").append(rootSpan.getCostTime()).append("ms").append("\n");
        appendChildren(spanMap, spanId, depth, sb);
        LOGGER.info(sb.toString());
    }

    private static void appendChildren(Map<String, LocalSpan> spanMap, String spanId, int depth, StringBuilder sb) {
        List<Map.Entry<String, LocalSpan>> childSpanList = spanMap.entrySet().stream().filter(span -> spanId.equals(span.getValue().getParentSpanId())).toList();
        if (CollectionUtil.isEmpty(childSpanList)) {
            return;
        }
        depth++;
        for (Map.Entry<String, LocalSpan> childSpanEntry : childSpanList) {
            LocalSpan childSpan = childSpanEntry.getValue();
            sb.append("     ".repeat(depth));
            sb.append("【").append(childSpan.getComponentDefine().name()).append("】");
            sb.append(childSpan.getOperateName()).append("  ").append(childSpan.getCostTime()).append("ms").append("\n");
            appendChildren(spanMap, childSpanEntry.getKey(), depth, sb);
        }
    }

    public static void remove() {
        LOCAL_TRACE_ID.remove();
        LOCAL_SPAN_ID_STACK.remove();
        LOCAL_SPAN_CACHE.remove();
        LOCAL_ROOT_SPAN.remove();
        LOCAL_PROPERTY.remove();
    }

    public static void register(LocalSpan localSpan) {
        String spanId = localSpan.getSpanId();
        LOCAL_SPAN_ID_STACK.get().addLast(spanId);
        LOCAL_SPAN_CACHE.get().put(spanId, localSpan);
        if (localSpan.isRoot()) {
            LOCAL_ROOT_SPAN.set(localSpan);
        }
    }

    public static String getGlobalTraceId() {
        return LOCAL_TRACE_ID.get();
    }

    public static void init() {
        LOCAL_TRACE_ID.set(java.util.UUID.randomUUID().toString());
        LOCAL_SPAN_ID_STACK.set(new LinkedList<>());
        LOCAL_SPAN_CACHE.set(new LinkedHashMap<>());
    }

    private static Stack<String> getStack() {
        if (STACK_THREAD_LOCAL.get() == null) {
            Stack<String> activeSpanIdStack = new Stack<>();
            STACK_THREAD_LOCAL.set(activeSpanIdStack);
        }
        return STACK_THREAD_LOCAL.get();
    }

    public static ContextSnapshot capture() {
        return new ContextSnapshot(activeSpanIdMap.isEmpty() ? null : activeSpanIdMap.getLast(), LOCAL_TRACE_ID.get());
    }

    public static boolean isRoot() {
        return LOCAL_TRACE_ID.get() == null;
    }

    public static boolean isRoot(ContextSnapshot contextSnapshot) {
        return LOCAL_TRACE_ID.get() == null && contextSnapshot == null;
    }

    private static String getParentSpanId() {
        return getParentSpanId(null);
    }

    private static String getParentSpanId(ContextSnapshot contextSnapshot) {
        if (contextSnapshot != null) {
            return contextSnapshot.getSpanId();
        }
        LinkedList<String> methodInvokeList = LOCAL_SPAN_ID_STACK.get();
        if (methodInvokeList.isEmpty()) {
            return "0";
        }
        return methodInvokeList.getLast();
    }

    public static void setProperty(String key, Object value) {
        Map<String, Object> properties = LOCAL_PROPERTY.get();
        if (properties == null) {
            properties = new HashMap<>();
            LOCAL_PROPERTY.set(properties);
        }
        properties.put(key, value);
    }

    public static Object getProperty(String key) {
        Map<String, Object> properties = LOCAL_PROPERTY.get();
        return properties == null ? null : properties.get(key);
    }

    @Override
    public void boot() {

    }

    @Override
    public void shutdown() {

    }
}
