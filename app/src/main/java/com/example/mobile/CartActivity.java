package com.example.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile.Adapter.CartAdapter;
import com.example.mobile.Api.ApiClient;
import com.example.mobile.Api.ApiService;
import com.example.mobile.Models.CartResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity {
    private static final String TAG = "CartActivity";
    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private TextView totalAmountTextView;
    private TextView discountedTotalAmountTextView;
    private Button enterInformationButton;
    private Button checkoutButton;
    private PaymentSheet paymentSheet;
    private String paymentIntentClientSecret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Initialize views
        recyclerView = findViewById(R.id.cart_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartAdapter();
        recyclerView.setAdapter(adapter);

        totalAmountTextView = findViewById(R.id.total_amount);
        discountedTotalAmountTextView = findViewById(R.id.discounted_total_amount);
        enterInformationButton = findViewById(R.id.btn_enter_information);
        checkoutButton = findViewById(R.id.btn_checkout);

        // Setup navigation
        NavigationManager.setupNavigation(this, findViewById(R.id.bottom_navigation));

        // Configure Stripe with test publishable key
        PaymentConfiguration.init(this, "pk_test_51R4HLJ4a2fYGaT9ntHjY5Bm02V5TDbxj0TCjxJQTXUTxKcaeDu8EMW374Zkr1AZKMaUHOdJWktcFpyapHpDLzeko00g99ZbkhB");

        // Set up button listeners
        enterInformationButton.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        checkoutButton.setOnClickListener(v -> {
            // Get total amount from TextView
            String totalAmountStr = totalAmountTextView.getText().toString().replace("Total Amount: $", "").trim();
            double totalAmount = Double.parseDouble(totalAmountStr) * 100; // Convert to cents for Stripe

            // Call server to create PaymentIntent
            createStripePaymentIntent(totalAmount);
        });

        loadCartItems();
    }

    private void createStripePaymentIntent(double amount) {
        String userID = SignInActivity.getStoredValue(this, "userID");
        if (userID == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Retrieve JWT token from SharedPreferences
        String jwtToken = SignInActivity.getStoredValue(this, "jwtToken");
        if (jwtToken == null) {
            Toast.makeText(this, "Authentication token not found", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ResponseBody> call = apiService.createStripePaymentIntent("Bearer " + jwtToken, amount, "usd");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseString = response.body().string();
                        Log.d(TAG, "Stripe Payment Intent Response: " + responseString);

                        // Parse JSON response
                        JSONObject jsonObject = new JSONObject(responseString);
                        if (jsonObject.getBoolean("success")) {
                            JSONObject data = jsonObject.getJSONObject("data");
                            paymentIntentClientSecret = data.getString("clientSecret");
                            presentPaymentSheet();
                        } else {
                            Toast.makeText(CartActivity.this, "Failed to create payment intent: " + jsonObject.getString("description"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException | JSONException e) {
                        Log.e(TAG, "Error parsing response: " + e.getMessage());
                        Toast.makeText(CartActivity.this, "Error creating payment intent", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CartActivity.this, "Failed to create payment intent", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "API Call Failed: " + t.getMessage(), t);
                Toast.makeText(CartActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void presentPaymentSheet() {
        if (paymentIntentClientSecret == null || paymentIntentClientSecret.isEmpty()) {
            Toast.makeText(this, "Payment intent not initialized", Toast.LENGTH_SHORT).show();
            return;
        }

        paymentSheet = new PaymentSheet(this, paymentSheetResult -> {
            if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
                Toast.makeText(CartActivity.this, "Payment successful!", Toast.LENGTH_SHORT).show();
            } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
                Toast.makeText(CartActivity.this, "Payment failed: " + ((PaymentSheetResult.Failed) paymentSheetResult).getError().getMessage(), Toast.LENGTH_SHORT).show();
            } else if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
                Toast.makeText(CartActivity.this, "Payment canceled", Toast.LENGTH_SHORT).show();
            }
        });

        try {
            paymentSheet.presentWithPaymentIntent(paymentIntentClientSecret, new PaymentSheet.Configuration.Builder("Your Store")
                    .build());
        } catch (Exception e) {
            Log.e(TAG, "Error presenting PaymentSheet: " + e.getMessage());
            Toast.makeText(this, "Error during checkout: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadCartItems() {
        String userID = SignInActivity.getStoredValue(this, "userID");
        if (userID == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<CartResponse> call = apiService.getCart(userID);

        Log.d(TAG, "Request URL: " + call.request().url());

        call.enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                Log.d(TAG, "Response Code: " + response.code());
                if (response.body() != null) {
                    CartResponse cartResponse = response.body();
                    List<CartResponse.Item> items = cartResponse.getItems();
                    if (items != null) {
                        adapter.setItems(items);
                        adapter.notifyDataSetChanged();
                    }
                    totalAmountTextView.setText("Total Amount: $" + cartResponse.getTotalAmount());
                    discountedTotalAmountTextView.setText("Discounted Total: $" + (cartResponse.getDiscountedTotalAmount() != null ? cartResponse.getDiscountedTotalAmount() : 0));
                } else {
                    ResponseBody errorBody = response.errorBody();
                    if (errorBody != null) {
                        try {
                            Log.e(TAG, "Raw Response: " + errorBody.string());
                        } catch (IOException e) {
                            Log.e(TAG, "Error reading response body: " + e.getMessage());
                        }
                    }
                    Log.e(TAG, "Unsuccessful response: " + response.message());
                    Toast.makeText(CartActivity.this, "Failed to load cart", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                Log.e(TAG, "API Call Failed: " + t.getMessage(), t);
                Toast.makeText(CartActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}