package com.me.code.customize.generator.utils;

import com.me.code.customize.generator.data.ColumnInfo;
import com.me.code.customize.generator.data.ConfigInfo;
import com.me.code.customize.generator.data.DataTypeTranslator;
import com.me.code.customize.generator.data.TableInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhull
 * @date 2018/6/19
 * <P>Mapper生成器</P>
 */
public class MapperGenerator {

    private static String RESULT_MAP = "<$result column=\"$column\" property=\"$property\" jdbcType=\"$jdbcType\"/>";
    private static String INSERT = "<insert id=\"$id\" parameterType=\"$parameterType\" useGeneratedKeys=\"true\" keyProperty=\"$key\">";
    private static String SELECT = "<select id=\"$id\" parameterType=\"$parameterType\" resultMap=\"$resultMap\">";
    private static String SELECT_COUNT = "<select id=\"$id\" parameterType=\"$parameterType\" resultType=\"java.lang.Integer\">";
    private static String BASE_COLUMNS = "Base_Columns";
    private static String COLUMNS_FOR_INSERT = "Columns_For_Insert";
    private static String COLUMN = "#{$property, jdbcType=$jdbcType}";

    /**
     * 主键
     */
    private static ColumnInfo priColumn = null;
    /**
     * 其他列
     */
    private static List<ColumnInfo> ordinaryColumns;
    /**
     * 一行最大长度
     */
    private static int LINE_LENGTH = 120;

    /**
     * 入口
     *
     * @param configInfo 配置信息
     * @param tableInfo  表信息
     */
    public static List<String> generate(ConfigInfo configInfo, TableInfo tableInfo) {
        List<String> content = new ArrayList<>();
        addHead(content);
        divideColumns(tableInfo);
        addMapper(configInfo, tableInfo, content);
        return content;
    }

    /**
     * 添加mapper
     *
     * @param configInfo
     * @param tableInfo
     * @param content
     */
    private static void addMapper(ConfigInfo configInfo, TableInfo tableInfo, List<String> content) {
        String parameterType = configInfo.getDomainPackagePath() + "." + tableInfo.getClassName() + configInfo.getDomainPostfix();
        addNamespace(configInfo, tableInfo.getClassName(), content);
        addResultMap(configInfo, tableInfo, content);
        addColumn(content);
        addInsert(parameterType, tableInfo.getTableName(), content);
        addBatchInsert(tableInfo.getTableName(), content);
        if (priColumn != null) {
            addDeleteByPrimaryKey(tableInfo.getTableName(), content);
            addUpdateByPrimaryKey(parameterType, tableInfo.getTableName(), content);
            addQueryByPrimaryKey(tableInfo, content);
        }
        addQueryList(parameterType, tableInfo, content);
        addCount(parameterType, tableInfo, content);
        content.add("</mapper>");
    }

    /**
     * 添加统计数量方法
     *
     * @param parameterType
     * @param tableInfo
     * @param content
     */
    private static void addCount(String parameterType, TableInfo tableInfo, List<String> content) {
        content.add("");
        content.add(CommonUtil.SPACE4 + "<!-- 统计数量 -->");
        content.add(CommonUtil.SPACE4 + SELECT_COUNT.replace("$id", "count")
                .replace("$parameterType", parameterType));
        content.add(CommonUtil.getNTab(2) + "select count(*)");
        selectFromWhere(tableInfo, content);
    }

    /**
     * 添加根据条件查询列表方法
     *
     * @param parameterType
     * @param tableInfo
     * @param content
     */
    private static void addQueryList(String parameterType, TableInfo tableInfo, List<String> content) {
        content.add("");
        content.add(CommonUtil.SPACE4 + "<!-- 根据主键查询 -->");
        content.add(CommonUtil.SPACE4 +
                SELECT.replace("$id", "queryList")
                        .replace("$parameterType", parameterType)
                        .replace("$resultMap", tableInfo.getObjectName()));
        content.add(CommonUtil.getNTab(2) + "select <include refid=\"" + BASE_COLUMNS + "\"/>");
        selectFromWhere(tableInfo, content);
    }

