package com.example.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

public class CheckoutActivity extends AppCompatActivity {
    private static final String TAG = "CheckoutActivity";
    private PaymentSheet paymentSheet;
    private String paymentIntentClientSecret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Configure Stripe with test publishable key
        PaymentConfiguration.init(this, "pk_test_51R4HLJ4a2fYGaT9ntHjY5Bm02V5TDbxj0TCjxJQTXUTxKcaeDu8EMW374Zkr1AZKMaUHOdJWktcFpyapHpDLzeko00g99ZbkhB");

        // Get data from Intent
        Intent intent = getIntent();
        paymentIntentClientSecret = intent.getStringExtra("paymentIntentClientSecret");
        String userID = intent.getStringExtra("userID");
        double totalAmount = intent.getDoubleExtra("totalAmount", 0.0);
        double discountedTotalAmount = intent.getDoubleExtra("discountedTotalAmount", 0.0);

        if (paymentIntentClientSecret == null || paymentIntentClientSecret.isEmpty()) {
            Toast.makeText(this, "Payment intent not initialized", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (userID == null || totalAmount <= 0) {
            Toast.makeText(this, "Invalid user or amount data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Present payment sheet
        presentPaymentSheet();
    }

    private void presentPaymentSheet() {
        paymentSheet = new PaymentSheet(this, paymentSheetResult -> {
            if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
                Toast.makeText(CheckoutActivity.this, "Payment successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CheckoutActivity.this, SuccessActivity.class);
                startActivity(intent);
                finish();
            } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
                Toast.makeText(CheckoutActivity.this, "Payment failed: " + ((PaymentSheetResult.Failed) paymentSheetResult).getError().getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            } else if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
                Toast.makeText(CheckoutActivity.this, "Payment canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        try {
            paymentSheet.presentWithPaymentIntent(paymentIntentClientSecret, new PaymentSheet.Configuration.Builder("Your Store")
                    .build());
        } catch (Exception e) {
            Log.e(TAG, "Error presenting PaymentSheet: " + e.getMessage());
            Toast.makeText(this, "Error during checkout: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}