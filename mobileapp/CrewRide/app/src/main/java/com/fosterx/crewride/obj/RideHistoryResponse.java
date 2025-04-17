package com.fosterx.crewride.obj;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RideHistoryResponse {
    @SerializedName("driverRides")
    private List<OfferedRide> driverRides;

    @SerializedName("participantRides")
    private List<OfferedRide> participantRides;

    public List<OfferedRide> getDriverRides() {
        return driverRides;
    }

    public void setDriverRides(List<OfferedRide> driverRides) {
        this.driverRides = driverRides;
    }

    public List<OfferedRide> getParticipantRides() {
        return participantRides;
    }

    public void setParticipantRides(List<OfferedRide> participantRides) {
        this.participantRides = participantRides;
    }

    @Override
    public String toString() {
        return "RideHistoryResponse{" +
                "driverRides=" + driverRides +
                ", participantRides=" + participantRides +
                '}';
    }
}