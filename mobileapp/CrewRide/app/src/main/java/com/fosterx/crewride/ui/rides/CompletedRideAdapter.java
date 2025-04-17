package com.fosterx.crewride.ui.rides;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fosterx.crewride.R;
import com.fosterx.crewride.obj.Ride;

import java.util.List;

public class CompletedRideAdapter extends RecyclerView.Adapter<CompletedRideAdapter.ViewHolder> {

    private final List<Ride> rides;
    private final Context context;

    public CompletedRideAdapter(List<Ride> rides, Context context) {
        this.rides = rides;
        this.context = context;
    }

    public void updateData(List<Ride> newRides) {
        rides.clear();
        rides.addAll(newRides);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_completed_ride, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ride ride = rides.get(position);
        //holder.rideInfo.setText("From: " + ride.from + "\nTo: " + ride.to + "\nDate: " + ride.date + "\nRider: " + ride.driverName + "\nCo-riders: " + ride.coRiderNames);

        /*
        holder.btnThank.setOnClickListener(v -> {
            ApiService api = ApiClient.getClient(context).create(ApiService.class);
            api.sendThankYou(ApiService.rideIdPayload(ride.id)).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Toast.makeText(context, "Thank you sent!", Toast.LENGTH_SHORT).show();
                    holder.btnThank.setEnabled(false);
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(context, "Failed to send message", Toast.LENGTH_SHORT).show();
                }
            });
        });
        */
    }

    @Override
    public int getItemCount() {
        return rides.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView rideInfo;
        Button btnThank;

        ViewHolder(View itemView) {
            super(itemView);
            rideInfo = itemView.findViewById(R.id.text_ride_info);
            btnThank = itemView.findViewById(R.id.btn_thank);
        }
    }
}