    /**
     * from ... where ...
     *
     * @param tableInfo
     * @param content
     */
    private static void selectFromWhere(TableInfo tableInfo, List<String> content) {
        content.add(CommonUtil.getNTab(2) + "from " + tableInfo.getTableName());
        content.add(CommonUtil.getNTab(2) + "<where>");
        if4SelectOrUpdate(tableInfo.getColumnInfos(), content, 0);
        content.add(CommonUtil.getNTab(2) + "</where>");
        content.add(CommonUtil.SPACE4 + "</select>");
    }

    /**
     * 添加if-test语句
     * 0-select, 1-update
     *
     * @param columnInfos
     * @param content
     * @param flag
     */
    private static void if4SelectOrUpdate(List<ColumnInfo> columnInfos, List<String> content, int flag) {
        for (ColumnInfo columnInfo : columnInfos) {
            String property = columnInfo.getProperty();
            if ("String".equals(columnInfo.getType().java)) {
                content.add(CommonUtil.getNTab(3) + "<if test=\" " + property + " != null and " + property + " != '' \" >");
            } else {
                content.add(CommonUtil.getNTab(3) + "<if test=\" " + property + " != null \" >");
            }
            StringBuilder sb0 = new StringBuilder(CommonUtil.getNTab(4));
            StringBuilder columnEqualsProperty = new StringBuilder().append("`").append(columnInfo.getColumnName()).append("` = ")
                    .append(COLUMN.replace("$property", property).replace("$jdbcType", columnInfo.getType().mybatis));
            if (flag == 0) {
                // select
                sb0.append("AND ").append(columnEqualsProperty);
            } else if (flag == 1) {
                // update
                sb0.append(columnEqualsProperty).append(",");
            } else {
                throw new RuntimeException("传值错误: flag = " + flag);
            }
            content.add(sb0.toString());
            content.add(CommonUtil.getNTab(3) + "</if>");
        }
    }

    /**
     * 添加根据主键查询方法
     *
     * @param tableInfo
     * @param content
     */
    private static void addQueryByPrimaryKey(TableInfo tableInfo, List<String> content) {
        content.add("");
        content.add(CommonUtil.SPACE4 + "<!-- 根据主键查询 -->");
        content.add(CommonUtil.SPACE4 +
                SELECT.replace("$id", "queryByPrimaryKey")
                        .replace("$parameterType", "java.lang." + priColumn.getType().java)
                        .replace("$resultMap", tableInfo.getObjectName()));
        content.add(CommonUtil.getNTab(2) + "select <include refid=\"" + BASE_COLUMNS + "\"/>");
        content.add(CommonUtil.getNTab(2) + "from " + tableInfo.getTableName());
        content.add(CommonUtil.getNTab(2) + "where `" + priColumn.getColumnName() + "` = " +
                COLUMN.replace("$property", priColumn.getProperty())
                        .replace("$jdbcType", priColumn.getType().mybatis));
        content.add(CommonUtil.SPACE4 + "</select>");
    }

    /**
     * 添加根据主键更新非空字段方法
     *
     * @param parameterType
     * @param tableName
     * @param content
     */
    private static void addUpdateByPrimaryKey(String parameterType, String tableName, List<String> content) {
        content.add("");
        content.add(CommonUtil.SPACE4 + "<!-- 根据主键更新非空字段 -->");
        content.add(CommonUtil.SPACE4 + "<update id=\"updateByPrimaryKey\" parameterType=\"" + parameterType + "\">");
        content.add(CommonUtil.getNTab(2) + "update " + tableName);
        content.add(CommonUtil.getNTab(2) + "<set>");
        if4SelectOrUpdate(ordinaryColumns, content, 1);
        content.add(CommonUtil.getNTab(2) + "</set>");
        content.add(CommonUtil.getNTab(2) + "where `" + priColumn.getColumnName() + "` = " +
                COLUMN.replace("$property", priColumn.getProperty())
                        .replace("$jdbcType", priColumn.getType().mybatis));
        content.add(CommonUtil.SPACE4 + "</update>");
    }

