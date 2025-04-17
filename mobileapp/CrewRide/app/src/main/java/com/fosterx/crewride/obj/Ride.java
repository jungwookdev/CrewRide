package com.fosterx.crewride.obj;


import java.util.List;

public class Ride {

    private String rideId;
    private Driver driver;
    private String fromAddress;
    private String toAddress;
    private GeoLocation currentDriverLocation;
    private String estimatedTimeOfArrival;
    private String drivingPath;
    private String conversationTopics;
    private List<Participant> participants;

    // Constructors
    public Ride() {
    }

    public Ride(String rideId, Driver driver, String fromAddress, String toAddress, GeoLocation currentDriverLocation,
                String estimatedTimeOfArrival, String drivingPath,
                String conversationTopics, List<Participant> participants) {
        this.rideId = rideId;
        this.driver = driver;
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.currentDriverLocation = currentDriverLocation;
        this.estimatedTimeOfArrival = estimatedTimeOfArrival;
        this.drivingPath = drivingPath;
        this.conversationTopics = conversationTopics;
        this.participants = participants;
    }

    // Getters and setters
    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public GeoLocation getCurrentDriverLocation() {
        return currentDriverLocation;
    }

    public void setCurrentDriverLocation(GeoLocation currentDriverLocation) {
        this.currentDriverLocation = currentDriverLocation;
    }

    public String getEstimatedTimeOfArrival() {
        return estimatedTimeOfArrival;
    }

    public void setEstimatedTimeOfArrival(String estimatedTimeOfArrival) {
        this.estimatedTimeOfArrival = estimatedTimeOfArrival;
    }

    public String getDrivingPath() {
        return drivingPath;
    }

    public void setDrivingPath(String drivingPath) {
        this.drivingPath = drivingPath;
    }

    public String getConversationTopics() {
        return conversationTopics;
    }

    public void setConversationTopics(String conversationTopics) {
        this.conversationTopics = conversationTopics;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

}