package com.fosterx.crewride;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fosterx.crewride.network.ApiClient;
import com.fosterx.crewride.network.ApiService;
import com.fosterx.crewride.obj.User;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private MaterialButton loginBtn;
    private Button registerBtn;
    private ProgressBar progressBar;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.input_email);
        passwordInput = findViewById(R.id.input_password);
        loginBtn = findViewById(R.id.btn_login);
        registerBtn = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.login_progress);

        apiService = ApiClient.getClient(this).create(ApiService.class);

        loginBtn.setOnClickListener(v -> loginUser());
        registerBtn.setOnClickListener(v -> {
            // Optional: redirect to registration screen or call register API
            Toast.makeText(this, "Register clicked", Toast.LENGTH_SHORT).show();
        });
    }

    private void loginUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email and password are required", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        loginBtn.setEnabled(false);

        ApiService.LoginRequest loginRequest = new ApiService.LoginRequest();
        loginRequest.email = email;
        loginRequest.password = password;

        apiService.login(loginRequest).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                progressBar.setVisibility(View.GONE);
                loginBtn.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                    User user = response.body();

                    Intent mainActivity = new Intent(LoginActivity.this, MainActivity.class);
                    mainActivity.putExtra("user", user);
                    startActivity(mainActivity);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                loginBtn.setEnabled(true);
                Toast.makeText(LoginActivity.this, "Login failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}