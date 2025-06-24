package com.example.mobile;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SuccessActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        TextView successMessage = findViewById(R.id.success_message);
        successMessage.setText("Payment Successful! Thank you for your purchase.");
    }
}

