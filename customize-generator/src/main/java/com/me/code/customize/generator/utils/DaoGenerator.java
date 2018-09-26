package com.me.code.customize.generator.utils;

import com.me.code.customize.generator.data.ColumnInfo;
import com.me.code.customize.generator.data.ConfigInfo;
import com.me.code.customize.generator.data.TableInfo;
import org.apache.commons.lang3.time.FastDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author zhull
 * @date 2018/6/13
 * <P>Dao生成器</P>
 */
public class DaoGenerator extends BaseGenerator {

    /**
     * 入口
     * @param configInfo 配置信息
     * @param tableInfo  表信息
     */
    public static List<String> generate(ConfigInfo configInfo, TableInfo tableInfo) {
        List<String> content = new ArrayList<>();
        addPackage(configInfo.getModulePackagePath(), content);
        addImport(configInfo.getDomainPackagePath(), tableInfo.getClassName() + configInfo.getDomainPostfix(), content);
        addClassComment(tableInfo.getComment(), content);
        addClass(configInfo, tableInfo, content);
        return content;
    }

    /**
     * 添加类内容
     * @param tableInfo
     * @param content
     */
    private static void addClass(ConfigInfo configInfo, TableInfo tableInfo, List<String> content) {
        ColumnInfo priColumn = null;
        for (ColumnInfo columnInfo : tableInfo.getColumnInfos()) {
            if (CommonUtil.PRI.equals(columnInfo.getKey())) {
                priColumn = columnInfo;
                break;
            }
        }
        String className = tableInfo.getClassName() + configInfo.getDomainPostfix();
        content.add("public interface " + tableInfo.getClassName() +  configInfo.getDaoPostfix() + " {");
        addInsert(className, content);
        addBatchInsert(className, content);
        if (priColumn != null) {
            addDeleteByPrimaryKey(priColumn, content);
            addUpdateByPrimaryKey(className, content);
            addQueryByPrimaryKey(priColumn, className, content);
        }
        addQueryOne(className, content);
        addQueryList(className, content);
        addCount(className, content);

        content.add("}");
    }

    /**
     * 添加addUpdateByPrimaryKey方法
     *
     * @param domainClassName
     * @param content
     */
    private static void addUpdateByPrimaryKey(final String domainClassName, List<String> content) {
        content.add("");
        content.add(CommonUtil.SPACE4 + "/**");
        content.add(CommonUtil.SPACE4 + " * 根据主键更新");
        content.add(CommonUtil.SPACE4 + " * ");
        content.add(CommonUtil.SPACE4 + " * @param condition 要更新的对象");
        content.add(CommonUtil.SPACE4 + " * @return 影响条数");
        content.add(CommonUtil.SPACE4 + " */");
        content.add(CommonUtil.SPACE4 + "int updateByPrimaryKey(" + domainClassName + " condition);");
    }

    /**
     * 添加deleteByPrimaryKey方法
     * @param priColumn
     * @param content
     */
    private static void addDeleteByPrimaryKey(ColumnInfo priColumn, List<String> content) {
        content.add("");
        content.add(CommonUtil.SPACE4 + "/**");
        content.add(CommonUtil.SPACE4 + " * 根据主键删除");
        content.add(CommonUtil.SPACE4 + " * ");
        content.add(CommonUtil.SPACE4 + " * @param " + priColumn.getProperty() + " 主键值");
        content.add(CommonUtil.SPACE4 + " * @return 影响条数");
        content.add(CommonUtil.SPACE4 + " */");
        content.add(CommonUtil.SPACE4 + "int deleteByPrimaryKey(" + priColumn.getType().java + " " +
                priColumn.getProperty() + ");");
    }

    /**
     * 添加类注释
     * @param comment 表注释
     * @param content output
     */
    private static void addClassComment(String comment, List<String> content) {
        content.add("");
        content.add("/**");
        content.add(" * " + comment + "数据访问");
        content.add(" * ");
        content.add(" * @author " + AUTHOR);
        content.add(" * @date " + FastDateFormat.getInstance("yyyy/MM/dd").format(new Date()));
        content.add(" */ ");
    }

    /**
     * 添加import语句
     *
     * @param domainPackagePath domain包路径
     * @param domainName        类名
     * @param content           output
     */
    private static void addImport(String domainPackagePath, String domainName, List<String> content) {
        content.add("");
        content.add(CommonUtil.IMPORT.replace("$modulePackagePath", domainPackagePath + "." + domainName));
        content.add(CommonUtil.IMPORT.replace("$modulePackagePath", "java.util.List"));
    }

