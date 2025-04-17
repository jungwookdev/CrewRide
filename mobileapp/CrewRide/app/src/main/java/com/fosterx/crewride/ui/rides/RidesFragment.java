package com.fosterx.crewride.ui.rides;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.fosterx.crewride.MainActivity;
import com.fosterx.crewride.R;
import com.fosterx.crewride.network.ApiClient;
import com.fosterx.crewride.network.ApiService;
import com.fosterx.crewride.obj.GeoLocation;
import com.fosterx.crewride.obj.OfferedRide;
import com.fosterx.crewride.obj.Participant;
import com.fosterx.crewride.obj.PrepResponse;
import com.fosterx.crewride.obj.Ride;
import com.fosterx.crewride.obj.RideHistoryResponse;
import com.fosterx.crewride.obj.RideOffer;
import com.fosterx.crewride.obj.User;
import com.fosterx.crewride.ui.search.OfferedRideAdapter;
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
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RidesFragment extends Fragment implements OnMapReadyCallback {

    private TabLayout tabLayout;
    private NestedScrollView scrollCompletedRides;

    private GoogleMap googleMap;
    private SupportMapFragment mapFragment;
    private ImageView ivDriverHeadshot;
    private TextView tvNoRides, tvDriverName, tvETA;
    private CardView cardRide;
    private LinearLayout layoutRiders;
    private MaterialButton btnOfferRide, btnCallDriver, btnChat, btnPrep;

    private RecyclerView completedRidesRecycler;
    private LinearLayout llCurrentRide;
    private OfferedRideAdapter adapter;
    private List<OfferedRide> rideList = new ArrayList<>();
    // Use a dummy current location (replace with actual user location)
    private GeoLocation currentUserLocation = new GeoLocation(37.4809798, -122.2427129);

    private static final String EMPLOYEE_ID = "EMP038";

    private Ride currentRide;
    private GeoLocation geoLocationFrom = null;
    private GeoLocation geoLocationTo = null;
    private String mapsKey;
    private PlacesClient placesClient;
    private AutocompleteSessionToken sessionToken;
    private List<AutocompletePrediction> predictionList = new ArrayList<>();

    private MaterialAutoCompleteTextView inputFrom, inputTo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rides, container, false);


        try {
            ApplicationInfo ai = requireActivity().getPackageManager().getApplicationInfo(
                    requireActivity().getPackageName(), PackageManager.GET_META_DATA);
            mapsKey = ai.metaData.getString("com.google.android.geo.API_KEY");
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }

        if (mapsKey != null) {
            if (!Places.isInitialized()) {
                Places.initialize(getContext(), mapsKey);
            }
            placesClient = Places.createClient(getContext());
        }

        // Get references to the TabLayout and content containers
        tabLayout = view.findViewById(R.id.tab_layout);
        llCurrentRide = view.findViewById(R.id.ll_current_ride);
        scrollCompletedRides = view.findViewById(R.id.scroll_completed_rides);

        // Add two tabs to the TabLayout
        TabLayout.Tab currentRideTab = tabLayout.newTab().setText("Current Ride");
        TabLayout.Tab completedRidesTab = tabLayout.newTab().setText("Completed RideS");
        tabLayout.addTab(currentRideTab);
        tabLayout.addTab(completedRidesTab);

        cardRide = view.findViewById(R.id.card_ride);
        tvNoRides = view.findViewById(R.id.tv_no_rides);

        // Set default visibility: show current ride container and FAB, hide completed rides container
        llCurrentRide.setVisibility(View.VISIBLE);
        scrollCompletedRides.setVisibility(View.GONE);
        // fabOfferRide.setVisibility(View.VISIBLE);

        // Set TabLayout listener to toggle visibility between tabs
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    llCurrentRide.setVisibility(View.VISIBLE);
                    scrollCompletedRides.setVisibility(View.GONE);
                    //fabOfferRide.setVisibility(View.VISIBLE);
                    // Fetch current ride data from API based on employee id
                } else {
                    llCurrentRide.setVisibility(View.GONE);
                    scrollCompletedRides.setVisibility(View.VISIBLE);
                    //fabOfferRide.setVisibility(View.GONE);
                    loadRideHistory();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // No action required here
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // No action required here
            }
        });

        btnOfferRide = view.findViewById(R.id.btn_offer_ride);
        btnOfferRide.setOnClickListener(v -> {
            showOfferRideDialog();
        });

        CardView cardRide = view.findViewById(R.id.card_ride);

        FrameLayout mapFragmentContainer = cardRide.findViewById(R.id.map_fragment_container);
        mapFragmentContainer.setClipToOutline(true);
        mapFragment = SupportMapFragment.newInstance();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.map_fragment_container, mapFragment)
                .commit();

        tvETA = cardRide.findViewById(R.id.tv_eta);
        // Driver Info
        ivDriverHeadshot = cardRide.findViewById(R.id.iv_driver_headshot);
        tvDriverName = cardRide.findViewById(R.id.tv_driver_name);
        btnCallDriver = cardRide.findViewById(R.id.btn_call_driver);

        // Riders
        layoutRiders = cardRide.findViewById(R.id.layout_riders);

        // Other Actions
        btnChat = cardRide.findViewById(R.id.btn_chat);
        btnPrep = cardRide.findViewById(R.id.btn_prep);

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
                        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(requireContext())
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


        completedRidesRecycler = view.findViewById(R.id.completed_rides_recycler);
        completedRidesRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new OfferedRideAdapter(getContext(), rideList, currentUserLocation, null);
        completedRidesRecycler.setAdapter(adapter);

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
                    cardRide.setVisibility(View.VISIBLE);
                    tvNoRides.setVisibility(View.GONE);

                    currentRide = response.body();
                    Log.e("TAG", "headshot: " + currentRide.getDriver().getHeadshotUrl());
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
        tvETA.setText("Arriving in " + currentRide.getEstimatedTimeOfArrival() + " Minutes");
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
            // Perform any pre-ride steps
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


            if (!driverHeadshotUrl.equals(userHeadshotUrl)) {
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
            }

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

    public void showOfferRideDialog() {

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_offer_ride, null);
        inputFrom = dialogView.findViewById(R.id.input_from);
        inputTo = dialogView.findViewById(R.id.input_to);
        FrameLayout container = new FrameLayout(getContext());
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(20, 32, 20, 32);  // your desired margins in px
        container.addView(dialogView, lp);

        EditText inputDepartureTime = dialogView.findViewById(R.id.input_departure);
        setupInputFromAutocomplete(inputFrom);
        setupInputFromAutocomplete(inputTo);

        Spinner spinnerRiders = dialogView.findViewById(R.id.spinner_riders);
        String[] options = IntStream.rangeClosed(1, 10).mapToObj(String::valueOf).toArray(String[]::new);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, options);
        spinnerRiders.setAdapter(adapter);
        spinnerRiders.setSelection(2); // default to 3

        MaterialCheckBox cbChat = dialogView.findViewById(R.id.checkbox_chat);
        MaterialCheckBox cbMusic = dialogView.findViewById(R.id.checkbox_music);
        MaterialCheckBox cbCalm = dialogView.findViewById(R.id.checkbox_calm);

        MaterialCheckBox cbNoWork = dialogView.findViewById(R.id.checkbox_nowork);
        MaterialCheckBox cbEating = dialogView.findViewById(R.id.checkbox_eating);
        MaterialCheckBox cbDrinking = dialogView.findViewById(R.id.checkbox_drinking);

        inputDepartureTime.setOnClickListener(v -> showDateTimePicker(inputDepartureTime));

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setView(container)
                .setPositiveButton("Offer", (dialog, which) -> {
                    String to = inputTo.getText().toString();
                    String from = inputFrom.getText().toString();
                    String departure_time = inputDepartureTime.getText().toString();
                    int no_of_riders = spinnerRiders.getSelectedItemPosition() + 1;

                    List<String> preferences = new ArrayList<>();
                    if (cbChat.isChecked()) preferences.add("Small Chat");
                    if (cbMusic.isChecked()) preferences.add("Music");
                    if (cbCalm.isChecked()) preferences.add("Calm and Relax");

                    List<String> allowances = new ArrayList<>();
                    if (cbNoWork.isChecked()) allowances.add("No Work Talk");
                    if (cbEating.isChecked()) allowances.add("Eating");
                    if (cbDrinking.isChecked()) allowances.add("Drinking");

                    if (geoLocationFrom != null && geoLocationTo != null) {
                        submitOfferedRide(((MainActivity) getActivity()).getUser().getEmployeeId(),
                                geoLocationFrom, geoLocationTo, departure_time, no_of_riders
                        );
                    } else {
                        Toast.makeText(getContext(), "Please select valid 'From' and 'To' locations from suggestions", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null);

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
        // Make background transparent so custom rounded corners show
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    private void showDateTimePicker(EditText targetEditText) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dateDialog = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            TimePickerDialog timeDialog = new TimePickerDialog(getContext(), (view1, hourOfDay, minute) -> {
                String formatted = String.format(Locale.getDefault(), "%04d-%02d-%02d %02d:%02d", year, month + 1, dayOfMonth, hourOfDay, minute);
                targetEditText.setText(formatted);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            timeDialog.show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dateDialog.show();
    }


    private void loadRideHistory() {
        ApiService apiService = ApiClient.getClient(getContext()).create(ApiService.class);
        apiService.getRideHistory(EMPLOYEE_ID).enqueue(new Callback<RideHistoryResponse>() {
            @Override
            public void onResponse(Call<RideHistoryResponse> call, Response<RideHistoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.e("TAG", response.body().toString());

                    // Combine driverRides and participantRides into one list.
                    RideHistoryResponse history = response.body();
                    Log.e("TAG", "Response:" + response.body().toString());
                    List<OfferedRide> allRides = new ArrayList<>();
                    if (history.getDriverRides() != null) {
                        allRides.addAll(history.getDriverRides());
                    }
                    if (history.getParticipantRides() != null) {
                        allRides.addAll(history.getParticipantRides());
                    }

                    if (allRides.isEmpty()) {
                        Toast.makeText(getContext(), "No completed rides found", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("TAG", "Return Result" + allRides.toArray().toString());
                        adapter.setSelectable(false);
                        adapter.updateData(allRides);
                    }
                } else {
                    Toast.makeText(getContext(), "Error retrieving ride history", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RideHistoryResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to load ride history: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitOfferedRide(String employeeId, GeoLocation from, GeoLocation to, String departure_time, int no_of_riders) {
        RideOffer rideOffer = new RideOffer(employeeId, geoLocationFrom.getLatitude(), geoLocationFrom.getLongitude(),
                geoLocationTo.getLatitude(), geoLocationTo.getLongitude(), departure_time, 15, no_of_riders);

        ApiService api = ApiClient.getClient(getContext()).create(ApiService.class);
        api.offerRide(rideOffer).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Ride offered!", Toast.LENGTH_SHORT).show();
                    fetchCurrentRide(((MainActivity) getActivity()).getUser());
                } else {
                    Toast.makeText(getContext(), "Offer failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupInputFromAutocomplete(MaterialAutoCompleteTextView textView) {
        // Find the MaterialAutoCompleteTextView from your layout (ensure you use MaterialAutoCompleteTextView, not TextInputEditText)

        // Create an adapter with an empty list for suggestions
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
        textView.setAdapter(arrayAdapter);
        textView.setThreshold(1); // Start suggesting after one character

        // Listen for text changes to query autocomplete suggestions
        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    // Build a request to get autocomplete predictions
                    FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                            .setQuery(s.toString())
                            .setSessionToken(sessionToken)
                            .setTypeFilter(TypeFilter.ADDRESS)
                            .build();

                    placesClient.findAutocompletePredictions(request)
                            .addOnSuccessListener(response -> {
                                predictionList = response.getAutocompletePredictions();
                                List<String> suggestions = new ArrayList<>();
                                for (AutocompletePrediction prediction : predictionList) {
                                    suggestions.add(prediction.getFullText(null).toString());
                                }
                                arrayAdapter.clear();
                                arrayAdapter.addAll(suggestions);
                                arrayAdapter.notifyDataSetChanged();
                            })
                            .addOnFailureListener(exception -> {
                                Log.e("PlaceAutocomplete", "Prediction fetch error: " + exception.getMessage());
                            });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed here
            }
        });

        // Listen for item selection from suggestions
        textView.setOnItemClickListener((parent, view, position, id) -> {
            if (predictionList.size() > position) {
                String placeId = predictionList.get(position).getPlaceId();
                // Specify the fields to return (LAT_LNG, NAME, ADDRESS, etc.)
                List<Place.Field> placeFields = Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ADDRESS);
                FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.builder(placeId, placeFields).build();

                placesClient.fetchPlace(fetchPlaceRequest)
                        .addOnSuccessListener((FetchPlaceResponse fetchPlaceResponse) -> {
                            Place place = fetchPlaceResponse.getPlace();
                            if (place.getLatLng() != null) {

                                double latitude = place.getLatLng().latitude;
                                double longitude = place.getLatLng().longitude;

                                if (textView.equals(inputFrom)) {
                                    geoLocationFrom = new GeoLocation(latitude, longitude);
                                } else {
                                    geoLocationTo = new GeoLocation(latitude, longitude);
                                }

                                Log.d("PlaceAutocomplete", "Selected place: " + place.getName() +
                                        ", Lat: " + latitude + ", Lng: " + longitude);
                                // Use latitude and longitude for your server communication or further processing.
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e("PlaceAutocomplete", "Place fetch error: " + e.getMessage());
                            Toast.makeText(getContext(), "Place not found!", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }


}