package com.xiaobaicai.agent.core.log;

import com.google.common.collect.Lists;
import com.xiaobaicai.agent.core.constants.LogConstant;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author caijy
 * @description
 * @date 2024/11/12 星期二 15:02
 */
public abstract class AbstractLogger implements Logger {

    protected final String className;

    protected static final String ROOT_PROJECT_NAME = AbstractLogger.class.getPackage().getImplementationTitle();

    protected AbstractLogger(Class clazz) {
        this.className = makeClassNameShorter(clazz.getPackageName() + "." + clazz.getSimpleName());
    }

    /**
     * 为了避免包名过长，缩写包名
     **/
    protected String makeClassNameShorter(String className) {
        String fixedName = className;
        String[] names = className.split("\\.");
        int nameLen = names.length;
        // 保持className单词长度为4，超过X则对前X个名字进行【只保留首字母】
        int fixLength = nameLen > 0 ? nameLen - LogConstant.CLASSNAME_WORD_KEEP_LEN : nameLen;
        if (fixLength > 0) {
            List<String> partList = Lists.newArrayList();
            for (int i = 0; i < nameLen; i++) {
                String part = names[i];
                if (i < fixLength) {
                    part = names[i].substring(0, 1).toLowerCase();
                }
                partList.add(part);
            }
            fixedName = String.join(".", partList);
        }

        // 对总长进行处理,小于CLASSNAME_KEEP_LEN的剩余长度空格补全
        fixLength = Math.max(fixLength, 0);
        int totalLen = fixedName.length();
        int spaceLen = 0;
        if (totalLen <= LogConstant.CLASSNAME_KEEP_LEN) {
            spaceLen = LogConstant.CLASSNAME_KEEP_LEN - totalLen;
            return fixedName + " ".repeat(spaceLen);
        }

        boolean shorter = true;
        int tmpLen = totalLen;
        String[] fixedNameArr = fixedName.split("\\.");
        List<String> partList = Lists.newArrayList();
        for (int i = 0; i < fixedNameArr.length; i++) {
            String part = fixedNameArr[i];
            if (i >= fixLength && shorter) {
                tmpLen -= (part.length() - 1);
                if (tmpLen <= LogConstant.CLASSNAME_KEEP_LEN) {
                    spaceLen = LogConstant.CLASSNAME_KEEP_LEN - tmpLen;
                    shorter = false;
                }
                part = fixedNameArr[i].substring(0, 1).toLowerCase();
            }
            partList.add(part);
        }
        fixedName = String.join(".", partList);
        return fixedName + " ".repeat(spaceLen);
    }

    @Override
    public void info(String msg) {
        System.out.printf("%s INFO  --- [ %s ] %s: %s\n", LocalDateTime.now(), ROOT_PROJECT_NAME, className, msg);
    }

    @Override
    public void error(String msg) {
        System.err.printf("%s ERROR  --- [ %s ] %s: %s\n", LocalDateTime.now(), ROOT_PROJECT_NAME, className, msg);
    }
}
