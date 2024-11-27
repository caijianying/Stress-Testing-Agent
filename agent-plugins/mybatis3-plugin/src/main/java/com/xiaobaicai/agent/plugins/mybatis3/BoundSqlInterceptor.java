package com.xiaobaicai.agent.plugins.mybatis3;

import com.xiaobaicai.agent.core.constants.StressTestingConstant;
import com.xiaobaicai.agent.core.log.Logger;
import com.xiaobaicai.agent.core.log.LoggerFactory;
import com.xiaobaicai.agent.core.plugin.context.ContextManager;
import com.xiaobaicai.agent.core.plugin.interceptor.enhance.MethodAroundInterceptorV1;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.apache.ibatis.mapping.BoundSql;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author caijy
 * @description
 * @date 2024/11/26 星期二 17:46
 */
public class BoundSqlInterceptor implements MethodAroundInterceptorV1 {

    private static final Logger LOGGER = LoggerFactory.getLogger(BoundSqlInterceptor.class);

    private static final Set<Object> invokerSet = new HashSet<>();

    @Override
    public void beforeMethod(Object obj, Class<?> clazz, Method method, Object[] allArguments, Class<?>[] argumentsTypes) {
        Boolean inPt = (Boolean) ContextManager.getProperty(StressTestingConstant.HEADER_NAME_STRESS_TESTING_FLAG);
        if (inPt != null && inPt) {
            String shadowMode = (String) ContextManager.getProperty(StressTestingConstant.SHADOW_MODE_KEY);
            try {
                if (StressTestingConstant.SHADOW_MODE_TABLE_VALUE.equals(shadowMode)) {
                    switchToShadowTable(obj);
                }
                if (StressTestingConstant.SHADOW_MODE_DATABASE_VALUE.equals(shadowMode)) {
                    switchToShadowDataBase(obj);
                }
            } catch (Throwable ex) {
                LOGGER.error("enhance sql cause an error:  " + ex.getMessage());
            }
        }
    }

    @Override
    public void afterMethod(Object obj, Class<?> clazz, Method method, Object[] allArguments, Class<?>[] argumentsTypes) throws Throwable {

    }

    private static void switchToShadowTable(Object obj) throws NoSuchFieldException, IllegalAccessException {
        if (invokerSet.add(obj)) {
            // 影子表
            BoundSql boundSql = (BoundSql) obj;
            String originalSql = boundSql.getSql();
            LOGGER.info("SwitchToShadowTable, Original SQL: " + originalSql);

            // 通过反射获取 A 类的 sql 字段
            Field sqlField = obj.getClass().getDeclaredField("sql");
            sqlField.setAccessible(true);

            // 修改字段值
            String modifiedSql = replaceSqlTables(originalSql);
            sqlField.set(obj, modifiedSql);
            LOGGER.info("SwitchToShadowTable, Modified SQL: " + modifiedSql);
        }
    }

    private static void switchToShadowDataBase(Object obj) throws NoSuchFieldException, IllegalAccessException {
        if (invokerSet.add(obj)) {
            // 影子库
            BoundSql boundSql = (BoundSql) obj;
            String originalSql = boundSql.getSql();
            LOGGER.info("SwitchToShadowDataBase, Original SQL: " + originalSql);

            // 通过反射获取 A 类的 sql 字段
            Field sqlField = obj.getClass().getDeclaredField("sql");
            sqlField.setAccessible(true);

            // 修改字段值
            Object dataBaseName = ContextManager.getProperty(StressTestingConstant.DATABASE_NAME_KEY);
            String newDataBaseName = "p_" + dataBaseName;
            String modifiedSql = "use " + newDataBaseName + ";" + originalSql;
            sqlField.set(obj, modifiedSql);
            LOGGER.info("SwitchToShadowDataBase, Modified SQL: " + modifiedSql);
        }
    }

    private static String replaceSqlTables(String sql) {
        Statement statement = null;
        try {
            statement = CCJSqlParserUtil.parse(sql);
        } catch (JSQLParserException e) {
            LOGGER.error("parse sql error: " + e.getMessage());
        }
        if (statement == null) {
            return sql;
        }
        List<String> tableList = new TablesNamesFinder().getTableList(statement);
        for (String tableName : tableList) {
            sql = sql.replace(tableName, tableName + "_");
        }
        return sql;
    }
}
