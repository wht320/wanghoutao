package com.wanghoutao.oracle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 从本机连接 Docker 里的 Oracle Database Free。
 * 默认：jdbc:oracle:thin:@localhost:1521/FREEPDB1 ，用户 system。
 */
public final class OracleConnectDemo {

    public static final String DEFAULT_URL = "jdbc:oracle:thin:@localhost:1521/FREEPDB1";
    public static final String DEFAULT_USER = "system";
    public static final String DEFAULT_PASSWORD = "Oracle123";

    private OracleConnectDemo() {
    }

    public static void main(String[] args) throws Exception {
        String url = env("ORACLE_URL", DEFAULT_URL);
        String user = env("ORACLE_USER", DEFAULT_USER);
        String password = env("ORACLE_PASSWORD", DEFAULT_PASSWORD);

        System.out.printf("正在连接 %s ，用户 %s%n", url, user);
        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT USER AS username, SYSDATE AS db_time FROM DUAL")) {
            if (resultSet.next()) {
                System.out.printf("连接成功：用户=%s 数据库时间=%s%n",
                        resultSet.getString("username"),
                        resultSet.getTimestamp("db_time"));
            }
        }
    }

    static String env(String name, String defaultValue) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return value;
    }
}
