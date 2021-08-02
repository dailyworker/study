package io.dailyworker.framework.domain;

import io.dailyworker.framework.aop.CustomRequest;
import io.dailyworker.framework.controller.CustomRequestContext;
import io.dailyworker.framework.controller.TransactionContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeTest {
    @Test
    @DisplayName("Employee를 생성한다.")
    public void create_employee() throws Exception {
        //given
        CustomRequest customRequest = CustomRequestContext.get();
        customRequest.put("EMP_NAME", "AAA");

        Employee employee = new Employee();

        //when
        employee.select();
        //then
    }

    @Test
    @DisplayName("Employee를 생성한다.")
    public void update_employee() throws Exception {
        //given
        CustomRequest customRequest = CustomRequestContext.get();
        customRequest.put("EMP_NAME", "AAA");
        customRequest.put("HP_N", "010-2222-3300");

        Employee employee = new Employee();

        //when
        employee.update();

        //then
        TransactionContext.commit();
    }
}