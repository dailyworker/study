package io.dailyworker.framework.domain;

import io.dailyworker.framework.aop.CustomRequest;
import io.dailyworker.framework.controller.BizController;
import io.dailyworker.framework.controller.CustomRequestContext;
import io.dailyworker.framework.db.Table;

import java.sql.SQLException;

public class Employee implements BizController {
    public String select() throws SQLException {

        CustomRequest customRequest = CustomRequestContext.get();

        EmployeeRepository employeeRepository = new EmployeeRepository();
        Table maybeTable = employeeRepository.select();

        customRequest.put("table", maybeTable);

        return "select";
    }

    public String update() throws Exception {

        CustomRequest customRequest = CustomRequestContext.get();

        EmployeeRepository employeeRepository = new EmployeeRepository();
        Table maybeTable = employeeRepository.select();

        for(int i = 0; i < maybeTable.size(); i++) {
            String maybeEmpNo = maybeTable.find("EMP_NO", i);
            customRequest.put("EMP_NO", maybeEmpNo);
            if(employeeRepository.update() != 1) {
                throw new Exception();
            }
        }
        return select();
    }
}
