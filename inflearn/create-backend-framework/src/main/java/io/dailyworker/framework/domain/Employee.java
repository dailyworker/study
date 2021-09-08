package io.dailyworker.framework.domain;

import io.dailyworker.framework.aop.CustomRequest;
import io.dailyworker.framework.controller.BizController;
import io.dailyworker.framework.controller.CustomRequestContext;
import io.dailyworker.framework.db.Table;
import io.dailyworker.framework.security.DataCrypt;

import java.sql.SQLException;

public class Employee implements BizController {
    public String select() throws SQLException {

        CustomRequest customRequest = CustomRequestContext.get();

        EmployeeRepository employeeRepository = new EmployeeRepository();
        Table maybeTable = employeeRepository.select();

        /* FIXME : 이 부분은 비기능 품질 속성 관련해서 볼 때 수정될 예정
           개발자가 암, 복호화 기능을 코드라인으로 관리하는게 아니라 프레임워크에서 제공해줄 예정
           DataCrypt crypt = DataCrypt.getDataCrypt("SDB");

           for (int i = 0; i < maybeTable.size(); i++) {
                String encryptedPhoneNum = table.getString("HP_N", i);
                String decryptedPhoneNum = crypt.decrypt(encryptedPhoneNum);
                table.setData("HP_N", i, decryptedPhoneNum);
           }
        */

        customRequest.put("table", maybeTable);

        return "select";
    }

    public String update() throws Exception {

        CustomRequest customRequest = CustomRequestContext.get();

        /* FIXME : 이 부분은 비기능 품질 속성 관련해서 볼 때 수정될 예정
           개발자가 암, 복호화 기능을 코드라인으로 관리하는게 아니라 프레임워크에서 제공해줄 예정
           DataCrypt crypt = DataCrypt.getDataCrypt("SDB");
           String phoneNum = customRequest.getString("HP_N");
           phoneNum = crypt.encrypt(phoneNum);
           customRequest.put("HP_N", phoneNum);
        */
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
