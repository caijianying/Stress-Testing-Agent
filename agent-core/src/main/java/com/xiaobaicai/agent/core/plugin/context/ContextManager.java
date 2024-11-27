package com.xiaobaicai.agent.core.plugin.context;

import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONUtil;
import com.xiaobaicai.agent.core.boot.BootService;
import com.xiaobaicai.agent.core.log.Logger;
import com.xiaobaicai.agent.core.log.LoggerFactory;
import com.xiaobaicai.agent.core.model.TraceSegment;
import com.xiaobaicai.agent.core.plugin.span.LocalSpan;
import com.xiaobaicai.agent.core.trace.ComponentDefine;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;

/**
 * @author caijy
 * @description 上下文管理
 * @date 2024/1/26 星期五 4:04 下午
 */
public class ContextManager implements BootService {

    public static final Logger LOGGER = LoggerFactory.getLogger(ContextManager.class);

    private static final LinkedList<String> activeSpanIdMap = new LinkedList<>();

    private static final ThreadLocal<Stack> STACK_THREAD_LOCAL = new ThreadLocal<>();

    private static final ThreadLocal<String> LOCAL_TRACE_ID = new ThreadLocal<>();
    private static final ThreadLocal<Map<String, Object>> LOCAL_PROPERTY = new ThreadLocal<>();

    public static void createSpan(ComponentDefine component, String operatorName) {
        createSpan(component, operatorName, null);
    }

    public static void createSpan(ComponentDefine component, String operatorName, ContextSnapshot snapshot) {
        long start = System.currentTimeMillis();
        LocalSpan localSpan = LocalSpan.builder()
                .isRoot(isRoot(snapshot))
                .traceId(getOrCreateTraceId(snapshot))
                .operatorName(operatorName)
                .componentDefine(component)
                .spanId(UUID.randomUUID().toString())
                .parentSpanId(getParentSpanId(snapshot))
                .build();
        getStack().push(localSpan.getSpanId());
        activeSpanIdMap.addLast(localSpan.getSpanId());
        startSpan(localSpan, start);
        LOGGER.info("createSpan => " + JSONUtil.toJsonStr(localSpan));
    }

    private static Stack<String> getStack() {
        if (STACK_THREAD_LOCAL.get() == null) {
            Stack<String> activeSpanIdStack = new Stack<>();
            STACK_THREAD_LOCAL.set(activeSpanIdStack);
        }
        return STACK_THREAD_LOCAL.get();
    }

    private static void startSpan(LocalSpan localSpan, Long start) {

        RuntimeContext.TraceModel model = new RuntimeContext.TraceModel();
        model.setRoot(localSpan.getIsRoot());
        model.setComponent(localSpan.getComponentDefine().name());
        model.setName(localSpan.getOperatorName());
        model.setStart(start);
        model.setTraceId(localSpan.getTraceId());
        model.setSpanId(localSpan.getSpanId());
        model.setParentSpanId(localSpan.getParentSpanId());

        if (localSpan.getIsRoot()) {
            model.setParentSpanId("0");
        }
        RuntimeContext.registerTraceInfo(model);
    }

    public static void stopSpan() {
        String traceId = LOCAL_TRACE_ID.get();
        if (getStack().size() > 0) {
            String spanId = getStack().pop();
            RuntimeContext.exit(traceId, spanId);
            activeSpanIdMap.remove(spanId);
            LOGGER.info("stopSpan =>  traceId=" + traceId + ",spanId:" + spanId);
        }
        LOGGER.info("stopSpan => activeSpanIdMap.isEmpty(): " + activeSpanIdMap.isEmpty());
        LOGGER.info("stopSpan => activeSpanIdMap: " + JSONUtil.toJsonStr(activeSpanIdMap));
        if (activeSpanIdMap.isEmpty()) {
            printInformation(traceId);
            clear(traceId);
        }
    }

    private static void clear(String traceId) {
        if (traceId != null) {
            RuntimeContext.clearTrace(traceId);
        }
        LOCAL_TRACE_ID.remove();
        activeSpanIdMap.clear();
        STACK_THREAD_LOCAL.remove();
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

    private static String getParentSpanId(ContextSnapshot contextSnapshot) {
        if (contextSnapshot != null) {
            return contextSnapshot.getSpanId();
        }
        if (!getStack().isEmpty()) {
            return getStack().peek();
        }
        return null;
    }

    public static boolean isActive() {
        return !activeSpanIdMap.isEmpty();
    }

    private static void printInformation(String traceId) {
        TraceSegment traceSegment = RuntimeContext.getTraceSegment(traceId);
        LOGGER.info(JSONUtil.toJsonStr(traceSegment));
    }

    public static String getOrCreateTraceId(ContextSnapshot contextSnapshot) {
        String traceId = LOCAL_TRACE_ID.get();
        if (traceId == null) {
            if (contextSnapshot != null) {
                traceId = contextSnapshot.getTraceId();
            } else {
                traceId = UUID.fastUUID().toString();
            }
            LOCAL_TRACE_ID.set(traceId);
        }
        return traceId;
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
    public void boot() throws Throwable {

    }

    @Override
    public void shutdown() throws Throwable {

    }
}
