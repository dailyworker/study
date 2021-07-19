package io.dailyworker.framework.db.ex;

import io.dailyworker.framework.aop.CustomRequest;
import io.dailyworker.framework.controller.CustomRequestContext;
import io.dailyworker.framework.controller.TransactionContext;
import io.dailyworker.framework.db.SQLiteJdbcOopTransaction;
import io.dailyworker.framework.db.Transaction;
import io.dailyworker.framework.db.ex.dto.EmployeeDto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeJdbc {
    public static void main(String[] args) throws SQLException {
        String name = "Seansin";
        String phoneNum = "010-1234-1234";

        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setName(name);
        employeeDto.setPhoneNum(phoneNum);

        Transaction transaction = TransactionContext.get();

        List<EmployeeDto> maybeEmployees = select(transaction, employeeDto);

        update(transaction, maybeEmployees, employeeDto);

        transaction.closeConnection();

        transaction = new SQLiteJdbcOopTransaction();

        CustomRequest customRequest = CustomRequestContext.get();
        customRequest.put("EMP_NAME", name);

        List<String[]> table = selectForCollection(transaction);

        System.out.println(table.get(1)[0]);
        System.out.println(table.get(1)[2]);

        transaction.closeConnection();
    }

    private static List<String[]> selectForCollection(Transaction transaction) throws SQLException {

        CustomRequest customRequest = CustomRequestContext.get();
        String name = customRequest.getString("EMP_NAME");

        String query = "SELECT EMP_NO, EMP_NAME, HP_N, DEPT_N FROM EMP";

        if (!"".equals(name)) {
            query += " WHERE EMP_NAME = '" + name + "'";
        }

        System.out.println(query);

        Statement statement = transaction.statement();
        ResultSet resultSet = statement.executeQuery(query);

        ArrayList<String[]> table = new ArrayList<>();

        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        String[] column = new String[columnCount];

        for (int i = 0; i < column.length; i++) {
            column[i] = metaData.getColumnName(i + 1);
        }

        table.add(column);


        while (resultSet.next()) {
            String[] recode = new String[columnCount];

            for(int i = 0; i < column.length; i++) {
                recode[i] = resultSet.getString(column[i]);
            }
            table.add(recode);
        }
        return table;
    }

    private static List<EmployeeDto> select(Transaction transaction, EmployeeDto employeeDto) throws SQLException {
        String name = employeeDto.getName();
        String query = "SELECT EMP_NO, EMP_NAME, HP_N, DEPT_N FROM EMP";

        if (!"".equals(name)) {
            query += " WHERE EMP_NAME = '" + name + "'";
        }

        System.out.println(query);

        Statement statement = transaction.statement();
        ResultSet resultSet = statement.executeQuery(query);

        ArrayList<EmployeeDto> employeeDtos = new ArrayList<>();

        while (resultSet.next()) {

            EmployeeDto dto = new EmployeeDto();
            dto.setId(resultSet.getString("EMP_NO"));
            dto.setName(resultSet.getString("EMP_NAME"));
            dto.setPhoneNum(resultSet.getString("HP_N"));
            dto.setDepartmentNo(resultSet.getString("DEPT_N"));

            employeeDtos.add(dto);
        }
        return employeeDtos;
    }

    private static void update(Transaction transaction,
            List<EmployeeDto> maybeEmployees,
            EmployeeDto employeeDto) throws SQLException {

        transaction.setAutoCommitFalse();

        try {
            for(EmployeeDto dto : maybeEmployees) {
                String id = dto.setId();
                String phoneNum = employeeDto.getPhoneNum();

                String query = "UPDATE EMP SET HP_N = ? WHERE EMP_NO = ?";
                PreparedStatement preparedStatement = transaction.preparedStatement(query);
                preparedStatement.setString(1, phoneNum);
                preparedStatement.setString(2, id);

                System.out.println(query);

                int updateCnt = preparedStatement.executeUpdate();
                if(updateCnt != 1) {
                    throw new SQLException("업데이트에 실패하였습니다.");
                }
            }
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        }
    }
}
