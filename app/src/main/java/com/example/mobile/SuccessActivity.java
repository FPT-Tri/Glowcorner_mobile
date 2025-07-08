package com.example.mobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile.Adapter.OrderDetailsSuccessAdapter;
import com.example.mobile.Api.ApiClient;
import com.example.mobile.Api.ApiService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SuccessActivity extends AppCompatActivity {
    private TextView orderIdTextView, customerNameTextView, orderDateTextView, totalAmountTextView;
    private RecyclerView orderDetailsRecyclerView;
    private OrderDetailsSuccessAdapter adapter;
    private Button backToHomeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        orderIdTextView = findViewById(R.id.order_id);
        customerNameTextView = findViewById(R.id.customer_name);
        orderDateTextView = findViewById(R.id.order_date);
        totalAmountTextView = findViewById(R.id.total_amount);
        orderDetailsRecyclerView = findViewById(R.id.order_details_recycler_view);
        backToHomeButton = findViewById(R.id.back_to_home_button);

        orderDetailsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderDetailsSuccessAdapter();
        orderDetailsRecyclerView.setAdapter(adapter);

        backToHomeButton.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
            String orderID = prefs.getString("orderID", null);
            if (orderID == null) {
                Toast.makeText(SuccessActivity.this, "Order ID not found.", Toast.LENGTH_SHORT).show();
                return;
            }

            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<ResponseBody> call = apiService.updateOrderStatus(orderID, "COMPLETED");
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response.isSuccessful()) {
                            Toast.makeText(SuccessActivity.this, "Order status updated to COMPLETED!", Toast.LENGTH_SHORT).show();
                            // Clear orderID from SharedPreferences
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.remove("orderID");
                            editor.apply();
                            // Navigate to HomeActivity
                            Intent intent = new Intent(SuccessActivity.this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(SuccessActivity.this, "Failed to update order status.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(SuccessActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(SuccessActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        loadOrderDetails();
    }

    private void loadOrderDetails() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String orderID = prefs.getString("orderID", null);
        if (orderID == null) {
            Toast.makeText(this, "Error: Order ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ResponseBody> call = apiService.getOrderDetails(orderID);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        String responseString = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseString);
                        JSONObject data = jsonObject.getJSONObject("data");

                        String orderID = data.getString("orderID");
                        String customerName = data.getString("customerName");
                        String orderDate = data.getString("orderDate");
                        double totalAmount = data.getDouble("totalAmount");

                        orderIdTextView.setText("Order ID: " + orderID);
                        customerNameTextView.setText("Customer: " + customerName);
                        orderDateTextView.setText("Order Date: " + orderDate);
                        totalAmountTextView.setText("Total Amount: $" + totalAmount);

                        JSONArray orderDetailsArray = data.getJSONArray("orderDetails");
                        List<OrderDetailsSuccessAdapter.OrderDetail> orderDetails = new ArrayList<>();
                        for (int i = 0; i < orderDetailsArray.length(); i++) {
                            JSONObject detail = orderDetailsArray.getJSONObject(i);
                            OrderDetailsSuccessAdapter.OrderDetail orderDetail = new OrderDetailsSuccessAdapter.OrderDetail(
                                    detail.getString("productName"),
                                    detail.getInt("quantity"),
                                    detail.getDouble("productPrice"),
                                    detail.getDouble("totalAmount")
                            );
                            orderDetails.add(orderDetail);
                        }

                        adapter.setOrderDetails(orderDetails);
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(SuccessActivity.this, "Failed to load order details", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(SuccessActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(SuccessActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}