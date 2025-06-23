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
    private TextView userIdTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Khởi tạo UI
        initViews();

        // Cài đặt navigation và dropdown
        setupNavigation();

        // Load dữ liệu sản phẩm
        loadProducts();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view);
        userIdTextView = findViewById(R.id.userIdTextView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductAdapter(null);
        recyclerView.setAdapter(adapter);

        // Hiển thị user ID từ SharedPreferences
        String userID = SignInActivity.getStoredValue(this, "userID");
        userIdTextView.setText(userID != null ? "User ID: " + userID : "User ID: Not logged in");
    }

    private void setupNavigation() {
        NavigationManager.setupNavigation(this, findViewById(R.id.bottom_navigation));
        new DropdownMenuManager(this);
    }

    private void loadProducts() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ProductResponse> call = apiService.getProducts();

        Log.d(TAG, "Request URL: " + call.request().url());

        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body().getData();
                    if (products != null && !products.isEmpty()) {
                        adapter = new ProductAdapter(products);
                        recyclerView.setAdapter(adapter);
                    } else {
                        Toast.makeText(HomeActivity.this, "No products available", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Response unsuccessful: " + response.message());
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
