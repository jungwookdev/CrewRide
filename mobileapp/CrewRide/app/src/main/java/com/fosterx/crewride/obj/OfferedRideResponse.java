package com.fosterx.crewride.obj;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OfferedRideResponse {

    @SerializedName("rides")
    private List<OfferedRide> rides;

    public List<OfferedRide> getRides() {
        return rides;
    }

    public void setRides(List<OfferedRide> rides) {
        this.rides = rides;
    }
}