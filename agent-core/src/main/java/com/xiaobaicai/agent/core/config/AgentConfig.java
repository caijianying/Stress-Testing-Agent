package com.xiaobaicai.agent.core.config;

import cn.hutool.core.util.StrUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xiaobaicai
 * @description 关注微信公众号【程序员小白菜】领取源码
 * @date 2024/12/25 星期三 16:05
 */
public class AgentConfig {
    private static final Map<String, String> properties = new HashMap<>();
    private static final String SHADOW_MODE_KEY = "shadowMode";
    public static final String SHADOW_MODE_DB = "DB";
    public static final String SHADOW_MODE_TABLE = "TABLE";
    private static final String DEFAULT_SHADOW_MODE = SHADOW_MODE_TABLE;

    public static void readArgs(String agentArgs) {
        if (StrUtil.isBlank(agentArgs)) {
            return;
        }
        String[] args = agentArgs.split("&");
        for (String arg : args) {
            String[] param = arg.split("=");
            properties.put(param[0], param[1]);
        }
    }

    private static String getProperty(String key) {
        return properties.get(key);
    }

    public static String getShadowMode() {
        String shadowModel = properties.get(SHADOW_MODE_KEY);
        return shadowModel == null ? DEFAULT_SHADOW_MODE : shadowModel;
    }
}
