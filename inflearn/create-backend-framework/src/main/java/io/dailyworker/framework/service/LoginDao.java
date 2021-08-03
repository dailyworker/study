package io.dailyworker.framework.service;

import io.dailyworker.framework.db.SqlRunner;
import io.dailyworker.framework.db.Table;

import java.sql.SQLException;

public class LoginDao {
    private static final String FORM_NO_01 = "LOGIN_01";
    private static final String FORM_NO_02 = "LOGIN_02";
    private static final String FORM_NO_03 = "LOGIN_03";

    private SqlRunner sqlRunner = SqlRunner.getSqlRunner();

    public static LoginDao getLoginDao() {
        return new LoginDao();
    }

    Table firstLoginPhase() throws SQLException {
        return sqlRunner.getTable(FORM_NO_01);
    }

    int secondLoginPhase() throws SQLException {
        return sqlRunner.executeSql(FORM_NO_02);
    }

    int lastLoginPhase() throws SQLException {
        return sqlRunner.executeSql(FORM_NO_03);
    }
}
