package com.example.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile.Adapter.ProductAdapter;
import com.example.mobile.Api.ApiClient;
import com.example.mobile.Api.ApiService;
import com.example.mobile.Models.Product;
import com.example.mobile.Models.ProductResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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

        // Set up BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_sign_in) {
                Log.d(TAG, "Navigating to SignInActivity");
                Intent intent = new Intent(HomeActivity.this, SignInActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_cart) {
                Log.d(TAG, "Navigating to CartActivity");
                Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });

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