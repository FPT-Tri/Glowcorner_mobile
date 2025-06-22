package com.example.mobile.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mobile.Models.Product;
import com.example.mobile.R;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> productList;

    public ProductAdapter(List<Product> productList) {
        this.productList = productList;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        Product product = productList.get(position);
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
                // Example action: Log the product ID
                String productId = product.getProductID();
                // TODO: Implement your action (e.g., add to cart)
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