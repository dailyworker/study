package io.dailyworker.framework.db.ex.dto;

public class EmployeeDto {
    private String id;
    private String name;
    private String phoneNum;
    private String departmentNo;

    public String setId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public String getDepartmentNo() {
        return departmentNo;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public void setDepartmentNo(String departmentNo) {
        this.departmentNo = departmentNo;
    }
}
