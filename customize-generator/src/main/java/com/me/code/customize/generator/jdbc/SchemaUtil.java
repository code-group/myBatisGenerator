package com.me.code.customize.generator.jdbc;

import com.me.code.customize.generator.data.ColumnInfo;
import com.me.code.customize.generator.data.TableInfo;
import org.apache.commons.lang3.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhull
 * @date 2018/6/12
 * <P>Schema操作</P>
 */
public class SchemaUtil {

    /**
     * 获取db的表属性
     */
    private static String TABLE_TEMPLATE = "select TABLE_NAME, TABLE_COMMENT from information_schema.`TABLES` t where t.TABLE_SCHEMA = '$tableSchema'";
    private static String SOME_TABLE_TEMPLATE = TABLE_TEMPLATE + " and t.TABLE_NAME in ($tableName)";

    /**
     * 获取表的列属性
     * COLUMN_KEY 列的键属性
     */
    private static String COLUMN_TEMPLATE = "select t.COLUMN_NAME, upper(t.DATA_TYPE), t.COLUMN_COMMENT, upper(t.COLUMN_KEY) FROM information_schema.COLUMNS t where t.TABLE_SCHEMA = '$tableSchema' and t.TABLE_NAME = '$tableName' ORDER BY t.ORDINAL_POSITION";

    /**
     * 如tableNames为空，则查询tableSchema所有表
     *
     * @param tableSchema
     * @param tableNames  表名列表
     * @return
     */
    public static List<TableInfo> getTableInfos(String tableSchema, List<String> tableNames) throws SQLException {
        if (StringUtils.isEmpty(tableSchema)) {
            return null;
        }
        List<TableInfo> tableInfos = new ArrayList<>();
        if (tableNames == null || tableNames.size() == 0) {
            getTableInfos(tableInfos,
                    DbUtil.executeQuery(TABLE_TEMPLATE.replace("$tableSchema", tableSchema)));
        } else {
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < tableNames.size()-1; ++i) {
                sb.append("'").append(tableNames.get(i)).append("'").append(",");
            }
            sb.append("'").append(tableNames.get(tableNames.size()-1)).append("'");
            getTableInfos(tableInfos, DbUtil.executeQuery(
                    SOME_TABLE_TEMPLATE.replace("$tableSchema", tableSchema).replace("$tableName", sb)));
        }
        System.out.print("即将处理" + tableInfos.size() + "张表\t");
        for (TableInfo tableInfo : tableInfos) {
            System.out.print(tableInfo.getTableName() + "; ");
            ResultSet resultSet = DbUtil.executeQuery(
                    COLUMN_TEMPLATE.replace("$tableSchema", tableSchema).replace("$tableName", tableInfo.getTableName()));
            if (resultSet == null) {
                continue;
            }
            List<ColumnInfo> columnInfos = new ArrayList<>();
            while (resultSet.next()) {
                ColumnInfo columnInfo = new ColumnInfo();
                columnInfo.setColumnName(resultSet.getString(1));
                columnInfo.setDataType(resultSet.getString(2));
                columnInfo.setComment(resultSet.getString(3));
                columnInfo.setKey(resultSet.getString(4));
                columnInfo.setProperty();
                columnInfo.setType();
                columnInfos.add(columnInfo);
            }
            tableInfo.setColumnInfos(columnInfos);
        }
        System.out.println();
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        return tableInfos;
    }

    /**
     * 从结果集解析表信息
     *
     * @param tableInfos
     * @param tableResultSet
     * @throws SQLException
     */
    private static void getTableInfos(List<TableInfo> tableInfos, ResultSet tableResultSet) throws SQLException {
        if (tableResultSet == null) {
            return;
        }
        while (tableResultSet.next()) {
            TableInfo tableInfo = new TableInfo();
            tableInfo.setTableName(tableResultSet.getString(1).toUpperCase());
            tableInfo.setComment(tableResultSet.getString(2));
            tableInfo.setClassName();
            tableInfo.setObjectName();
            tableInfos.add(tableInfo);
        }
    }

}
