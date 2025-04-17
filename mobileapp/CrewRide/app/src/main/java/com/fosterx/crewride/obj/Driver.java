package com.fosterx.crewride.obj;

public class Driver {
    private String name;
    private String contact;
    private String headshotUrl; // optional URL for driver's headshot image

    public Driver() {
    }

    public Driver(String name, String contact, String headshotUrl) {
        this.name = name;
        this.contact = contact;
        this.headshotUrl = headshotUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getHeadshotUrl() {
        return headshotUrl;
    }

    public void setHeadshotUrl(String headshotUrl) {
        this.headshotUrl = headshotUrl;
    }
}