    /**
     * 增加根据主键查询方法.
     *
     * @param primaryColumn   主键列信息
     * @param domainClassName domain类名
     * @param content         输出文件内容
     */
    private static void addQueryByPrimaryKey(final ColumnInfo primaryColumn, final String domainClassName, final List<String> content) {
        content.add("");
        content.add(CommonUtil.SPACE4 + "/**");
        content.add(CommonUtil.SPACE4 + " * 根据主键查询");
        content.add(CommonUtil.SPACE4 + " * ");
        content.add(CommonUtil.SPACE4 + " * @param " + primaryColumn.getProperty() + " 主键值");
        content.add(CommonUtil.SPACE4 + " * @return 根据主键查询到的对象");
        content.add(CommonUtil.SPACE4 + " */");
        content.add(CommonUtil.SPACE4 + domainClassName + " queryByPrimaryKey(" + primaryColumn.getType().java + " " +
                primaryColumn.getProperty() + ");");
    }

    /**
     * 增加query方法，根据查询条件查询一条记录.
     *
     * @param domainClassName domain类名
     * @param content         输出文件内容
     */
    private static void addQueryOne(final String domainClassName, final List<String> content) {
        content.add("");
        content.add(CommonUtil.SPACE4 + "/**");
        content.add(CommonUtil.SPACE4 + " * 根据条件查询一条记录");
        content.add(CommonUtil.SPACE4 + " * ");
        content.add(CommonUtil.SPACE4 + " * @param condition 查询条件");
        content.add(CommonUtil.SPACE4 + " * @return 查询出来的对象");
        content.add(CommonUtil.SPACE4 + " */");
        content.add(CommonUtil.SPACE4 + domainClassName + " queryOne(" + domainClassName + " condition);");
    }

    /**
     * 增加query方法，根据查询条件查询列表.
     *
     * @param domainClassName domain类名
     * @param content         输出文件内容
     */
    private static void addQueryList(final String domainClassName, final List<String> content) {
        content.add("");
        content.add(CommonUtil.SPACE4 + "/**");
        content.add(CommonUtil.SPACE4 + " * 根据条件查询列表");
        content.add(CommonUtil.SPACE4 + " * ");
        content.add(CommonUtil.SPACE4 + " * @param condition 查询条件");
        content.add(CommonUtil.SPACE4 + " * @return 查询出来的对象列表");
        content.add(CommonUtil.SPACE4 + " */");
        content.add(CommonUtil.SPACE4 + "List<" + domainClassName + ">" + " queryList(" + domainClassName + " condition);");
    }

    /**
     * 增加count方法，统计数量.
     *
     * @param domainClassName domain类名
     * @param content         输出文件内容
     */
    private static void addCount(final String domainClassName, final List<String> content) {
        content.add("");
        content.add(CommonUtil.SPACE4 + "/**");
        content.add(CommonUtil.SPACE4 + " * 根据条件统计数量");
        content.add(CommonUtil.SPACE4 + " * ");
        content.add(CommonUtil.SPACE4 + " * @param condition 统计条件");
        content.add(CommonUtil.SPACE4 + " * @return 记录总数");
        content.add(CommonUtil.SPACE4 + " */");
        content.add(CommonUtil.SPACE4 + "int count(" + domainClassName + " condition);");
    }

    /**
     * 增加insert方法
     *
     * @param domainClassName domain类名
     * @param content         输出文件内容
     */
    private static void addInsert(final String domainClassName, final List<String> content) {
        content.add("");
        content.add(CommonUtil.SPACE4 + "/**");
        content.add(CommonUtil.SPACE4 + " * 插入一条数据");
        content.add(CommonUtil.SPACE4 + " * ");
        content.add(CommonUtil.SPACE4 + " * @param " + CommonUtil.firstCharToLower(domainClassName) + " 待插入对象");
        content.add(CommonUtil.SPACE4 + " */");
        content.add(CommonUtil.SPACE4 + "void insert(" + domainClassName + " " + CommonUtil.firstCharToLower(domainClassName) + ");");
    }

    /**
     * 增加batchInsert方法，批量插入数据.
     *
     * @param domainClassName domain类名
     * @param content         输出文件内容
     */
    private static void addBatchInsert(final String domainClassName, final List<String> content) {
        content.add("");
        content.add(CommonUtil.SPACE4 + "/**");
        content.add(CommonUtil.SPACE4 + " * 批量插入多条数据");
        content.add(CommonUtil.SPACE4 + " * ");
        content.add(CommonUtil.SPACE4 + " * @param list 待插入对象列表");
        content.add(CommonUtil.SPACE4 + " */");
        content.add(CommonUtil.SPACE4 + "void batchInsert(List<" + domainClassName + "> list);");
    }
}
