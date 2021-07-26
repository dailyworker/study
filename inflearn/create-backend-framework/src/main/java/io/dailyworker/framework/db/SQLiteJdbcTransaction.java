package io.dailyworker.framework.db;

import org.sqlite.SQLiteConfig;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import static org.sqlite.JDBC.createConnection;

public class SQLiteJdbcTransaction extends Transaction {

    public static final String JDBC_SQLITE_DB_FRAMEWORK_DB = "jdbc:sqlite:framework.db";

    @Override
    protected Connection generateConnection() throws SQLException {
        SQLiteConfig sqLiteConfig = new SQLiteConfig();
        Properties properties = sqLiteConfig.toProperties();
        return createConnection(JDBC_SQLITE_DB_FRAMEWORK_DB, properties);
    }
}
