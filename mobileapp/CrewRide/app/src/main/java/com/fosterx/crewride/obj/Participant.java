package com.fosterx.crewride.obj;


// Nested Participant class
public class Participant {
    private String employeeId;
    private String name;
    private String meetingTime;
    private String headshotUrl; // optional URL for participant's headshot

    public Participant() {
    }

    public Participant(String employeeId, String name, String meetingTime, String headshotUrl) {
        this.employeeId = employeeId;
        this.name = name;
        this.meetingTime = meetingTime;
        this.headshotUrl = headshotUrl;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMeetingTime() {
        return meetingTime;
    }

    public void setMeetingTime(String meetingTime) {
        this.meetingTime = meetingTime;
    }

    public String getHeadshotUrl() {
        return headshotUrl;
    }

    public void setHeadshotUrl(String headshotUrl) {
        this.headshotUrl = headshotUrl;
    }
}