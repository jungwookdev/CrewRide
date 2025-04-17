package com.fosterx.crewride.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.fosterx.crewride.MainActivity;
import com.fosterx.crewride.R;
import com.fosterx.crewride.obj.User;
import com.google.android.material.button.MaterialButton;


public class ProfileFragment extends Fragment {

    private ImageView profilePic;
    private TextView profileName, profileEmail;
    private MaterialButton btnEditProfile, btnSettings, btnLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Bind views from the layout
        profilePic = view.findViewById(R.id.profile_pic);
        profileName = view.findViewById(R.id.profile_name);
        profileEmail = view.findViewById(R.id.profile_email);

        btnEditProfile = view.findViewById(R.id.btn_edit_profile);
        btnSettings = view.findViewById(R.id.btn_settings);
        btnLogout = view.findViewById(R.id.btn_logout);

        // Retrieve a UserProfile from the MainActivity or another source
        User user = ((MainActivity) requireActivity()).getUser();
        if (user != null) {
            profileName.setText(user.getName());
            profileEmail.setText(user.getEmail());
            // Use Glide to load the profile picture with a circular crop.
            Glide.with(requireContext())
                    .load(user.getHeadshotUrl()) // Ensure your UserProfile model has a headshotUrl property.
                    .placeholder(R.drawable.ic_profile)
                    .circleCrop()
                    .into(profilePic);
        } else {
            profileName.setText("Unknown User");
            profileEmail.setText("No email available");
        }

        // Set up button click listeners (implement navigation or actions as desired)
        btnEditProfile.setOnClickListener(v -> {
            // TODO: Navigate to edit profile screen
        });

        btnSettings.setOnClickListener(v -> {
            // TODO: Navigate to settings screen
        });
        btnLogout.setOnClickListener(v -> {
            // TODO: Implement logout functionality
        });

        return view;
    }
}