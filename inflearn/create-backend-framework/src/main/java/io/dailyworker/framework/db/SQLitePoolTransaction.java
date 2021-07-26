package io.dailyworker.framework.db;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class SQLitePoolTransaction extends Transaction {
    @Override
    protected Connection generateConnection() throws SQLException {
        try {
            InitialContext initialContext = new InitialContext();
            DataSource lookup = (DataSource) initialContext.lookup("java:comp/env/jdbc/framework");
            return lookup.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }
    }
}
