package com.example.mobile.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mobile.Api.ApiClient;
import com.example.mobile.Api.ApiService;
import com.example.mobile.R;
import com.example.mobile.SignInActivity;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private static final String TAG = "ProductAdapter";
    private List<com.example.mobile.Models.Product> productList;
    private Context context;

    public ProductAdapter(List<com.example.mobile.Models.Product> productList) {
        this.productList = productList;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        context = parent.getContext();
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        com.example.mobile.Models.Product product = productList.get(position);
        holder.productIdTextView.setText(product.getProductID());
        holder.productNameTextView.setText(product.getProductName());
        holder.categoryTextView.setText(product.getCategory());
        holder.priceTextView.setText("$" + product.getPrice());
        holder.ratingTextView.setText(String.valueOf(product.getRating()));
        Glide.with(holder.itemView.getContext()).load(product.getImageUrl()).into(holder.productImageView);

        // Display skin types
        String skinTypes = String.join(", ", product.getSkinTypes());
        holder.skinTypesTextView.setText(skinTypes);

        // Set click listener for the "+" button
        holder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userID = SignInActivity.getStoredValue(context, "userID");
                String productID = product.getProductID();
                int quantity = 1;

                if (userID == null) {
                    Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show();
                    return;
                }

                ApiService apiService = ApiClient.getClient().create(ApiService.class);
                Call<ResponseBody> call = apiService.addToCart(userID, productID, quantity);

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            try {
                                String responseString = response.body().string();
                                Log.d(TAG, "Response String: " + responseString);
                                if ("success".equalsIgnoreCase(responseString) || responseString.contains("\"success\":true")) {
                                    Toast.makeText(context, "Added to cart successfully!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Failed to add to cart: " + responseString, Toast.LENGTH_SHORT).show();
                                }
                            } catch (IOException e) {
                                Log.e(TAG, "Error reading response: " + e.getMessage());
                                Toast.makeText(context, "Error adding to cart", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Log raw response body if available
                            ResponseBody errorBody = response.errorBody();
                            if (errorBody != null) {
                                try {
                                    String rawResponse = errorBody.string();
                                    Log.e(TAG, "Raw Response: " + rawResponse);
                                    Toast.makeText(context, "API Error: " + rawResponse, Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    Log.e(TAG, "Error reading response body: " + e.getMessage());
                                }
                            }
                            Toast.makeText(context, "Error adding to cart", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e(TAG, "API Call Failed: " + t.getMessage(), t);
                        if (t instanceof retrofit2.HttpException) {
                            retrofit2.HttpException httpException = (retrofit2.HttpException) t;
                            try {
                                String errorBody = httpException.response().errorBody().string();
                                Log.e(TAG, "Error Body: " + errorBody);
                                Toast.makeText(context, "API Error: " + errorBody, Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Log.e(TAG, "Error reading error body: " + e.getMessage());
                            }
                        } else {
                            Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        public TextView productIdTextView, productNameTextView, categoryTextView, priceTextView, ratingTextView, skinTypesTextView;
        public ImageView productImageView;
        public Button addButton;

        public ProductViewHolder(View itemView) {
            super(itemView);
            productIdTextView = itemView.findViewById(R.id.product_id);
            productNameTextView = itemView.findViewById(R.id.product_name);
            categoryTextView = itemView.findViewById(R.id.category);
            priceTextView = itemView.findViewById(R.id.price);
            ratingTextView = itemView.findViewById(R.id.rating);
            skinTypesTextView = itemView.findViewById(R.id.skin_types);
            productImageView = itemView.findViewById(R.id.product_image);
            addButton = itemView.findViewById(R.id.add_button);
        }
    }
}