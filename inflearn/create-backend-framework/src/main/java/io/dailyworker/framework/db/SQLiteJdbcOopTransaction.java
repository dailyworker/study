package io.dailyworker.framework.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteJdbcOopTransaction extends Transaction {
    @Override
    protected Connection generateConnection() throws SQLException {
        try {
            String className = "org.sqlite.JDBC";
            Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        String url = "jdbc:sqlite:~/study-workspace/db/framework.db";
        return DriverManager.getConnection(url);
    }
}
