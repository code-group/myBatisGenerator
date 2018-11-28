package com.me.code.customize.generator.data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhull
 * @date 2018/6/13
 * <P>MySQL -> Java, Mybatis 类型转换</P>
 */
public class DataTypeTranslator {

    private static String DATE_PACKAGE = "java.util.Date";
    private static String LOCAL_DATE_PACKAGE = "java.time.LocalDate";
    private static String LOCAL_DATE_TIME_PACKAGE = "java.time.LocalDateTime";
    private static String BIG_DECIMAL_PACKAGE = "java.math.BigDecimal";
    private static String BLOB_PACKAGE = "java.sql.Blob";
    private static String CLOB_PACKAGE = "java.sql.Clob";

    public enum Java2Mysql {
        //        DATE(new ArrayList<DataType>() {{
//                add(DataType.DATE);
//                add(DataType.DATETIME);
//                add(DataType.TIMESTAMP);
//            }}, DATE_PACKAGE),
        LOCAL_DATE(new ArrayList<DataType>() {{
            add(DataType.DATE_4LD);
        }}, LOCAL_DATE_PACKAGE),
        LOCAL_DATE_TIME(new ArrayList<DataType>() {{
            add(DataType.DATETIME_4LD);
        }}, LOCAL_DATE_TIME_PACKAGE),
        BIG_DECIMAL(new ArrayList<DataType>() {{
            add(DataType.NUMERIC);
            add(DataType.DECIMAL);
        }}, BIG_DECIMAL_PACKAGE),
        BLOB(new ArrayList<DataType>() {{
            add(DataType.BLOB);
        }}, BLOB_PACKAGE),
        CLOB(new ArrayList<DataType>() {{
            add(DataType.CLOB);
        }}, CLOB_PACKAGE),
        ;

        public List<DataType> dataTypes;
        public String packageName;

        Java2Mysql(ArrayList<DataType> dataTypes, String packageName) {
            this.dataTypes = dataTypes;
            this.packageName = packageName;
        }

        public static Java2Mysql instanceOf(DataType dataType) {
            if (dataType == null) {
                return null;
            }
            for (Java2Mysql e : Java2Mysql.values()) {
                if (e.dataTypes.contains(dataType)) {
                    return e;
                }
            }
            return null;
        }
    }
    public enum DataType {
        BIGINT("BIGINT", "BIGINT", "Long", "java.lang.Long", false),
        CHAR("CHAR", "CHAR", "String", "java.lang.String", false),
        TEXT("TEXT", "CLOB", "String", "java.lang.String", false),
        JSON("JSON", "String", "String", "java.lang.String", false),
        DOUBLE("DOUBLE", "DOUBLE", "Double", "java.lang.Double", false),
        FLOAT("FLOAT", "FLOAT", "Double", "java.lang.Double", false),
        INTEGER("INTEGER", "INTEGER", "Integer", "java.lang.Integer", false),
        INT("INT", "INTEGER", "Integer", "java.lang.Integer", false),
        TINYINT("TINYINT", "TINYINT", "Byte", "java.lang.Byte", false),
        VARCHAR("VARCHAR", "VARCHAR", "String", "java.lang.String", false),
        REAL("REAL", "REAL", "FLOAT", "java.lang.Float", false),
        SMALLINT("SMALLINT", "SMALLINT", "Short", "java.lang.Short", false),
        LONGBLOB("LONGBLOB", "BLOB", "Byte[]", "java.lang.Byte", false),
        BLOB("BLOB", "BLOB", "Blob", BLOB_PACKAGE, true),
        CLOB("CLOB", "CLOB", "Clob", CLOB_PACKAGE, true),
        NUMERIC("NUMERIC", "NUMERIC", "BigDecimal", BIG_DECIMAL_PACKAGE, true),
        DECIMAL("DECIMAL", "DECIMAL", "BigDecimal", BIG_DECIMAL_PACKAGE, true),

//        DATE("DATE", "DATE", "Date", DATE_PACKAGE, true),
//        DATETIME("DATETIME", "TIMESTAMP", "Date", DATE_PACKAGE, true),
//        TIMESTAMP("TIMESTAMP", "TIMESTAMP", "Date", DATE_PACKAGE, true),

        DATE_4LD("DATE", "DATE", "LocalDate", LOCAL_DATE_PACKAGE, true),
        DATETIME_4LD("DATETIME", "TIMESTAMP", "LocalDateTime", LOCAL_DATE_TIME_PACKAGE, true),
        TIMESTAMP_4LD("TIMESTAMP", "TIMESTAMP", "LocalDateTime", LOCAL_DATE_TIME_PACKAGE, true),
        ;

        public String mysql;
        public String mybatis;
        public String java;
        public String packageNeed;
        public Boolean needImport;

        DataType(String mysql, String mybatis, String java, String packageNeed, Boolean needImport) {
            this.mysql = mysql;
            this.mybatis = mybatis;
            this.java = java;
            this.packageNeed = packageNeed;
            this.needImport = needImport;
        }

        public static DataType instanceOf(String mysql) {
            if (mysql == null || mysql == "") {
                return null;
            }
            for (DataType e : DataType.values()) {
                if (e.mysql.equals(mysql)) {
                    return e;
                }
            }
            return null;
        }
    }
}
