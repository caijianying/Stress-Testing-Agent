package com.xiaobaicai.agent.core.plugin.interceptor.enhance;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import com.xiaobaicai.agent.core.model.TraceSegmentModel;
import com.xiaobaicai.agent.core.plugin.context.RuntimeContext;
import com.xiaobaicai.agent.core.plugin.context.TrackContext;
import com.xiaobaicai.agent.core.plugin.context.TrackManager;
import com.xiaobaicai.agent.core.trace.ComponentDefine;
import com.xiaobaicai.agent.core.utils.TraceSegmentBuilder;

/**
 * @author liguang
 * @date 2022/12/16 星期五 4:54 下午
 */
public class MethodInvocationContext {

    private static final String START_MILLI = "startMilli";

    private boolean isContinue = true;

    private static final ThreadLocal<AtomicInteger> DEPTH_CONTEXT = new ThreadLocal<>();

    private ThreadLocal<RuntimeContext> RUNTIME_CONTEXT = new ThreadLocal<RuntimeContext>();

    public boolean isContinue() {
        return isContinue;
    }

    public void setContinue(boolean isContinue) {
        this.isContinue = isContinue;
    }

    public void start(String interceptorName, ComponentDefine component, String fullMethodName) {
        String traceId = TrackManager.getCurrentSpan();
        if (null == traceId) {
            traceId = UUID.randomUUID().toString();
            TrackContext.setTraceId(traceId);
        }

        Long startMilli = null;
        Integer depth = null;
        try {
            depth = increase();
            TrackManager.createEntrySpan();
            TraceSegmentBuilder.add(TraceSegmentModel.builder().processFlag(1)
                    .methodName(fullMethodName).componentName(component.name()).depth(depth).build());
            startMilli = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        } catch (Throwable e) {

        } finally {
            getRuntimeContext().set(START_MILLI, startMilli);
        }
    }

    private static int increase() {
        AtomicInteger depth = DEPTH_CONTEXT.get();
        if (depth == null) {
            depth = new AtomicInteger(0);
        }
        int result = depth.incrementAndGet();
        DEPTH_CONTEXT.set(depth);
        return result;
    }

    public void stop(String interceptorName, ComponentDefine component, String fullMethodName) {
        try {
            long stopMilli = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
            // 方法退出即减一 保证depth的准确性
            decrease();
            TraceSegmentBuilder.add(TraceSegmentModel.builder().processFlag(0).methodName(fullMethodName).componentName(
                    component.name()).costTimeStamp(
                    stopMilli - Long.valueOf(getRuntimeContext().get(START_MILLI).toString())).build());
            TrackManager.getExitSpan();
        } catch (Throwable e) {
        } finally {
            clear();
        }
    }

    private static int decrease() {
        AtomicInteger depth = DEPTH_CONTEXT.get();
        if (depth == null) {
            depth = new AtomicInteger(0);
        }
        int result = depth.decrementAndGet();
        DEPTH_CONTEXT.set(depth);
        return result;
    }

    public void clear() {
        DEPTH_CONTEXT.remove();
        RUNTIME_CONTEXT.remove();
    }

    public RuntimeContext getRuntimeContext() {
        RuntimeContext runtimeContext = RUNTIME_CONTEXT.get();
        if (runtimeContext == null) {
            runtimeContext = new RuntimeContext(RUNTIME_CONTEXT);
            RUNTIME_CONTEXT.set(runtimeContext);
        }
        return runtimeContext;
    }
}
