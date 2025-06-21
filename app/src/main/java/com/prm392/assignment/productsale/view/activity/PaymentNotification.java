package com.prm392.assignment.productsale.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.prm392.assignment.productsale.R;

public class PaymentNotification extends AppCompatActivity {

    TextView txtNotification;
    Button btnBackHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_notification);

        txtNotification = findViewById(R.id.textViewNotify);

        Intent intent = getIntent();
        txtNotification.setText(intent.getStringExtra("result"));

        btnBackHome = findViewById(R.id.btnBackHome);
        btnBackHome.setOnClickListener((v) -> {
            Intent intent1 = new Intent(PaymentNotification.this, MainActivity.class);
            startActivity(intent1);
        });

    }
}