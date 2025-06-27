package com.example.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class CheckoutActivity extends AppCompatActivity {
    private static final String TAG = "CheckoutActivity";
    private String clientSecret;
    private double totalAmount;
    private double discountedAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        Button payButton = findViewById(R.id.payButton);

        // Retrieve data from intent
        Intent intent = getIntent();
        clientSecret = intent.getStringExtra("clientSecret");
        totalAmount = intent.getDoubleExtra("totalAmount", 0.0);
        discountedAmount = intent.getDoubleExtra("discountedAmount", 0.0);

        Log.d(TAG, "Received clientSecret: " + clientSecret);

        // Navigate to SuccessActivity immediately on pay button click
        payButton.setOnClickListener(v -> {
            Log.d(TAG, "Pay button clicked, navigating to SuccessActivity");
            Intent successIntent = new Intent(CheckoutActivity.this, SuccessActivity.class);
            successIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(successIntent);
            finish(); // Close CheckoutActivity
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("clientSecret", clientSecret);
        outState.putDouble("totalAmount", totalAmount);
        outState.putDouble("discountedAmount", discountedAmount);
        Log.d(TAG, "Saving instance state");
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        clientSecret = savedInstanceState.getString("clientSecret");
        totalAmount = savedInstanceState.getDouble("totalAmount");
        discountedAmount = savedInstanceState.getDouble("discountedAmount");
        Log.d(TAG, "Restoring instance state");
    }
}