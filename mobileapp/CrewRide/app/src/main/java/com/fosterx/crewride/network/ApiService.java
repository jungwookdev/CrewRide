package com.fosterx.crewride.network;

import com.fosterx.crewride.obj.JoinRideRequest;
import com.fosterx.crewride.obj.MessageRequest;
import com.fosterx.crewride.obj.MessageResponse;
import com.fosterx.crewride.obj.OfferedRideResponse;
import com.fosterx.crewride.obj.PrepResponse;
import com.fosterx.crewride.obj.Ride;
import com.fosterx.crewride.obj.RideHistoryResponse;
import com.fosterx.crewride.obj.RideOffer;
import com.fosterx.crewride.obj.User;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    class LoginRequest {
        public String email;
        public String password;
    }

    @POST("api/auth/login")
    Call<User> login(@Body LoginRequest request);

    // Sign up for a new user (using same credentials as login for demonstration)
    class SignupRequest {
        @SerializedName("user_id")
        public String userId;  // User table id
        @SerializedName("employee_id")
        public String employeeId;
        public String password;
        public String email;
        @SerializedName("phone_number")
        public String phoneNumber;
    }

    @POST("api/auth/signup")
    Call<Void> signup(@Body SignupRequest request);

    // Employee registration (detailed info)
    class EmployeeRegistrationRequest {
        @SerializedName("employee_id")
        public String employeeId;
        public String name;
        public String department;
        @SerializedName("job_role")
        public String jobRole;
        @SerializedName("work_schedule_type")
        public String workScheduleType;
        @SerializedName("remote_days")
        public String remoteDays;
        @SerializedName("languages_spoken")
        public String languagesSpoken;
        @SerializedName("emergency_contact")
        public String emergencyContact;
        @SerializedName("seniority_level")
        public String seniorityLevel;
        @SerializedName("home_latitude")
        public float homeLatitude;
        @SerializedName("home_longitude")
        public float homeLongitude;
        @SerializedName("home_area_name")
        public String homeAreaName;
        @SerializedName("parking_availability_near_home")
        public String parkingAvailabilityNearHome;
        @SerializedName("work_hours_start")
        public String workHoursStart;
        @SerializedName("work_hours_end")
        public String workHoursEnd;
        @SerializedName("days_working_in_office")
        public String daysWorkingInOffice;
        @SerializedName("meeting_heavy_days")
        public String meetingHeavyDays;
        @SerializedName("carpool_role")
        public String carpoolRole;
        @SerializedName("driving_availability_days")
        public String drivingAvailabilityDays;
        @SerializedName("preferred_pickup_time_to_goto_work")
        public String preferredPickupTimeToGotoWork;
        @SerializedName("preferred_dropoff_time_to_goto_work")
        public String preferredDropoffTimeToGotoWork;
        @SerializedName("preferred_pickup_time_to_leave_from_work")
        public String preferredPickupTimeToLeaveFromWork;
        @SerializedName("preferred_dropoff_time_to_leave_from_work")
        public String preferredDropoffTimeToLeaveFromWork;
        @SerializedName("ride_frequency_preference")
        public String rideFrequencyPreference;
        @SerializedName("preferred_communication")
        public String preferredCommunication;
        @SerializedName("music_preference")
        public String musicPreference;
        @SerializedName("co_rider_gender_preference")
        public String coRiderGenderPreference;
        @SerializedName("talk_preferences")
        public String talkPreferences;
        @SerializedName("conversation_topics")
        public String conversationTopics;
        @SerializedName("allergies_pet_peeves")
        public String allergiesPetPeeves;
        @SerializedName("car_model")
        public String carModel;
        @SerializedName("car_capacity")
        public int carCapacity;
        @SerializedName("comfort_level")
        public String comfortLevel;
        @SerializedName("fuel_type")
        public String fuelType;
        @SerializedName("carpool_insurance_coverage")
        public String carpoolInsuranceCoverage;
        @SerializedName("average_rating")
        public float averageRating;
        @SerializedName("punctuality_score")
        public float punctualityScore;
        @SerializedName("preferred_by_list")
        public String preferredByList;
        @SerializedName("notification_preferences")
        public String notificationPreferences;
        @SerializedName("auto_match_enabled")
        public int autoMatchEnabled;
        @SerializedName("preferred_carpool_group_size")
        public int preferredCarpoolGroupSize;
        @SerializedName("frequent_co_riders")
        public String frequentCoRiders;
        @SerializedName("do_not_match_list")
        public String doNotMatchList;
        public int cluster;
    }

    @POST("api/auth/register")
    Call<Void> registerEmployee(@Body EmployeeRegistrationRequest request);

    @GET("api/auth/profile")
    Call<User> getProfile(@Query("userId") String userId);

    // --- Ride Management ---
    @POST("/api/rides/offer")
    Call<Void> offerRide(@Body RideOffer rideOffer);

    @PATCH("api/rides/modify")
    Call<Void> modifyRide(@Body Map<String, Object> rideUpdates);
    // rideUpdates should include "rideId" and any fields to update (e.g., "bufferTime", "offeredSeats")

    @PATCH("api/rides/complete")
    Call<Void> completeRide(@Body Map<String, String> rideIdPayload);

    @GET("api/rides/search")
    Call<OfferedRideResponse> searchRides(
            @Query("employeeId") String employeeId,
            @Query("currentLatitude") double currentLatitude,
            @Query("currentLongitude") double currentLongitude,
            @Query("requestedDestLatitude") double requestedDestLatitude,
            @Query("requestedDestLongitude") double requestedDestLongitude,
            @Query("datetime") String datetime);

    @POST("api/rides/join")
    Call<Void> joinRide(@Body JoinRideRequest request);

    @GET("api/rides/history")
    Call<RideHistoryResponse> getRideHistory(@Query("employeeId") String employeeId);

    @DELETE("api/rides/cancel")
    Call<Void> cancelRide(@Body CancelRideRequest request);

    class CancelRideRequest {
        public String rideId;
        public String employeeId;
        public String reason;
    }

    @GET("api/rides/current")
    Call<Ride> getCurrentRideStatus(@Query("rideId") String rideId);

    @GET("api/rides/current/by-employee")
    Call<Ride> getCurrentRideByEmployee(@Query("employeeId") String employeeId);

    @PATCH("api/users/update-location")
    Call<Void> updateMyLocation(@Body LocationUpdateRequest request);

    class LocationUpdateRequest {
        public String employeeId;
        public float latitude;
        public float longitude;
    }

    @POST("api/rides/feedback")
    Call<Void> evaluateRide(@Body RideFeedbackRequest request);

    class RideFeedbackRequest {
        public String rideId;
        public String employeeId;
        public Integer satisfactionScore;
        public Integer punctualityScore;
        public String suggestionText;
    }

    // --- Messaging ---
    @POST("api/messages")
    Call<MessageResponse> sendMessage(@Body MessageRequest request);

    @GET("api/messages")
    Call<List<MessageResponse>> getMessages(@Query("rideId") String rideId,
                                            @Query("after") String after);

    // --- Headshot Upload (Optional) ---

    class HeadshotRequest {
        public String employeeId;
        public String headshot; // Base64-encoded data URL string.
    }

    class HeadshotResponse {
        public String message;
        public int headshotId;
    }

    @POST("api/headshot")
    Call<HeadshotResponse> uploadHeadshot(@Body HeadshotRequest request);

    // --- Optional Helper Methods ---

    static Map<String, String> rideIdPayload(String rideId) {
        Map<String, String> map = new HashMap<>();
        map.put("rideId", rideId);
        return map;
    }

    @GET("api/prep")
    Call<PrepResponse> getPrep(@Query("rideId") String rideId,
                               @Query("employeeId") String employeeId);
}
