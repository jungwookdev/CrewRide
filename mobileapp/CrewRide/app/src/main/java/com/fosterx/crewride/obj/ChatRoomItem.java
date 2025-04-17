package com.fosterx.crewride.obj;

public class ChatRoomItem implements ChatRoomListItem {
    private String rideId;
    private String driverName;
    private String fromAddress;
    private String toAddress;
    private String departureTime; // e.g., formatted local time string
    private String groupDate;     // e.g., "August 1, 2023"

    public ChatRoomItem(String rideId, String driverName, String fromAddress, String toAddress, String departureTime, String groupDate) {
        this.rideId = rideId;
        this.driverName = driverName;
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.departureTime = departureTime;
        this.groupDate = groupDate;
    }

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
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

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getGroupDate() {
        return groupDate;
    }

    public void setGroupDate(String groupDate) {
        this.groupDate = groupDate;
    }

    @Override
    public int getItemType() {
        return TYPE_ITEM;
    }
}