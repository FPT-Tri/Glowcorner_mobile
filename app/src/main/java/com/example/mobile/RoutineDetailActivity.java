package com.example.mobile;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile.Adapter.ProductAdapter;
import com.example.mobile.Api.ApiClient;
import com.example.mobile.Api.ApiService;
import com.example.mobile.Models.Product;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoutineDetailActivity extends AppCompatActivity {
    private static final String TAG = "RoutineDetailActivity";
    private TextView routineNameTextView;
    private TextView routineDescriptionTextView;
    private RecyclerView productsRecyclerView; // Correct ID to match layout
    private ProductAdapter productAdapter;
    private String routineId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_detail);

        routineId = getIntent().getStringExtra("routineID");
        if (routineId == null) {
            Toast.makeText(this, "Routine ID not provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        routineNameTextView = findViewById(R.id.routine_name);
        routineDescriptionTextView = findViewById(R.id.routine_description);
        productsRecyclerView = findViewById(R.id.products_recycler_view); // Changed from routines_recycler_view
        if (productsRecyclerView == null) {
            Log.e(TAG, "productsRecyclerView is null - check layout ID");
            Toast.makeText(this, "UI initialization failed", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        productsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new ProductAdapter(new ArrayList<>());
        productsRecyclerView.setAdapter(productAdapter);

        Log.d(TAG, "onCreate: Loading routine details for routineId: " + routineId);
        loadRoutineDetails();
    }

    private void loadRoutineDetails() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ResponseBody> call = apiService.getRoutineById(routineId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseString = response.body().string();
                        Log.d(TAG, "Routine Details Response: " + responseString);

                        JsonParser parser = new JsonParser();
                        JsonObject jsonObject = parser.parse(responseString).getAsJsonObject();
                        if (jsonObject.get("success").getAsBoolean() && jsonObject.get("status").getAsInt() == 200) {
                            JsonObject dataObject = jsonObject.getAsJsonObject("data");
                            String routineName = dataObject.get("routineName").getAsString();
                            String routineDescription = dataObject.get("routineDescription").getAsString();
                            JsonArray productArray = dataObject.getAsJsonArray("productDTOS");
                            List<Product> products = new ArrayList<>();
                            for (JsonElement productElement : productArray) {
                                JsonObject productObj = productElement.getAsJsonObject();
                                products.add(new Product(
                                        productObj.get("productID").getAsString(),
                                        productObj.get("productName").getAsString(),
                                        productObj.get("description").getAsString(),
                                        productObj.get("price").getAsDouble(),
                                        productObj.get("image_url").getAsString(),
                                        productObj.get("category").getAsString(),
                                        productObj.get("rating").getAsDouble()
                                ));
                            }

                            routineNameTextView.setText(routineName);
                            routineDescriptionTextView.setText(routineDescription);
                            productAdapter = new ProductAdapter(products);
                            productsRecyclerView.setAdapter(productAdapter);
                            Log.d(TAG, "Loaded routine: " + routineName + " with " + products.size() + " products");
                        } else {
                            Log.e(TAG, "API Error: " + jsonObject.get("description").getAsString());
                            Toast.makeText(RoutineDetailActivity.this, "Failed to load routine: " + jsonObject.get("description").getAsString(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Error parsing routine response: " + e.getMessage(), e);
                        Toast.makeText(RoutineDetailActivity.this, "Error loading routine details", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Response not successful. Status: " + response.code());
                    Toast.makeText(RoutineDetailActivity.this, "Failed to load routine. Status: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "API Call Failed for routine: " + t.getMessage(), t);
                Toast.makeText(RoutineDetailActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Activity destroyed");
    }
}