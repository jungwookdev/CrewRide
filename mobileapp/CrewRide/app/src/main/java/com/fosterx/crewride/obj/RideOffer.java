package com.fosterx.crewride.obj;

public class RideOffer {
    private String employeeId;
    private double fromLatitude;
    private double fromLongitude;
    private double toLatitude;
    private double toLongitude;
    private String departureTime;
    private int bufferTime;
    private int offeredSeats;

    public RideOffer(String employeeId, double fromLatitude, double fromLongitude, double toLatitude, double toLongitude, String departureTime, int bufferTime, int offeredSeats) {
        this.employeeId = employeeId;
        this.fromLatitude = fromLatitude;
        this.fromLongitude = fromLongitude;
        this.toLatitude = toLatitude;
        this.toLongitude = toLongitude;
        this.departureTime = departureTime;
        this.bufferTime = bufferTime;
        this.offeredSeats = offeredSeats;
    }

    // Getters and setters omitted for brevity
}
