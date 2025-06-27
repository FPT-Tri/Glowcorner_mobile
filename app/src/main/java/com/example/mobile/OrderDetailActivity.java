package com.example.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile.Adapter.OrderDetailAdapter;
import com.example.mobile.Api.ApiClient;
import com.example.mobile.Api.ApiService;
import com.example.mobile.Models.OrderDetail;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity {
    private static final String TAG = "OrderDetailActivity";
    private TextView orderIdTextView, orderDateTextView, customerNameTextView, totalAmountTextView, statusTextView;
    private RecyclerView orderDetailsRecyclerView;
    private OrderDetailAdapter orderDetailAdapter;
    private List<OrderDetail> orderDetailsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        // Initialize UI components
        orderIdTextView = findViewById(R.id.order_id_detail);
        orderDateTextView = findViewById(R.id.order_date_detail);
        customerNameTextView = findViewById(R.id.order_customer_name_detail);
        totalAmountTextView = findViewById(R.id.order_total_amount_detail);
        statusTextView = findViewById(R.id.order_status_detail);
        orderDetailsRecyclerView = findViewById(R.id.order_details_recycler_view);

        // Initialize RecyclerView
        orderDetailsList = new ArrayList<>();
        orderDetailAdapter = new OrderDetailAdapter(this, orderDetailsList);
        orderDetailsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderDetailsRecyclerView.setAdapter(orderDetailAdapter);

        // Get orderId from Intent
        Intent intent = getIntent();
        String orderId = intent.getStringExtra("orderId");
        if (orderId != null) {
            loadOrderDetails(orderId);
        } else {
            Toast.makeText(this, "Order ID not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadOrderDetails(String orderId) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ResponseBody> call = apiService.getOrderDetails(orderId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseString = response.body().string();
                        Log.d(TAG, "Order Details Response: " + responseString);

                        JSONObject jsonObject = new JSONObject(responseString);
                        if (jsonObject.getBoolean("success") && jsonObject.getInt("status") == 200) {
                            JsonParser parser = new JsonParser();
                            JsonObject rootObj = parser.parse(responseString).getAsJsonObject();
                            JsonObject dataObject = rootObj.getAsJsonObject("data");

                            // Set order information
                            orderIdTextView.setText(dataObject.get("orderID").getAsString());
                            orderDateTextView.setText(dataObject.get("orderDate").getAsString());
                            customerNameTextView.setText(dataObject.get("customerName").getAsString());
                            totalAmountTextView.setText("$" + dataObject.get("totalAmount").getAsDouble());
                            statusTextView.setText(dataObject.get("status").getAsString());

                            // Set order details
                            orderDetailsList.clear();
                            JsonArray detailsArray = dataObject.getAsJsonArray("orderDetails");
                            for (JsonElement element : detailsArray) {
                                JsonObject detailObj = element.getAsJsonObject();
                                orderDetailsList.add(new OrderDetail(
                                        detailObj.get("orderID").getAsString(),
                                        detailObj.get("productID").getAsString(),
                                        detailObj.get("productName").getAsString(),
                                        detailObj.get("quantity").getAsInt(),
                                        detailObj.get("productPrice").getAsDouble(),
                                        detailObj.get("discountName").isJsonNull() ? null : detailObj.get("discountName").getAsString(),
                                        detailObj.get("discountPercentage").isJsonNull() ? null : detailObj.get("discountPercentage").getAsDouble(),
                                        detailObj.get("totalAmount").getAsDouble(),
                                        detailObj.get("discountedTotalAmount").isJsonNull() ? null : detailObj.get("discountedTotalAmount").getAsDouble()
                                ));
                            }
                            orderDetailAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(OrderDetailActivity.this, "Failed to load order details: " + jsonObject.getString("description"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException | JSONException e) {
                        Log.e(TAG, "Error parsing order details response: " + e.getMessage());
                        Toast.makeText(OrderDetailActivity.this, "Error reading order data", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(OrderDetailActivity.this, "Failed to load order details. Status: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "API Call Failed for order details: " + t.getMessage(), t);
                Toast.makeText(OrderDetailActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
