package io.dailyworker.framework.domain;

import io.dailyworker.framework.db.SqlRunner;
import io.dailyworker.framework.db.Table;

import java.sql.SQLException;

public class EmployeeRepository {
    private static final String SQL_SELECT = "SQL_SELECT_BY_USERNAME";
    private static final String SQL_UPDATE = "SQL_UPDATE_EMP";

    private final SqlRunner sql = SqlRunner.getSqlRunner();

    Table select() throws SQLException {
        return sql.getTable(SQL_SELECT);
    }

    int update() throws SQLException {
        return sql.executeSql(SQL_UPDATE);
    }

}
