package com.xiaobaicai.agent.core.log;

import com.google.common.collect.Lists;
import com.xiaobaicai.agent.core.constants.LogConstant;

import java.util.List;

/**
 * @author caijy
 * @description
 * @date 2024/10/17 星期四 09:31
 */
public interface Logger {
    void info(String msg);

    void error(String msg);

    /**
     * 为了避免包名过长，缩写包名
     **/
    default String makePackageNameShorter(String className) {
        String[] names = className.split("\\.");
        int nameLen = names.length;
        // 保持className长度为4，超过X则对前X个名字进行【只保留首字母】
        Integer fixLength = nameLen > 0 ? nameLen - LogConstant.CLASSNAME_KEEP_LEN : nameLen;
        if (fixLength > 0) {
            List<String> partList = Lists.newArrayList();
            for (int i = 0; i < nameLen; i++) {
                String part = names[i];
                if (i < fixLength) {
                    part = names[i].substring(0, 1).toLowerCase();
                }
                partList.add(part);
            }
            return String.join(".", partList);
        }
        return className;
    }
}
