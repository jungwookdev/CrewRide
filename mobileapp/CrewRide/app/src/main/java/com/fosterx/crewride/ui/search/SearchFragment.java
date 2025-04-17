package com.fosterx.crewride.ui.search;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fosterx.crewride.MainActivity;
import com.fosterx.crewride.R;
import com.fosterx.crewride.network.ApiClient;
import com.fosterx.crewride.network.ApiService;
import com.fosterx.crewride.obj.GeoLocation;
import com.fosterx.crewride.obj.OfferedRide;
import com.fosterx.crewride.obj.OfferedRideResponse;
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
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {
    private TextInputEditText datetimeInput;
    private MaterialAutoCompleteTextView inputFrom, inputTo;
    private MaterialButton btnSearch;

    private String mapsKey;
    private PlacesClient placesClient;
    private AutocompleteSessionToken sessionToken;
    private List<AutocompletePrediction> predictionList = new ArrayList<>();


    private RecyclerView resultRecycler;
    private OfferedRideAdapter adapter;
    private List<OfferedRide> rideList = new ArrayList<>();

    private GeoLocation geoLocationFrom = null;
    private GeoLocation geoLocationTo = null;

    private String datetime = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

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

        inputFrom = view.findViewById(R.id.input_from);
        inputFrom.setText("");

        inputTo = view.findViewById(R.id.input_to);
        inputTo.setText("");

        setupInputFromAutocomplete(inputFrom);
        setupInputFromAutocomplete(inputTo);

        datetimeInput = view.findViewById(R.id.input_datetime);
        datetimeInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDatePicker();
                } else {
                    Log.d("Focus", "TextInputEditText lost focus");
                }
            }
        });

        Button searchBtn = view.findViewById(R.id.btn_search);
        searchBtn.setOnClickListener(v -> {
            // Hide the keyboard if it's visible
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }

            if (geoLocationFrom != null && geoLocationTo != null && datetime != null) {
                // Initialize RecyclerView and adapter
                resultRecycler = view.findViewById(R.id.search_results_recycler);
                resultRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
                adapter = new OfferedRideAdapter(getContext(), rideList, geoLocationFrom, datetime);
                resultRecycler.setAdapter(adapter);

                ApiService apiService = ApiClient.getClient(getContext()).create(ApiService.class);
                apiService.searchRides(((MainActivity) getActivity()).getUser().getEmployeeId(),
                                geoLocationFrom.getLatitude(),
                                geoLocationFrom.getLongitude(),
                                geoLocationTo.getLatitude(),
                                geoLocationTo.getLongitude(),
                                datetime)
                        .enqueue(new Callback<OfferedRideResponse>() {
                            @Override
                            public void onResponse(Call<OfferedRideResponse> call, Response<OfferedRideResponse> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    Log.e("TAG", response.body().toString());

                                    List<OfferedRide> rides = response.body().getRides();
                                    if (rides != null && !rides.isEmpty()) {
                                        adapter.setSelectable(true);
                                        adapter.updateData(rides);
                                    } else {
                                        Toast.makeText(getContext(), "No rides found", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getContext(), "Error retrieving rides", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<OfferedRideResponse> call, Throwable t) {
                                Log.e("SearchFragment", "Error: " + t.getMessage());
                                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(getContext(), "Please provide From, To, and Date/Time", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
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

    private void showDatePicker() {
        // Build the MaterialDatePicker instance
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .build();

        // Show the date picker
        datePicker.show(requireActivity().getSupportFragmentManager(), "DATE_PICKER");
        datePicker.addOnPositiveButtonClickListener(selection -> {
            // Create a Calendar from the selected date
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(selection);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            // No need to pre-set the time; let the time picker do that.
            showTimePicker(year, month, day);
        });
    }

    private void showTimePicker(int year, int month, int day) {
        // Build the MaterialTimePicker instance (you can set default hour/minute if desired)
        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTitleText("Select Time")
                // Choose either CLOCK_24H or CLOCK_12H per your preference
                .setTimeFormat(TimeFormat.CLOCK_24H)
                // Optionally, set default time here:
                .setHour(23)
                .setMinute(47)
                .build();

        timePicker.show(requireActivity().getSupportFragmentManager(), "TIME_PICKER");

        timePicker.addOnPositiveButtonClickListener(dialog -> {
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();

            // Create a calendar with the combined date and time
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day, hour, minute, 0);
            // TODO: Investigate why the selected date from the picker is not correctly displayed on the demo device. Should verify the behavior on additional devices.
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            // Format to a user-friendly display
            SimpleDateFormat displayFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            String displayDateTime = displayFormat.format(calendar.getTime());
            datetimeInput.setText(displayDateTime);

            // Create an ISO 8601 string that preserves the local hour and minute
            // (Note: The literal 'Z' indicates UTC, so if your server expects the same hour/minute, use the local timezone)
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            isoFormat.setTimeZone(TimeZone.getDefault());  // use the system default to keep local time values
            datetime = isoFormat.format(calendar.getTime());
        });
    }
}