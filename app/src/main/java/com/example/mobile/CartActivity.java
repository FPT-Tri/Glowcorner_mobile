package com.example.mobile;

import android.content.Intent;
import android.content.SharedPreferences;
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

        recyclerView = findViewById(R.id.cart_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartAdapter();
        recyclerView.setAdapter(adapter);

        totalAmountTextView = findViewById(R.id.total_amount);
        discountedTotalAmountTextView = findViewById(R.id.discounted_total_amount);
        addressWarningTextView = findViewById(R.id.address_warning);
        enterInformationButton = findViewById(R.id.btn_enter_information);
        checkoutButton = findViewById(R.id.btn_checkout);

        NavigationManager.setupNavigation(this, findViewById(R.id.bottom_navigation));

        loadUserAddressFromApi();

        enterInformationButton.setOnClickListener(v -> {
            startActivity(new Intent(CartActivity.this, ProfileActivity.class));
        });

        checkoutButton.setOnClickListener(v -> handleCheckout());

        loadCartItems();
    }

    private void loadUserAddressFromApi() {
        String userId = SignInActivity.getStoredValue(this, "userID");
        if (userId == null) return;

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ResponseBody> call = apiService.getUserProfile(userId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        String responseString = response.body().string();
                        org.json.JSONObject jsonObject = new org.json.JSONObject(responseString);
                        String address = jsonObject.getJSONObject("data").optString("address", "");
                        addressWarningTextView.setText(address.isEmpty() ? "Please add your address." : "Address: " + address);
                    } else {
                        addressWarningTextView.setText("Failed to load address");
                    }
                } catch (Exception e) {
                    addressWarningTextView.setText("Error loading address");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                addressWarningTextView.setText("Network error: " + t.getMessage());
            }
        });
    }

    private void handleCheckout() {
        String userID = SignInActivity.getStoredValue(this, "userID");
        String jwtToken = SignInActivity.getStoredValue(this, "jwtToken");
        if (userID == null || jwtToken == null) return;

        double totalAmount = Double.parseDouble(totalAmountTextView.getText().toString().replace("Total Amount: $", "").trim());
        double discountedTotalAmount = Double.parseDouble(discountedTotalAmountTextView.getText().toString().replace("Discounted Total: $", "").trim());

        JsonObject orderData = new JsonObject();
        orderData.addProperty("customerID", userID);
        orderData.addProperty("orderDate", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        orderData.addProperty("status", "PENDING");
        orderData.addProperty("totalAmount", totalAmount);
        orderData.addProperty("discountedTotalAmount", discountedTotalAmount);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<OrderResponse> call = apiService.createOrder("Bearer " + jwtToken, userID, orderData);
        call.enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    String clientSecret = response.body().getData().getPaymentIntentId();
                    String orderID = response.body().getData().getOrderID();
                    // Store orderID in SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("orderID", orderID);
                    editor.apply();

                    Log.d("CartActivity", "Received clientSecret = " + clientSecret);
                    Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
                    intent.putExtra("clientSecret", clientSecret);
                    intent.putExtra("totalAmount", totalAmount);
                    intent.putExtra("discountedAmount", discountedTotalAmount);
                    startActivity(intent);
                } else {
                    Toast.makeText(CartActivity.this, "Order creation failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                Toast.makeText(CartActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCartItems() {
        String userID = SignInActivity.getStoredValue(this, "userID");
        if (userID == null) return;

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<CartResponse> call = apiService.getCart(userID);

        call.enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.body() != null) {
                    List<CartResponse.Item> items = response.body().getItems();
                    adapter.setItems(items);
                    adapter.notifyDataSetChanged();
                    totalAmountTextView.setText("Total Amount: $" + response.body().getTotalAmount());
                    discountedTotalAmountTextView.setText("Discounted Total: $" + response.body().getDiscountedTotalAmount());
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                Toast.makeText(CartActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}