    /**
     * 添加删除方法
     *
     * @param tableName
     * @param content
     */
    private static void addDeleteByPrimaryKey(String tableName, List<String> content) {
        content.add("");
        content.add(CommonUtil.SPACE4 + "<!-- 根据主键删除 -->");
        content.add(CommonUtil.SPACE4 + "<delete id=\"deleteByPrimaryKey\" parameterType=\"java.lang." +
                priColumn.getType().java + "\">");
        content.add(CommonUtil.getNTab(2) + "delete from " + tableName);
        content.add(CommonUtil.getNTab(2) + "where `" + priColumn.getColumnName() + "` = " +
                COLUMN.replace("$property", priColumn.getProperty())
                        .replace("$jdbcType", priColumn.getType().mybatis));
        content.add(CommonUtil.SPACE4 + "</delete>");
    }

    /**
     * 添加批量插入方法
     *
     * @param tableName
     * @param content
     */
    private static void addBatchInsert(String tableName, List<String> content) {
        content.add("");
        content.add(CommonUtil.SPACE4 + "<!-- 批量插入 -->");
        content.add(CommonUtil.SPACE4 + INSERT.replace("$id", "batchInsert")
                .replace("$parameterType", "java.util.List")
                .replace("$key", priColumn.getProperty()));
        content.add(CommonUtil.getNTab(2) + "insert into " + tableName);
        content.add(CommonUtil.getNTab(2) + "(" + CommonUtil.SPACE4 + "<include refid=\"" + COLUMNS_FOR_INSERT + "\"/>" + CommonUtil.SPACE4 + ")");
        content.add(CommonUtil.getNTab(2) + "values");
        content.add(CommonUtil.getNTab(2) + "<foreach collection=\"list\" item=\"item\" separator=\",\" >");
        content.add(CommonUtil.getNTab(3) + "(");
        listProperty(content, 4);
        content.add(CommonUtil.getNTab(3) + ")");
        content.add(CommonUtil.getNTab(2) + "</foreach>");
        content.add(CommonUtil.SPACE4 + "</insert>");
    }

    /**
     * 生成 #{property, jdbcType=jdbcType}
     *
     * @param content
     * @param tabNum
     */
    private static void listProperty(List<String> content, int tabNum) {
        String item = tabNum == 3 ? "" : "item.";
        StringBuilder sb = new StringBuilder(CommonUtil.getNTab(tabNum));
        int lastIndex = ordinaryColumns.size() - 1;
        for (int i = 0; i < lastIndex; ++i) {
            String temp = COLUMN.replace("$property", item + ordinaryColumns.get(i).getProperty())
                    .replace("$jdbcType", ordinaryColumns.get(i).getType().mybatis) + ", ";
            sb = getNewLine(content, tabNum, sb, temp);
            sb.append(temp);
        }
        if (ordinaryColumns.size() > 0) {
            String temp = COLUMN.replace("$property",
                    item + ordinaryColumns.get(lastIndex).getProperty())
                    .replace("$jdbcType", ordinaryColumns.get(lastIndex).getType().mybatis);
            sb = getNewLine(content, tabNum, sb, temp);
            sb.append(temp);
        }
        content.add(sb.toString());
    }

    /**
     * 如超过一行长度则换行
     *
     * @param content
     * @param tabNum
     * @param sb
     * @param temp
     * @return
     */
    private static StringBuilder getNewLine(List<String> content, int tabNum, StringBuilder sb, String temp) {
        if (sb.length() + temp.length() > LINE_LENGTH) {
            content.add(sb.toString());
            sb = new StringBuilder(CommonUtil.getNTab(tabNum));
        }
        return sb;
    }

    /**
     * 添加插入方法
     *
     * @param parameterType
     * @param tableName
     * @param content
     */
    private static void addInsert(String parameterType, String tableName, List<String> content) {
        content.add("");
        content.add(CommonUtil.SPACE4 + "<!-- 插入数据 -->");
        content.add(CommonUtil.SPACE4 + INSERT.replace("$id", "insert")
                .replace("$parameterType", parameterType)
                .replace("$key", priColumn.getProperty()));
        content.add(CommonUtil.getNTab(2) + "insert into " + tableName);
        content.add(CommonUtil.getNTab(2) + "(" + CommonUtil.SPACE4 + "<include refid=\"" + COLUMNS_FOR_INSERT + "\"/>" + CommonUtil.SPACE4 + ")");
        content.add(CommonUtil.getNTab(2) + "values(");
        listProperty(content, 3);
        content.add(CommonUtil.getNTab(2) + ")");
        content.add(CommonUtil.SPACE4 + "</insert>");
    }

