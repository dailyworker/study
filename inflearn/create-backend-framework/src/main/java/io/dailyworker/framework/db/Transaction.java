package io.dailyworker.framework.db;

import java.sql.*;
import java.util.ArrayList;

public abstract class Transaction {
    private Connection conn = null;
    private final ArrayList<Statement> statements = new ArrayList<>();
    private final ArrayList<ResultSet> resultSets = new ArrayList<>();
    private boolean setAutoCommit = false;

    protected abstract Connection generateConnection() throws SQLException;

    public void closeConnection() {
        try {
            if (!hasConnection()) {
                return;
            }
            this.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void commit() throws SQLException {
        if(!hasConnection()) {
            return;
        }
        this.close();
        this.conn.commit();
    }

    public void rollback() throws SQLException {
        if(!hasConnection()) {
            return;
        }
        this.close();
        this.conn.rollback();
    }

    public PreparedStatement preparedStatement(String sql) throws SQLException {
        PreparedStatement preparedStatement = connection().prepareStatement(sql);
        statements.add(preparedStatement);
        return preparedStatement;
    }

    public Statement statement() throws SQLException {
        Statement statement = connection().createStatement();
        statements.add(statement);
        return statement;
    }

    public ResultSet resultSet(PreparedStatement preparedStatement) throws SQLException {
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSets.add(resultSet);
        return resultSet;
    }

    public void setAutoCommitFalse() throws SQLException {
        if(setAutoCommit) {
            return;
        }
        setAutoCommit = true;
        conn.setAutoCommit(false);
    }

    private Connection connection() throws SQLException {
        if(!hasConnection()) {
            this.conn = generateConnection();
        }
        return conn;
    }

    private void close() {
        closeStatements();
        closeResultSets();
    }

    private void closeResultSets() {
        for(ResultSet resultSet : resultSets) {
            try {
                resultSet.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        resultSets.clear();
    }

    private void closeStatements() {
        for(Statement statement : statements) {
            try {
                statement.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        statements.clear();
    }

    private boolean hasConnection() {
        return conn == null;
    }
}
