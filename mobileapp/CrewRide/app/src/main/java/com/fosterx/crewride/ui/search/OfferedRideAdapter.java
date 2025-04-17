package com.fosterx.crewride.ui.search;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fosterx.crewride.MainActivity;
import com.fosterx.crewride.R;
import com.fosterx.crewride.network.ApiClient;
import com.fosterx.crewride.network.ApiService;
import com.fosterx.crewride.obj.GeoLocation;
import com.fosterx.crewride.obj.JoinRideRequest;
import com.fosterx.crewride.obj.OfferedRide;
import com.fosterx.crewride.ui.rides.RidesFragment;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OfferedRideAdapter extends RecyclerView.Adapter<OfferedRideAdapter.ViewHolder> {

    private List<OfferedRide> rideList;
    private Context context;
    // Assume you have a variable for the current user's location
    private GeoLocation currentUserLocation;
    private String datetime;

    private boolean isSelectable;

    public OfferedRideAdapter(Context context, List<OfferedRide> rideList, GeoLocation currentUserLocation, String datetime) {
        this.context = context;
        this.rideList = rideList;
        this.currentUserLocation = currentUserLocation;
        this.datetime = datetime;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_offered_ride, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OfferedRide ride = rideList.get(position);

        // For demonstration purposes, assume the driver name and headshot are derived from driverId.
        // In a real app these would be part of the API response.
        String driverName = ride.getDriverName(); // Replace with ride.getDriverName() if available
        String driverHeadshotUrl = ride.getDriverHeadshotUrl(); // Replace with ride.getDriverHeadshotUrl() if available

        holder.tvDriverName.setText(driverName);

        // Load driver's headshot with Glide using circle crop
        Glide.with(context)
                .load(driverHeadshotUrl)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.ic_person_placeholder)
                .into(holder.ivDriverHeadshot);

        // Remove county info from addresses by removing any substring that contains "County"
        Log.e("TAG", "Ride" + ride.toString());

        String fromAddress = ride.getFromAddress();
        if (fromAddress.contains(",")) {
            fromAddress = fromAddress.replaceAll(",\\s*[^,]*County", "");
        }
        String toAddress = ride.getToAddress();
        if (toAddress.contains(",")) {
            toAddress = toAddress.replaceAll(",\\s*[^,]*County", "");
        }

        holder.tvFromAddress.setText("From: " + fromAddress);
        holder.tvToAddress.setText("To: " + toAddress);

        // Convert departure time from UTC ISO string to local time format
        try {
            SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = utcFormat.parse(ride.getDepartureTime());
            SimpleDateFormat localFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
            localFormat.setTimeZone(TimeZone.getDefault());
            String localTime = localFormat.format(date);
            holder.tvDepartureTime.setText("Departure: " + localTime);
        } catch (Exception e) {
            e.printStackTrace();
            holder.tvDepartureTime.setText("Departure: N/A");
        }

        // Calculate the shortest distance (stub implementation)
        double distance = getShortestDistanceToPath(currentUserLocation, ride.getDrivingPath());
        holder.tvDistance.setText(String.format("Distance: %.2f miles", distance));

        // Set a click listener to trigger join confirmation
        holder.itemView.setOnClickListener(v -> {
            if (isSelectable) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Join Ride")
                        .setMessage("Would you like to join " + ride.getDriverName() + " for a ride?")
                        .setPositiveButton("Join", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // Show a progress dialog while joining the ride
                                ProgressDialog progressDialog = new ProgressDialog(context);
                                progressDialog.setMessage("Joining ride...");
                                progressDialog.setCancelable(false);
                                progressDialog.show();

                                // Create a join ride request. Adjust fields as required.
                                JoinRideRequest joinRequest = new JoinRideRequest();
                                joinRequest.setRideId(ride.getRideId());
                                String employeeId = ((MainActivity) context).getUser().getEmployeeId();
                                joinRequest.setEmployeeId(employeeId);
                                joinRequest.setPickupLatitude(currentUserLocation.getLatitude());
                                joinRequest.setPickupLongitude(currentUserLocation.getLongitude());
                                joinRequest.setMeetingTime(datetime);
                                // Optionally, set pickup latitude, longitude, and address if needed

                                ApiService apiService = ApiClient.getClient(context).create(ApiService.class);
                                apiService.joinRide(joinRequest).enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        progressDialog.dismiss();
                                        if (response.isSuccessful()) {
                                            Toast.makeText(context, "Joined ride successfully", Toast.LENGTH_SHORT).show();
                                            // Navigate to Ride Fragment. Adjust your navigation code as needed.
                                            ((MainActivity) context).loadFragment(new RidesFragment());
                                        } else {
                                            Toast.makeText(context, "Error joining ride", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        progressDialog.dismiss();
                                        Toast.makeText(context, "Failed to join ride: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                builder.create().show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return rideList.size();
    }

    public void updateData(List<OfferedRide> rides) {
        this.rideList = rides;
        notifyDataSetChanged();
    }

    public void setSelectable(boolean selectable) {
        isSelectable = selectable;
    }

    /**
     * Uses the Haversine formula to calculate the distance (in miles) between two geographic points.
     */
    private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371; // Earth's radius in kilometers
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distanceKm = R * c;
        return distanceKm * 0.621371; // Convert kilometers to miles
    }

    /**
     * Calculate the shortest distance from currentLocation to the points in the drivingPath.
     *
     * @param currentLocation The current location.
     * @param drivingPath     A JSON string representing an array of coordinate pairs.
     * @return The shortest distance in miles.
     */
    private double getShortestDistanceToPath(GeoLocation currentLocation, String drivingPath) {
        double minDistance = Double.MAX_VALUE;
        try {
            JSONArray pathArray = new JSONArray(drivingPath);
            for (int i = 0; i < pathArray.length(); i++) {
                JSONArray coord = pathArray.getJSONArray(i);
                double lat = coord.getDouble(0);
                double lon = coord.getDouble(1);
                double distance = haversineDistance(currentLocation.getLatitude(), currentLocation.getLongitude(), lat, lon);
                if (distance < minDistance) {
                    minDistance = distance;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return minDistance;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivDriverHeadshot;
        TextView tvDriverName, tvFromAddress, tvToAddress, tvDepartureTime, tvDistance;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivDriverHeadshot = itemView.findViewById(R.id.iv_driver_headshot);
            tvDriverName = itemView.findViewById(R.id.tv_driver_name);
            tvFromAddress = itemView.findViewById(R.id.tv_from_address);
            tvToAddress = itemView.findViewById(R.id.tv_to_address);
            tvDepartureTime = itemView.findViewById(R.id.tv_departure_time);
            tvDistance = itemView.findViewById(R.id.tv_distance);
        }
    }
}