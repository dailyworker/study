package io.dailyworker.framework.controller;

import io.dailyworker.framework.aop.CustomHttpRequestLocal;
import io.dailyworker.framework.aop.CustomRequest;
import io.dailyworker.framework.db.SqlRunner;

import java.sql.SQLException;

public class MasterControllerDao {
    private static final String EMP_FORM = "MASTERCONTROLLER_ERD_01";
    private static final String LOGIN_FORM = "MASTERCONTROLLER_ERD_02";

    CustomRequest controller(String key) throws SQLException {
        CustomRequest request = new CustomHttpRequestLocal();
        request.put("key", key);
        return SqlRunner.getSqlRunner().getTable(EMP_FORM, request).getCustomRequest();
    }

    CustomRequest view(String key) throws SQLException {
        CustomRequest request = new CustomHttpRequestLocal();
        request.put("key", key);
        return SqlRunner.getSqlRunner().getTable(LOGIN_FORM, request).getCustomRequest();
    }
}
