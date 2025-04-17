package com.fosterx.crewride.ui.rides;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fosterx.crewride.obj.Ride;

import java.util.List;

public class RideAdapter extends RecyclerView.Adapter<RideAdapter.ViewHolder> {

    private final List<Ride> rides;

    public RideAdapter(List<Ride> rides) {
        this.rides = rides;
    }

    public void updateData(List<Ride> newRides) {
        rides.clear();
        rides.addAll(newRides);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RideAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView view = new TextView(parent.getContext());
        view.setPadding(24, 24, 24, 24);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RideAdapter.ViewHolder holder, int position) {
        Ride ride = rides.get(position);
        holder.textView.setText("From:  ride.from +  To:  + ride.to + \nDate: + ride.date");
    }

    @Override
    public int getItemCount() {
        return rides.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = (TextView) itemView;
        }
    }
}
