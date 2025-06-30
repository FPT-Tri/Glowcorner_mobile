package com.example.mobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile.Api.ApiClient;
import com.example.mobile.Api.ApiResponse;
import com.example.mobile.Api.ApiService;
import com.example.mobile.Models.LoginRequest;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {
    private static final String TAG = "SignInActivity";
    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private TextView signUpTextView;

    // Static credentials for login
    private static final String STATIC_USERNAME = "user";
    private static final String STATIC_PASSWORD = "pass";

    // SharedPreferences key
    private static final String PREF_NAME = "UserPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate called");
        setContentView(R.layout.sign_in);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        signUpTextView = findViewById(R.id.signUpTextView);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(SignInActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                LoginRequest loginRequest = new LoginRequest(username, password);
                ApiService apiService = ApiClient.getClient().create(ApiService.class);
                Call<ApiResponse> call = apiService.login(loginRequest);

                long startTime = new Date().getTime();
                Log.d(TAG, "API Call Started at: " + startTime + " for username: " + username);

                call.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        long endTime = new Date().getTime();
                        Log.d(TAG, "API Call Ended at: " + endTime + ", Duration: " + (endTime - startTime) + " ms");
                        Log.d(TAG, "Response Code: " + response.code() + ", Body: " + (response.body() != null ? response.body().toString() : "null"));
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse apiResponse = response.body();
                            if (apiResponse.isSuccess()) {
                                Log.d(TAG, "Login Successful, Checking user role");

                                // Save user details to SharedPreferences
                                SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putInt("status", apiResponse.getStatus());
                                editor.putString("description", apiResponse.getDescription());
                                ApiResponse.Data data = apiResponse.getData();
                                String userRole = null;
                                if (data != null) {
                                    editor.putString("jwtToken", data.getJwtToken());
                                    editor.putString("userID", data.getUserID());
                                    userRole = data.getRole();
                                    editor.putString("userRole", userRole);
                                    editor.putString("userEmail", data.getEmail());
                                    editor.putString("fullName", data.getFullName());
                                    editor.putString("userAddress", "");
                                    editor.putString("userPhone", "");
                                }
                                editor.apply();

                                Toast.makeText(SignInActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

                                // Navigate based on userRole
                                Intent intent;
                                if ("CUSTOMER".equals(userRole)) {
                                    intent = new Intent(SignInActivity.this, HomeActivity.class);
                                } else if ("STAFF".equals(userRole)) {
                                    intent = new Intent(SignInActivity.this, StaffHomeActivity.class);
                                } else if ("MANAGER".equals(userRole)) {
                                    intent = new Intent(SignInActivity.this, ManagerHomeActivity.class);
                                } else {
                                    Log.w(TAG, "Unknown user role: " + userRole);
                                    Toast.makeText(SignInActivity.this, "Unknown user role", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                startActivity(intent);
                                finish();
                            } else {
                                Log.w(TAG, "Login Failed: " + apiResponse.getDescription());
                                Toast.makeText(SignInActivity.this, apiResponse.getDescription(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e(TAG, "Unsuccessful response: " + response.message());
                            Toast.makeText(SignInActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        long endTime = new Date().getTime();
                        Log.e(TAG, "API Call Failed at: " + endTime + ", Duration: " + (endTime - startTime) + " ms, Error: " + t.getMessage(), t);
                        Toast.makeText(SignInActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();

                        // Fallback to static check if API fails
                        if (username.equals(STATIC_USERNAME) && password.equals(STATIC_PASSWORD)) {
                            Log.d(TAG, "Login Successful with static credentials, Checking user role");
                            // Save static login details to SharedPreferences
                            SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putInt("status", 200);
                            editor.putString("description", "Login successful! (Static fallback)");
                            editor.putString("jwtToken", "dummy-jwt");
                            editor.putString("userID", "dummy-id");
                            // Static role for testing, assuming CUSTOMER for simplicity
                            String userRole = "CUSTOMER"; // Change to STAFF or MANAGER for testing
                            editor.putString("userRole", userRole);
                            editor.putString("userEmail", "user@example.com");
                            editor.putString("fullName", "Dummy User");
                            editor.putString("userAddress", "Dummy Address");
                            editor.putString("userPhone", "1234567890");
                            editor.apply();

                            Toast.makeText(SignInActivity.this, "Login successful! (Static fallback)", Toast.LENGTH_SHORT).show();

                            // Navigate based on userRole
                            Intent intent;
                            if ("CUSTOMER".equals(userRole)) {
                                intent = new Intent(SignInActivity.this, HomeActivity.class);
                            } else if ("STAFF".equals(userRole)) {
                                intent = new Intent(SignInActivity.this, StaffHomeActivity.class);
                            } else if ("MANAGER".equals(userRole)) {
                                intent = new Intent(SignInActivity.this, ManagerHomeActivity.class);
                            } else {
                                Log.w(TAG, "Unknown user role: " + userRole);
                                Toast.makeText(SignInActivity.this, "Unknown user role", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            startActivity(intent);
                            finish();
                        } else {
                            Log.w(TAG, "Login Failed: Invalid username or password (Static check)");
                            Toast.makeText(SignInActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Navigating to SignUpActivity");
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy called");
    }

    // Helper method to get stored value (can be used in other activities)
    public static String getStoredValue(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(key, null);
    }

    // Helper method to get stored int value
    public static int getStoredIntValue(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(key, -1);
    }
}