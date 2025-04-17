package com.fosterx.crewride;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.fosterx.crewride.obj.User;
import com.fosterx.crewride.ui.home.HomeFragment;
import com.fosterx.crewride.ui.messages.MessagesFragment;
import com.fosterx.crewride.ui.profile.ProfileFragment;
import com.fosterx.crewride.ui.rides.RidesFragment;
import com.fosterx.crewride.ui.search.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private User user;



    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Retrieve the user object from the Intent
        user = (User) getIntent().getSerializableExtra("user");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(navListener);
        // Load default fragment
        loadFragment(new HomeFragment());
    }

    private final NavigationBarView.OnItemSelectedListener navListener = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            String title = "";

            if (item.getItemId() == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            }else if (item.getItemId() == R.id.nav_search) {
                selectedFragment = new SearchFragment();
            }else if (item.getItemId() == R.id.nav_rides) {
                selectedFragment = new RidesFragment();
            }else if (item.getItemId() == R.id.nav_messages) {
                selectedFragment = new MessagesFragment();
            }else if (item.getItemId() == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }
            loadFragment(selectedFragment);
            return true;
        }
    };


    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    public User getUser() {
        return user;
    }

    public BottomNavigationView getBottomNavigationView() {
        return bottomNavigationView;
    }
}
