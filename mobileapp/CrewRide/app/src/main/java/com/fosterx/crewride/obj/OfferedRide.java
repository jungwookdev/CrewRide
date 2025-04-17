package com.fosterx.crewride.obj;

import com.google.gson.annotations.SerializedName;

public class OfferedRide {

    @SerializedName(value = "rideId", alternate = {"ride_id"})
    private String rideId;

    @SerializedName(value = "driverId", alternate = {"driver_id"})
    private String driverId;

    @SerializedName(value = "driverName", alternate = {"driver_name"})
    private String driverName;

    @SerializedName(value = "driverHeadshotUrl", alternate = {"driver_headshotUrl"})
    private String driverHeadshotUrl;

    @SerializedName(value = "departureTime", alternate = {"departure_time"})
    private String departureTime;

    @SerializedName(value = "drivingPath", alternate = {"driving_path"})
    private String drivingPath;  // stored as String (you can later parse it into List<List<Double>> if needed)

    @SerializedName(value = "availableSeats", alternate = {"available_seats"})
    private int availableSeats;

    @SerializedName(value = "priority")
    private int priority;

    @SerializedName(value = "averageRating", alternate = {"average_rating"})
    private double averageRating;

    @SerializedName(value = "punctualityScore", alternate = {"punctuality_score"})
    private double punctualityScore;

    @SerializedName(value = "fromLatitude", alternate = {"from_latitude"})
    private double fromLatitude;

    @SerializedName(value = "fromLongitude", alternate = {"from_longitude"})
    private double fromLongitude;

    @SerializedName(value = "fromAddress", alternate = {"from_address"})
    private String fromAddress;

    @SerializedName(value = "toLatitude", alternate = {"to_latitude"})
    private double toLatitude;

    @SerializedName(value = "toLongitude", alternate = {"to_longitude"})
    private double toLongitude;

    @SerializedName(value = "toAddress", alternate = {"to_address"})
    private String toAddress;

    // Default constructor (required for Gson)
    public OfferedRide() {
    }

    // Parameterized constructor
    public OfferedRide(String rideId, String driverId, String driverName, String driverHeadshotUrl,
                       String departureTime, String drivingPath, int availableSeats, int priority,
                       double averageRating, double punctualityScore, double fromLatitude, double fromLongitude,
                       String fromAddress, double toLatitude, double toLongitude, String toAddress) {
        this.rideId = rideId;
        this.driverId = driverId;
        this.driverName = driverName;
        this.driverHeadshotUrl = driverHeadshotUrl;
        this.departureTime = departureTime;
        this.drivingPath = drivingPath;
        this.availableSeats = availableSeats;
        this.priority = priority;
        this.averageRating = averageRating;
        this.punctualityScore = punctualityScore;
        this.fromLatitude = fromLatitude;
        this.fromLongitude = fromLongitude;
        this.fromAddress = fromAddress;
        this.toLatitude = toLatitude;
        this.toLongitude = toLongitude;
        this.toAddress = toAddress;
    }

    // Getters and Setters

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverHeadshotUrl() {
        return driverHeadshotUrl;
    }

    public void setDriverHeadshotUrl(String driverHeadshotUrl) {
        this.driverHeadshotUrl = driverHeadshotUrl;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getDrivingPath() {
        return drivingPath;
    }

    public void setDrivingPath(String drivingPath) {
        this.drivingPath = drivingPath;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public double getPunctualityScore() {
        return punctualityScore;
    }

    public void setPunctualityScore(double punctualityScore) {
        this.punctualityScore = punctualityScore;
    }

    public double getFromLatitude() {
        return fromLatitude;
    }

    public void setFromLatitude(double fromLatitude) {
        this.fromLatitude = fromLatitude;
    }

    public double getFromLongitude() {
        return fromLongitude;
    }

    public void setFromLongitude(double fromLongitude) {
        this.fromLongitude = fromLongitude;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public double getToLatitude() {
        return toLatitude;
    }

    public void setToLatitude(double toLatitude) {
        this.toLatitude = toLatitude;
    }

    public double getToLongitude() {
        return toLongitude;
    }

    public void setToLongitude(double toLongitude) {
        this.toLongitude = toLongitude;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    @Override
    public String toString() {
        return "OfferedRide{" +
                "rideId='" + rideId + '\'' +
                ", driverId='" + driverId + '\'' +
                ", driverName='" + driverName + '\'' +
                ", driverHeadshotUrl='" + driverHeadshotUrl + '\'' +
                ", departureTime='" + departureTime + '\'' +
                ", drivingPath='" + drivingPath + '\'' +
                ", availableSeats=" + availableSeats +
                ", priority=" + priority +
                ", averageRating=" + averageRating +
                ", punctualityScore=" + punctualityScore +
                ", fromLatitude=" + fromLatitude +
                ", fromLongitude=" + fromLongitude +
                ", fromAddress='" + fromAddress + '\'' +
                ", toLatitude=" + toLatitude +
                ", toLongitude=" + toLongitude +
                ", toAddress='" + toAddress + '\'' +
                '}';
    }
}