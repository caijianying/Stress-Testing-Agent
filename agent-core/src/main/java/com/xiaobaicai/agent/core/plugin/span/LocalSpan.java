package com.xiaobaicai.agent.core.plugin.span;

import com.xiaobaicai.agent.core.trace.ComponentDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author caijy
 * @description isRoot, traceId, component, name, parentSpanId spanId
 * @date 2024/1/26 星期五 5:05 下午
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class LocalSpan implements Serializable {
    @Serial
    private static final long serialVersionUID = -7335003348952891472L;

    private boolean isRoot;
    private String traceId;
    private String spanId;
    private String parentSpanId;

    /**
     * 链路名称
     **/
    private String operateName;

    private Long startTime;

    private Long costTime;

    private ComponentDefine componentDefine;

    public void start() {
        this.startTime = System.currentTimeMillis();
    }

    public void finish() {
        this.costTime = System.currentTimeMillis() - startTime;
    }
}
