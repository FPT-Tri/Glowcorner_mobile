package com.example.mobile.Adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile.Api.ApiClient;
import com.example.mobile.Api.ApiService;
import com.example.mobile.Models.CartResponse;
import com.example.mobile.R;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartResponse.Item> items;
    private Context context;
    private OnCartActionListener listener;

    // Interface for callback to activity
    public interface OnCartActionListener {
        void onQuantityUpdated();
        void onItemRemoved();
        void showToast(String message);
    }

    public CartAdapter(Context context, OnCartActionListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setItems(List<CartResponse.Item> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartResponse.Item item = items.get(position);
        holder.productNameTextView.setText(item.getProductName());
        holder.quantityTextView.setText("Qty: " + item.getQuantity());
        holder.totalAmountTextView.setText("Total: $" + (float) item.getTotalAmount() / 100);

        // Handle price display
        if (item.getDiscountedTotalAmount() != null) {
            // Has discount: display totalAmount with strikethrough in red and discounted price in red
            holder.priceTextView.setText("$" + (float) item.getTotalAmount() / 100);
            holder.priceTextView.setPaintFlags(holder.priceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.priceTextView.setTextColor(context.getResources().getColor(R.color.red));
            holder.discountedPriceTextView.setText("$" + (float) item.getDiscountedTotalAmount() / 100);
            holder.discountedPriceTextView.setVisibility(View.VISIBLE);
        } else {
            // No discount: display totalAmount in black without strikethrough
            holder.priceTextView.setText("$" + (float) item.getTotalAmount() / 100);
            holder.priceTextView.setPaintFlags(holder.priceTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.priceTextView.setTextColor(context.getResources().getColor(android.R.color.black));
            holder.discountedPriceTextView.setVisibility(View.GONE);
        }

        // Set up quantity update buttons
        holder.decrementButton.setOnClickListener(v -> updateQuantity(item.getProductID(), item.getQuantity() - 1));
        holder.incrementButton.setOnClickListener(v -> updateQuantity(item.getProductID(), item.getQuantity() + 1));
        holder.removeButton.setOnClickListener(v -> removeItem(item.getProductID()));
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    private void updateQuantity(String productID, int newQuantity) {
        if (newQuantity < 1) {
            listener.showToast("Quantity cannot be less than 1");
            return;
        }

        String userID = com.example.mobile.SignInActivity.getStoredValue(context, "userID");
        if (userID == null) {
            listener.showToast("User not logged in");
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ResponseBody> call = apiService.updateCartItem(userID, productID, newQuantity);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseString = response.body().string();
                        if ("success".equalsIgnoreCase(responseString) || responseString.contains("\"success\":true")) {
                            listener.showToast("Quantity updated successfully!");
                            listener.onQuantityUpdated(); // Trigger cart reload
                        } else {
                            listener.showToast("Failed to update quantity: " + responseString);
                        }
                    } catch (Exception e) {
                        listener.showToast("Error updating quantity");
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                listener.showToast("Error: " + t.getMessage());
            }
        });
    }

    private void removeItem(String productID) {
        String userID = com.example.mobile.SignInActivity.getStoredValue(context, "userID");
        if (userID == null) {
            listener.showToast("User not logged in");
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ResponseBody> call = apiService.removeFromCart(userID, productID);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseString = response.body().string();
                        if ("success".equalsIgnoreCase(responseString) || responseString.contains("\"success\":true")) {
                            listener.showToast("Item removed from cart!");
                            listener.onItemRemoved(); // Trigger cart reload
                            // Remove item from list locally (optional, depends on API behavior)
                            for (int i = 0; i < items.size(); i++) {
                                if (items.get(i).getProductID().equals(productID)) {
                                    items.remove(i);
                                    notifyItemRemoved(i);
                                    break;
                                }
                            }
                        } else {
                            listener.showToast("Failed to remove item: " + responseString);
                        }
                    } catch (Exception e) {
                        listener.showToast("Error removing item");
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                listener.showToast("Error: " + t.getMessage());
            }
        });
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        public TextView productNameTextView, priceTextView, quantityTextView, totalAmountTextView, discountedPriceTextView;
        public Button decrementButton, incrementButton, removeButton;

        public CartViewHolder(View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.cart_product_name);
            priceTextView = itemView.findViewById(R.id.cart_price);
            quantityTextView = itemView.findViewById(R.id.cart_quantity);
            totalAmountTextView = itemView.findViewById(R.id.cart_total_amount);
            discountedPriceTextView = itemView.findViewById(R.id.cart_discounted_price);
            decrementButton = itemView.findViewById(R.id.decrement_button);
            incrementButton = itemView.findViewById(R.id.increment_button);
            removeButton = itemView.findViewById(R.id.remove_button);
        }
    }
}