    /**
     * 添加列集合
     *
     * @param content
     */
    private static void addColumn(List<String> content) {
        content.add("");
        content.add(CommonUtil.SPACE4 + "<!-- 基础字段 -->");
        content.add(CommonUtil.SPACE4 + "<sql id=\"" + BASE_COLUMNS + "\">");
        content.add(CommonUtil.getNTab(2) + "`" + priColumn.getColumnName() + "`,");
        content.add(CommonUtil.getNTab(2) + "<include refid=\"" + COLUMNS_FOR_INSERT + "\"/>");
        content.add(CommonUtil.SPACE4 + "</sql>");

        content.add("");
        content.add(CommonUtil.SPACE4 + "<!-- Insert使用字段 -->");
        content.add(CommonUtil.SPACE4 + "<sql id=\"" + COLUMNS_FOR_INSERT + "\">");
        StringBuilder sb = new StringBuilder(CommonUtil.getNTab(2));
        for (int i = 0; i < ordinaryColumns.size() - 1; ++i) {
            String temp = "`" + ordinaryColumns.get(i).getColumnName() + "`, ";
            sb = getNewLine(content, 2, sb, temp);
            sb.append(temp);
        }
        if (ordinaryColumns.size() > 0) {
            String temp = "`" + ordinaryColumns.get(ordinaryColumns.size() - 1).getColumnName() + "`";
            sb = getNewLine(content, 2, sb, temp);
            sb.append(temp);
        }
        content.add(sb.toString());
        content.add(CommonUtil.SPACE4 + "</sql>");
    }

    /**
     * 获取主键、普通列
     *
     * @param tableInfo
     * @return
     */
    private static void divideColumns(TableInfo tableInfo) {
        priColumn = null;
        ordinaryColumns = new ArrayList<>();
        for (ColumnInfo columnInfo : tableInfo.getColumnInfos()) {
            if (CommonUtil.PRI.equals(columnInfo.getKey())) {
                priColumn = columnInfo;
            } else {
                ordinaryColumns.add(columnInfo);
            }
        }
    }

    /**
     * 添加结果映射
     *
     * @param configInfo
     * @param tableInfo
     * @param content
     */
    private static void addResultMap(ConfigInfo configInfo, TableInfo tableInfo, List<String> content) {
        content.add(CommonUtil.SPACE4 + "<resultMap id=\"" + tableInfo.getObjectName() + "\" type=\"" +
                configInfo.getDomainPackagePath() + "." + tableInfo.getClassName() + configInfo.getDomainPostfix() + "\">");
        if (priColumn != null) {
            content.add(CommonUtil.getNTab(2) + RESULT_MAP.replace("$result", "id")
                    .replace("$column", priColumn.getColumnName())
                    .replace("$property", priColumn.getProperty())
                    .replace("$jdbcType", DataTypeTranslator.DataType.instanceOf(priColumn.getDataType()).mybatis));
        }
        for (ColumnInfo columnInfo : ordinaryColumns) {
            content.add(CommonUtil.getNTab(2) + RESULT_MAP.replace("$result", "result")
                    .replace("$column", columnInfo.getColumnName())
                    .replace("$property", columnInfo.getProperty())
                    .replace("$jdbcType", DataTypeTranslator.DataType.instanceOf(columnInfo.getDataType()).mybatis));
        }
        content.add(CommonUtil.SPACE4 + "</resultMap>");
    }

    /**
     * 添加作用域
     *
     * @param configInfo
     * @param className  表对应的类名
     * @param content
     */
    private static void addNamespace(ConfigInfo configInfo, String className, List<String> content) {
        content.add("<mapper namespace=\"" + configInfo.getModulePackagePath() + "." + className + configInfo.getDaoPostfix() + "\">");
    }

    /**
     * 添加头部信息
     *
     * @param content
     */
    private static void addHead(List<String> content) {
        content.add("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
        content.add("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\" >");
    }

}
