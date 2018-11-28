package com.me.code.customize.generator.utils;

import com.me.code.customize.generator.data.ColumnInfo;
import com.me.code.customize.generator.data.ConfigInfo;
import com.me.code.customize.generator.data.DataTypeTranslator;
import com.me.code.customize.generator.data.TableInfo;
import org.apache.commons.lang3.time.FastDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author zhull
 * @date 2018/6/13
 * <P>生成Domain的内容</P>
 */
public class DomainGenerator extends BaseGenerator {

    /**
     * 入口
     * @param configInfo 文件配置
     * @param tableInfo
     */
    public static List<String> generate(ConfigInfo configInfo, TableInfo tableInfo) {
        List<String> content = new ArrayList<>();
        addPackage(configInfo.getDomainPackagePath(), content);
        addImport(tableInfo, content);
        addClassComment(tableInfo, content);
        addAnnotation(content);
        addClass(configInfo.getDomainPostfix(), tableInfo, content);
        return content;
    }

    /**
     * 添加类内容
     * @param tableInfo
     * @param content
     */
    private static void addClass(String postfix, TableInfo tableInfo, List<String> content) {
        content.add("public class " + tableInfo.getClassName() + postfix + " {");
        for (ColumnInfo columnInfo : tableInfo.getColumnInfos()) {
            content.add("");
            content.add(CommonUtil.SPACE4 + "/**");
            content.add(CommonUtil.SPACE4 + " * " + columnInfo.getComment());
            content.add(CommonUtil.SPACE4 + " */");
            content.add(CommonUtil.SPACE4 + "private " + columnInfo.getType().java + " " + columnInfo.getProperty() + ";");
        }
        content.add("}");
    }

    /**
     * 添加类注解
     * @param content
     */
    private static void addAnnotation(List<String> content) {
        content.add("@Data");
    }

    /**
     * 添加类注释
     * @param tableInfo raw materials
     * @param content   output
     */
    private static void addClassComment(TableInfo tableInfo, List<String> content) {
        content.add("");
        content.add("/**");
        content.add(" * " + tableInfo.getComment());
        content.add(" * ");
        content.add(" * @author " + AUTHOR);
        content.add(" * @date " + FastDateFormat.getInstance("yyyy/MM/dd").format(new Date()));
        content.add(" */");
    }

    /**
     * 添加import语句
     *
     * @param tableInfo raw materials
     * @param content   output
     */
    private static void addImport(TableInfo tableInfo, List<String> content) {
        content.add("");
        content.add("import lombok.Data;");
        boolean importBlob = false;
        boolean importClob = false;
        boolean importDate = false;
        boolean importLocalDate = false;
        boolean importLocalDateTime = false;
        boolean importBigDecimal = false;

        for (ColumnInfo columnInfo : tableInfo.getColumnInfos()) {
            DataTypeTranslator.Java2Mysql java2Mysql = DataTypeTranslator.Java2Mysql.instanceOf(columnInfo.getType());
            if (java2Mysql != null) {
                switch (java2Mysql) {
                    case BLOB:
                        importBlob = importPackage(content, importBlob, java2Mysql);
                        break;
                    case CLOB:
                        importClob = importPackage(content, importClob, java2Mysql);
                        break;
//                    case DATE:
//                        importDate = importPackage(content, importDate, java2Mysql);
//                        break;
                    case LOCAL_DATE:
                        importLocalDate = importPackage(content, importLocalDate, java2Mysql);
                        break;
                    case LOCAL_DATE_TIME:
                        importLocalDateTime = importPackage(content, importLocalDateTime, java2Mysql);
                        break;
                    case BIG_DECIMAL:
                        importBigDecimal = importPackage(content, importBigDecimal, java2Mysql);
                        break;
                    default:
                        System.err.println("未识别的数据类型: " + java2Mysql);
                }
            }
        }
    }

    private static boolean importPackage(List<String> content, boolean imported, DataTypeTranslator.Java2Mysql typeEnum) {
        if (!imported) {
            content.add(CommonUtil.IMPORT.replace("$modulePackagePath", typeEnum.packageName));
        }
        return true;
    }
}
