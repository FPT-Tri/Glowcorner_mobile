package com.example.mobile;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile.Adapter.HomeProductAdapter;
import com.example.mobile.Api.ApiClient;
import com.example.mobile.Api.ApiService;
import com.example.mobile.Models.Product;
import com.example.mobile.Models.ProductResponse;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";

    private RecyclerView recyclerView;
    private HomeProductAdapter adapter;
    private TextView userIdTextView;
    private Spinner skinTypeSpinner;
    private Spinner categorySpinner;
    private EditText searchEditText;
    private Button searchButton;

    // Predefined lists for dropdowns
    private final List<String> skinTypes = Arrays.asList("All", "Dry", "Oily", "Combination", "Sensitive");
    private final List<String> categories = Arrays.asList("All", "Cleanser", "Serum", "Moisturizer");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize UI
        initViews();

        // Setup navigation and dropdown
        setupNavigation();

        // Setup spinners
        setupSpinners();

        // Setup search button
        setupSearch();

        // Load all products initially
        loadProducts();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view);
        userIdTextView = findViewById(R.id.userIdTextView);
        skinTypeSpinner = findViewById(R.id.skin_type_spinner);
        categorySpinner = findViewById(R.id.category_spinner);
        searchEditText = findViewById(R.id.search_edit_text);
        searchButton = findViewById(R.id.search_button);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HomeProductAdapter(null);
        recyclerView.setAdapter(adapter);

        // Display user ID from SharedPreferences
        String userID = SignInActivity.getStoredValue(this, "userID");
        userIdTextView.setText(userID != null ? "User ID: " + userID : "User ID: Not logged in");
    }

    private void setupNavigation() {
        NavigationManager.setupNavigation(this, findViewById(R.id.bottom_navigation));
        new DropdownMenuManager(this);
    }

    private void setupSpinners() {
        // Setup skin type spinner
        ArrayAdapter<String> skinTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, skinTypes);
        skinTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        skinTypeSpinner.setAdapter(skinTypeAdapter);

        // Setup category spinner
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        // Spinner listeners
        skinTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedSkinType = skinTypes.get(position);
                String selectedCategory = categorySpinner.getSelectedItem().toString();
                loadFilteredProducts(selectedSkinType, selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = categories.get(position);
                String selectedSkinType = skinTypeSpinner.getSelectedItem().toString();
                loadFilteredProducts(selectedSkinType, selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupSearch() {
        searchButton.setOnClickListener(v -> {
            String query = searchEditText.getText().toString().trim();
            if (query.isEmpty()) {
                Toast.makeText(HomeActivity.this, "Please enter a search query", Toast.LENGTH_SHORT).show();
                loadProducts(); // Reset to all products
                return;
            }
            searchProduct(query);
        });
    }

    // Sửa lại giống ManagerProductsActivity: dùng updateData thay vì tạo adapter mới
    private void searchProduct(String query) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ProductResponse> call = query.matches("\\d+") ?
                apiService.getProductById(query) :
                apiService.getProductsByName(query);

        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Product> products = response.body().getData();
                    adapter.updateData(products);
                } else {
                    Toast.makeText(HomeActivity.this, "No products found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                Toast.makeText(HomeActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Sửa lại giống ManagerProductsActivity: dùng updateData thay vì tạo adapter mới
    private void loadFilteredProducts(String skinType, String category) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ProductResponse> call;

        if (skinType.equals("All") && category.equals("All")) {
            call = apiService.getProducts();
        } else if (!skinType.equals("All")) {
            call = apiService.getProductsBySkinType(skinType);
        } else {
            call = apiService.getProductsByCategory(category);
        }

        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    adapter.updateData(response.body().getData());
                } else {
                    Toast.makeText(HomeActivity.this, "No products available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                Toast.makeText(HomeActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProducts() {
        loadFilteredProducts("All", "All");
    }
}