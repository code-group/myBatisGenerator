package com.me.code.customize.generator.jdbc;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author zhull
 * @date 2018/6/12
 * <P>数据库工具</P>
 */
public class DbUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(DbUtil.class);
    private static String DRIVER_NAME = "com.mysql.jdbc.Driver";

    private static Connection connection;

    public static void initConnection(String url, String user, String password) throws ClassNotFoundException, SQLException {
        // 加载驱动
        Class.forName(DRIVER_NAME);
        connection = DriverManager.getConnection(url, user, password);
    }

    public static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public static ResultSet executeQuery(String sql) throws SQLException {
        if (StringUtils.isEmpty(sql)) {
            return null;
        }
        if (connection == null || connection.isClosed()) {
            LOGGER.error("请先初始化数据库连接");
            throw new RuntimeException("未建立连接");
        }
        return connection.createStatement().executeQuery(sql);
    }
}
