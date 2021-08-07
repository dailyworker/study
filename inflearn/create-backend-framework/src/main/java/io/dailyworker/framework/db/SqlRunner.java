package io.dailyworker.framework.db;

import io.dailyworker.framework.aop.CustomRequest;
import io.dailyworker.framework.controller.CustomRequestContext;
import io.dailyworker.framework.controller.TransactionContext;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

public class SqlRunner {


    private static final SqlRunner sqlRunner = new SqlRunner();

    private SqlRunner() {}

    public static SqlRunner getSqlRunner() {
        return sqlRunner;
    }

    String getSql(Transaction transaction, String key) throws SQLException {
        PreparedStatement preparedStatement = transaction.preparedStatement("SELECT SQL FROM FW_SQL WHERE KEY = ?");
        preparedStatement.setString(1, key);

        ResultSet resultSet = transaction.resultSet(preparedStatement);

        if (!resultSet.next()) {
            throw new RuntimeException();
        }

        String sql = resultSet.getString("SQL");

        transaction.clearResource();
        return sql;
    }

    SqlData getSqlData(String sql) throws SQLException {
        ArrayList<String> params = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();

        int fromIndex = -1;
        for(int i = 0;  i < 1000; i++) {
            int open = sql.indexOf("[", fromIndex);

            if(open == -1) {
                stringBuilder.append(sql.substring(fromIndex + 1));
                break;
            }

            int close = sql.indexOf("]", open + 1);

            params.add(sql.substring(open + 1, close));
            stringBuilder.append(sql.substring(fromIndex + 1, open) + "?");
            fromIndex = close;
        }

        SqlData sqlData = new SqlData();
        sqlData.param = params;
        sqlData.updatedSql = stringBuilder.toString();
        sqlData.originSql = sql;

        return sqlData;
    }

    SqlData getSqlData(Transaction transaction, String key) throws SQLException {
        String sql = getSql(transaction, key);

        SqlData sqlData = getSqlData(sql);
        sqlData.key = key;

        System.out.println(sqlData.originSql);
        System.out.println(sqlData.updatedSql);
        System.out.println(sqlData.key);

        return sqlData;
    }

    Table getTable(Transaction transaction, CustomRequest request, String key) throws SQLException {
        SqlData sqlData = getSqlData(transaction, key);

        PreparedStatement preparedStatement = transaction.preparedStatement(sqlData.updatedSql);

        String executeSql = sqlData.updatedSql;
        for(int i = 0; i < sqlData.param.size(); i++) {
            String maybeKey = sqlData.param.get(i).toLowerCase();
            String maybeData = request.getString(maybeKey);

            preparedStatement.setString(i+1, maybeData);
            executeSql = executeSql.replaceFirst("\\?", "'" + maybeData + "'");
        }

        System.out.println(executeSql);

        ResultSet resultSet = transaction.resultSet(preparedStatement);
        ResultSetMetaData metaData = resultSet.getMetaData();

        int columnCount = metaData.getColumnCount();
        String[] cols = new String[columnCount];

        for(int i = 0; i < cols.length; i++) {
            cols[i] = metaData.getColumnName(i + 1);
        }

        Table table = new Table(cols);

        while (resultSet.next()) {
            String[] record = new String[columnCount];

            for (int i = 0; i < cols.length; i++) {
                record[i] = resultSet.getString(cols[i]);
            }

            if (!table.addRecord(record)) {
                break;
            }
        }
        transaction.clearResource();
        return table;
    }

    public Table getTable(String key) throws SQLException {
        Transaction transaction = TransactionContext.get();
        CustomRequest customRequest = CustomRequestContext.get();

        return getTable(transaction, customRequest, key);
    }


    public Table getTable(String key, CustomRequest customRequest) throws SQLException {
        Transaction transaction = TransactionContext.get();
        return getTable(transaction, customRequest, key);
    }

    public int executeSql(Transaction transaction, CustomRequest customRequest, String key) throws SQLException {
        SqlData sqlData = getSqlData(transaction, key);

        transaction.setAutoCommitFalse();

        PreparedStatement preparedStatement = transaction.preparedStatement(sqlData.updatedSql);

        String executeSql = sqlData.updatedSql;
        for(int i = 0; i < sqlData.param.size(); i++) {
            String maybeKey = sqlData.param.get(i);
            String maybeData = customRequest.getString(maybeKey);
            preparedStatement.setString(i + 1, maybeData);

            executeSql = executeSql.replaceFirst("\\?", "'" + maybeData + "'");
        }

        System.out.println(executeSql);

        int updatedCnt = preparedStatement.executeUpdate();

        System.out.println("execute cnt [" + updatedCnt + "]");

        transaction.clearResource();

        return updatedCnt;
    }

    public int executeSql(String key) throws SQLException {
        Transaction transaction = TransactionContext.get();
        CustomRequest customRequest = CustomRequestContext.get();
        return executeSql(transaction, customRequest, key);
    }

    public int executeSql(String key, CustomRequest customRequest) throws SQLException {
        Transaction transaction = TransactionContext.get();
        return executeSql(transaction, customRequest, key);
    }
}
