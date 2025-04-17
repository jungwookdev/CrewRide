package com.fosterx.crewride.ui.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.fosterx.crewride.MainActivity;
import com.fosterx.crewride.R;
import com.fosterx.crewride.network.ApiClient;
import com.fosterx.crewride.network.ApiService;
import com.fosterx.crewride.obj.Participant;
import com.fosterx.crewride.obj.PrepResponse;
import com.fosterx.crewride.obj.Ride;
import com.fosterx.crewride.obj.User;
import com.fosterx.crewride.util.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements OnMapReadyCallback {
    private ImageView ivHeadshot;
    private TextView tvWelcome;
    private SupportMapFragment mapFragment;
    private GoogleMap googleMap;

    // Views inside the CardView
    private TextView tvEta, tvDriverName, tvNoRides;
    private ImageView ivDriverHeadshot;
    private MaterialButton btnCallDriver, btnSearchRide, btnOfferRide, btnChat, btnPrep;
    private LinearLayout layoutRiders;
    private CardView cardRide;
    private Ride currentRide;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ivHeadshot = view.findViewById(R.id.iv_headshot);

        // Load driver headshot from API (e.g., using Glide or Coil)
        Glide.with(requireContext())
                .load(((MainActivity) getActivity()).getUser().getHeadshotUrl())
                .placeholder(R.drawable.ic_person_placeholder)
                .into(ivHeadshot);

        tvWelcome = view.findViewById(R.id.tv_welcome);
        tvWelcome.setText("Hello,\n" + ((MainActivity) getActivity()).getUser().getName() + "!");
        Button btnSearch = view.findViewById(R.id.btn_search_ride);
        Button btnOffer = view.findViewById(R.id.btn_offer_ride);


        btnSearch.setOnClickListener(v -> {
            ((MainActivity) getActivity()).getBottomNavigationView().setSelectedItemId(R.id.nav_search);
        });

        btnOffer.setOnClickListener(v -> {
            ((MainActivity) getActivity()).getBottomNavigationView().setSelectedItemId(R.id.nav_rides);
        });

        // Set up the CardView for Upcoming Ride
        cardRide = view.findViewById(R.id.card_ride);
        // Initialize the map container inside the CardView.
        FrameLayout mapContainer = cardRide.findViewById(R.id.map_fragment_container);
        // Look for an existing SupportMapFragment; if not found, create one.
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment_container);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.map_fragment_container, mapFragment)
                    .commit();
        }
        mapFragment.getMapAsync(this);

        // Set up references to views in the CardView
        tvEta = cardRide.findViewById(R.id.tv_eta);
        tvDriverName = cardRide.findViewById(R.id.tv_driver_name);

        ivDriverHeadshot = cardRide.findViewById(R.id.iv_driver_headshot);
        btnCallDriver = cardRide.findViewById(R.id.btn_call_driver);
        layoutRiders = cardRide.findViewById(R.id.layout_riders);
        btnChat = cardRide.findViewById(R.id.btn_chat);
        btnPrep = cardRide.findViewById(R.id.btn_prep);
        tvNoRides = view.findViewById(R.id.tv_no_rides);

        // For this demo, fill the card with dummy upcoming ride data:
        fetchCurrentRide(((MainActivity) getActivity()).getUser());

        return view;
    }


    private void fetchCurrentRide(User user) {
        // Replace with actual employee id if needed
        String employeeId = user.employeeId;
        ApiService apiService = ApiClient.getClient(getContext()).create(ApiService.class);
        apiService.getCurrentRideByEmployee(employeeId).enqueue(new Callback<Ride>() {
            @Override
            public void onResponse(Call<Ride> call, Response<Ride> response) {
                Log.e("TAG", "call" + call.toString());

                if (response.isSuccessful() && response.body() != null) {
                    currentRide = response.body();
                    
                    cardRide.setVisibility(View.VISIBLE);
                    tvNoRides.setVisibility(View.GONE);
                    populateCard();

                } else {
                    cardRide.setVisibility(View.GONE);
                    tvNoRides.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<Ride> call, Throwable t) {
                Log.e("TAG", "Failed to retrieve ride: " + t.getMessage());

                Toast.makeText(getContext(), "Failed to retrieve ride: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateCard() {
        tvEta.setText("Arriving in " + currentRide.getEstimatedTimeOfArrival() + " Minutes");
        // Driver info
        tvDriverName.setText(currentRide.getDriver().getName().replace(" ", "\n"));

        // Load driver headshot from API (e.g., using Glide or Coil)
        Glide.with(requireContext())
                .load(currentRide.getDriver().getHeadshotUrl()) // your headshot image API
                .placeholder(R.drawable.ic_person_placeholder)
                .into(ivDriverHeadshot);

        // Set up call driver button
        btnCallDriver.setOnClickListener(v -> {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + currentRide.getDriver().getContact()));
            startActivity(callIntent);
        });

        // Chat button
        btnChat.setOnClickListener(v -> {
            // Open your chat screen with the driver
        });

        // Prep button
        btnPrep.setOnClickListener(v -> {
            // Call the prep API and show suggestions in a dialog
            String rideId = currentRide.getRideId();
            String employeeId = ((MainActivity) getActivity()).getUser().employeeId;
            ApiService apiService = ApiClient.getClient(getContext()).create(ApiService.class);
            apiService.getPrep(rideId, employeeId).enqueue(new Callback<PrepResponse>() {
                @Override
                public void onResponse(Call<PrepResponse> call, Response<PrepResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String suggestions = response.body().getPrepSuggestion();
                        int marker = suggestions.indexOf("{");
                        String jsonPart = marker != -1 ? suggestions.substring(marker).trim() : suggestions.trim();

                        SpannableStringBuilder ssb = new SpannableStringBuilder();
                        try {
                            // Parse outer JSON
                            PrepResponse prep = response.body();
                            String note = prep.getNote(); // assuming you added getNote()
                            String prepRaw = prep.getPrepSuggestion(); // this is a JSON string

                            // Add Note if exists
                            if (note != null && !note.isEmpty()) {
                                String noteText = "Note: " + note + "\n\n";
                                ssb.append(noteText);
                                ssb.setSpan(new ForegroundColorSpan(Color.DKGRAY), 0, noteText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                ssb.setSpan(new AbsoluteSizeSpan(12, true), 0, noteText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }

                            // Parse inner JSON
                            JSONObject prepJson = new JSONObject(prepRaw);
                            JSONArray topics = prepJson.getJSONArray("topics");

                            for (int i = 0; i < topics.length(); i++) {
                                JSONObject topic = topics.getJSONObject(i);
                                String title = topic.getString("title");
                                String summary = topic.getString("summary");

                                int topicStart = ssb.length();
                                ssb.append((i + 1) + ". " + title + "\n");
                                ssb.setSpan(new StyleSpan(Typeface.BOLD), topicStart, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                ssb.setSpan(new ForegroundColorSpan(Color.BLACK), topicStart, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                ssb.setSpan(new AbsoluteSizeSpan(16, true), topicStart, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                                int sumStart = ssb.length();
                                ssb.append(summary + "\n\n");
                                ssb.setSpan(new ForegroundColorSpan(Color.DKGRAY), sumStart, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                ssb.setSpan(new AbsoluteSizeSpan(14, true), sumStart, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ssb.append("Failed to parse suggestions.");
                        }

                        // Inflate custom dialog view
                        View customView = LayoutInflater.from(requireContext())
                                .inflate(R.layout.dialog_prep_suggestions, null);
                        // wrap in a container that supports margin:
                        FrameLayout container = new FrameLayout(getContext());
                        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.MATCH_PARENT,
                                FrameLayout.LayoutParams.WRAP_CONTENT);
                        lp.setMargins(32, 32, 32, 32);  // your desired margins in px
                        container.addView(customView, lp);

                        TextView tvTitle = customView.findViewById(R.id.dialog_prep_title);
                        TextView tvContent = customView.findViewById(R.id.dialog_prep_content);
                        tvTitle.setText("Prep");
                        tvContent.setText(ssb);

                        // Build and show dialog
                        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                                .setView(container)
                                .setPositiveButton("OK", null)
                                .create();
                        dialog.show();
                        // Make background transparent so custom rounded corners show
                        if (dialog.getWindow() != null) {
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        }

                    } else {
                        Toast.makeText(getContext(), "Failed to get prep suggestions", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<PrepResponse> call, Throwable t) {
                    Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Populate riders
        for (Participant rider : currentRide.getParticipants()) {
            View riderItem = LayoutInflater.from(getContext()).inflate(R.layout.rider_item_layout, layoutRiders, false);
            ImageView ivRiderHeadshot = riderItem.findViewById(R.id.iv_rider_headshot);
            TextView tvRiderName = riderItem.findViewById(R.id.tv_rider_name);

            // Load rider headshot
            Glide.with(requireContext())
                    .load(rider.getHeadshotUrl())
                    .placeholder(R.drawable.ic_person_placeholder)
                    .into(ivRiderHeadshot);

            tvRiderName.setText(rider.getName().replace(" ", "\n"));

            layoutRiders.addView(riderItem);
        }

        mapFragment.getMapAsync(this);
    }


    private List<LatLng> parseDrivingPath(String drivingPathStr) {
        List<LatLng> latLngList = new ArrayList<>();
        try {
            // Assuming drivingPathStr is like "[[37.38059, -122.11332], [37.38059, -122.11383], ...]"
            JsonArray array = JsonParser.parseString(drivingPathStr).getAsJsonArray();
            for (JsonElement element : array) {
                JsonArray coords = element.getAsJsonArray();
                double lat = coords.get(0).getAsDouble();
                double lng = coords.get(1).getAsDouble();
                latLngList.add(new LatLng(lat, lng));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return latLngList;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        if (currentRide != null) {
            googleMap = map;

            List<LatLng> drivingPath = parseDrivingPath(currentRide.getDrivingPath());

            LatLng driverLoc = drivingPath.get(0);
            LatLng userLoc = drivingPath.get((int) drivingPath.size() / 2);
            String driverHeadshotUrl = currentRide.getDriver().getHeadshotUrl();
            String userHeadshotUrl = ((MainActivity) getActivity()).getUser().getHeadshotUrl();

            Glide.with(getContext())
                    .asBitmap()
                    .load(driverHeadshotUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            // Scale the bitmap to half its size
                            int width = resource.getWidth();
                            int height = resource.getHeight();
                            Bitmap scaledBitmap = Bitmap.createScaledBitmap(resource, width / 2, height / 2, false);

                            MarkerOptions markerOptions = new MarkerOptions()
                                    .position(driverLoc)
                                    .title("Driver Location")
                                    .icon(BitmapDescriptorFactory.fromBitmap(scaledBitmap));
                            googleMap.addMarker(markerOptions);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            // Handle cleanup if necessary
                        }
                    });

            Glide.with(getContext())
                    .asBitmap()
                    .load(userHeadshotUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            // Scale the bitmap to half its size
                            int width = resource.getWidth();
                            int height = resource.getHeight();
                            Bitmap scaledBitmap = Bitmap.createScaledBitmap(resource, width / 2, height / 2, false);

                            MarkerOptions markerOptions = new MarkerOptions()
                                    .position(userLoc)
                                    .title("Driver Location")
                                    .icon(BitmapDescriptorFactory.fromBitmap(scaledBitmap));
                            googleMap.addMarker(markerOptions);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            // Handle cleanup if necessary
                        }
                    });

            LatLng destination = drivingPath.get(drivingPath.size() - 1);
            Bitmap destBitmap = Utils.getBitmapFromVector(getContext(), R.mipmap.ic_destination_round);
            googleMap.addMarker(new MarkerOptions()
                    .position(destination)
                    .title("Destination")
                    .icon(BitmapDescriptorFactory.fromBitmap(destBitmap)));

            PolylineOptions polylineOptions = new PolylineOptions()
                    .addAll(drivingPath)
                    .color(Color.BLUE)
                    .width(6f);
            googleMap.addPolyline(polylineOptions);

            LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
            boundsBuilder.include(drivingPath.get(0));
            boundsBuilder.include(drivingPath.get(drivingPath.size() - 1));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 150));
            googleMap.moveCamera(CameraUpdateFactory.scrollBy(-100, 0));
        }
    }
}