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
import com.example.mobile.Models.ProductResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    private RecyclerView recyclerView;
    private ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductAdapter(null);
        recyclerView.setAdapter(adapter);

        // Setup navigation using NavigationManager
        NavigationManager.setupNavigation(this, findViewById(R.id.bottom_navigation));

        // Display userID from SharedPreferences
        TextView userIdTextView = findViewById(R.id.userIdTextView);
        String userID = SignInActivity.getStoredValue(this, "userID");
        if (userID != null) {
            userIdTextView.setText("User ID: " + userID);
        } else {
            userIdTextView.setText("User ID: Not logged in");
        }

        loadProducts();
    }

    private void loadProducts() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ProductResponse> call = apiService.getProducts();

        Log.d(TAG, "Request URL: " + call.request().url());

        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                Log.d(TAG, "Response Code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    ProductResponse productResponse = response.body();
                    List<Product> products = productResponse.getData();
                    Log.d(TAG, "Response Data: " + (products != null ? products.toString() : "null"));
                    if (products != null) {
                        adapter = new ProductAdapter(products);
                        recyclerView.setAdapter(adapter);
                    } else {
                        Log.e(TAG, "No products in response");
                        Toast.makeText(HomeActivity.this, "No products available", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Unsuccessful response: " + response.message());
                    Toast.makeText(HomeActivity.this, "Failed to load products", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                Log.e(TAG, "API Call Failed: " + t.getMessage(), t);
                Toast.makeText(HomeActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}