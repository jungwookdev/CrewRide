package com.fosterx.crewride.obj;

import java.io.Serializable;

public class User implements Serializable {

    public String userId;

    public String employeeId;

    public String name;
    public String email;
    public String phoneNumber;
    public String department;
    public String jobRole;

    public String headshotUrl;

    public User(String userId, String employeeId, String name, String email, String phoneNumber, String department, String jobRole, String headshotUrl) {
        this.userId = userId;
        this.employeeId = employeeId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.department = department;
        this.jobRole = jobRole;
        this.headshotUrl = headshotUrl;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getDepartment() {
        return department;
    }

    public String getJobRole() {
        return jobRole;
    }

    public String getHeadshotUrl() {
        return headshotUrl;
    }

    public User() {} // Required for Gson
}
