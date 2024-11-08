package com.xiaobaicai.agent.core.utils;

import com.xiaobaicai.agent.core.constants.ClassFilterConstants;

/**
 * @author liguang
 * @date 2022/8/8 星期一 8:54 下午
 */
public class IgnoredUtils {

    public static boolean ignoredMethods(String methodName) {
        if (ClassFilterConstants.MYBATIS_PLUS_IGNORED_METHODS.contains(methodName)) {
            return true;
        }
        if (ClassFilterConstants.JDK_IGNORED_METHODS.contains(methodName)) {
            return true;
        }
        return false;
    }

    public static boolean ignoredSpecificSymbol(String methodName) {
        if (methodName.startsWith("$")) {
            return true;
        }
        return false;
    }
}
