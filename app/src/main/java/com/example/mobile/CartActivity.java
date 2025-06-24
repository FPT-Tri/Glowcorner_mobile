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
import com.example.mobile.Models.OrderResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    private TextView addressWarningTextView;
    private Button enterInformationButton;
    private Button checkoutButton;

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
        addressWarningTextView = findViewById(R.id.address_warning);
        enterInformationButton = findViewById(R.id.btn_enter_information);
        checkoutButton = findViewById(R.id.btn_checkout);

        // Setup navigation
        NavigationManager.setupNavigation(this, findViewById(R.id.bottom_navigation));

        // Load user address from API
        loadUserAddressFromApi();

        // Set up button listeners
        enterInformationButton.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        checkoutButton.setOnClickListener(v -> handleCheckout());

        loadCartItems();
    }

    private void loadUserAddressFromApi() {
        String userId = SignInActivity.getStoredValue(this, "userID");
        if (userId == null) {
            Toast.makeText(this, "User not logged in. Please log in.", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ResponseBody> call = apiService.getUserProfile(userId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseString = response.body().string();
                        Log.d(TAG, "User Profile Response: " + responseString);

                        JSONObject jsonObject = new JSONObject(responseString);
                        if (jsonObject.getBoolean("success") && jsonObject.getInt("status") == 200) {
                            JSONObject dataObject = new JSONObject(responseString).getJSONObject("data");
                            String userAddress = dataObject.has("address") && !dataObject.isNull("address")
                                    ? dataObject.getString("address") : "Address not available";

                            // Display address or prompt
                            if (userAddress != null && !userAddress.equals("Address not available")) {
                                addressWarningTextView.setText("Delivery Address: " + userAddress);
                                addressWarningTextView.setVisibility(View.VISIBLE);
                            } else {
                                addressWarningTextView.setText("Please add your address before proceeding to checkout.");
                                addressWarningTextView.setVisibility(View.VISIBLE);
                            }
                        } else {
                            addressWarningTextView.setText("Failed to load address: " + jsonObject.getString("description"));
                            addressWarningTextView.setVisibility(View.VISIBLE);
                        }
                    } catch (IOException | JSONException e) {
                        Log.e(TAG, "Error parsing profile response: " + e.getMessage());
                        addressWarningTextView.setText("Error loading address.");
                        addressWarningTextView.setVisibility(View.VISIBLE);
                    }
                } else {
                    addressWarningTextView.setText("Failed to load address. Status: " + response.code());
                    addressWarningTextView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "API Call Failed for profile: " + t.getMessage(), t);
                addressWarningTextView.setText("Network error: " + t.getMessage());
                addressWarningTextView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void handleCheckout() {
        String userID = SignInActivity.getStoredValue(this, "userID");
        if (userID == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        double totalAmount = Double.parseDouble(totalAmountTextView.getText().toString().replace("Total Amount: $", "").trim());
        double discountedTotalAmount = Double.parseDouble(discountedTotalAmountTextView.getText().toString().replace("Discounted Total: $", "").trim());

        if (totalAmount <= 0) {
            Toast.makeText(this, "Invalid total amount", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare order data
        JsonObject orderData = new JsonObject();
        orderData.addProperty("customerID", userID);
        orderData.addProperty("orderDate", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        orderData.addProperty("status", "PENDING");
        orderData.addProperty("totalAmount", totalAmount);
        orderData.addProperty("discountedTotalAmount", discountedTotalAmount);

        // Call createOrder API
        String jwtToken = SignInActivity.getStoredValue(this, "jwtToken");
        if (jwtToken == null) {
            Toast.makeText(this, "Authentication token not found", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<OrderResponse> call = apiService.createOrder("Bearer " + jwtToken, userID, orderData);
        call.enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    OrderResponse orderResponse = response.body();
                    if (orderResponse.isSuccess()) {
                        Toast.makeText(CartActivity.this, "Order created successfully!", Toast.LENGTH_SHORT).show();
                        String paymentIntentId = orderResponse.getData().getPaymentIntentId();

                        if (paymentIntentId != null && !paymentIntentId.isEmpty()) {
                            // Proceed to CheckoutActivity with payment intent
                            Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
                            intent.putExtra("userID", userID);
                            intent.putExtra("totalAmount", totalAmount);
                            intent.putExtra("discountedTotalAmount", discountedTotalAmount);
                            intent.putExtra("paymentIntentClientSecret", paymentIntentId);
                            startActivity(intent);
                        } else {
                            Toast.makeText(CartActivity.this, "No payment intent created. Creating a new one.", Toast.LENGTH_SHORT).show();
                            createStripePaymentIntent(totalAmount * 100); // Convert to cents
                        }
                    } else {
                        Toast.makeText(CartActivity.this, "Failed to create order: " + orderResponse.getDescription(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CartActivity.this, "Failed to create order", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                Log.e(TAG, "API Call Failed for createOrder: " + t.getMessage(), t);
                Toast.makeText(CartActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createStripePaymentIntent(double amount) {
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

                        JSONObject jsonObject = new JSONObject(responseString);
                        if (jsonObject.getBoolean("success")) {
                            JSONObject data = jsonObject.getJSONObject("data");
                            String paymentIntentClientSecret = data.getString("clientSecret");

                            Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
                            intent.putExtra("userID", SignInActivity.getStoredValue(CartActivity.this, "userID"));
                            intent.putExtra("totalAmount", Double.parseDouble(totalAmountTextView.getText().toString().replace("Total Amount: $", "").trim()));
                            intent.putExtra("discountedTotalAmount", Double.parseDouble(discountedTotalAmountTextView.getText().toString().replace("Discounted Total: $", "").trim()));
                            intent.putExtra("paymentIntentClientSecret", paymentIntentClientSecret);
                            startActivity(intent);
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
                Log.e(TAG, "API Call Failed for createStripePaymentIntent: " + t.getMessage(), t);
                Toast.makeText(CartActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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