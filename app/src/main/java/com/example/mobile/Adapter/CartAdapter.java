package com.example.mobile.Adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile.Models.CartResponse;
import com.example.mobile.R;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartResponse.Item> items;

    public void setItems(List<CartResponse.Item> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CartViewHolder holder, int position) {
        CartResponse.Item item = items.get(position);
        holder.productNameTextView.setText(item.getProductName());
        holder.quantityTextView.setText("Qty: " + item.getQuantity());
        holder.totalAmountTextView.setText("Total: $" + item.getTotalAmount());

        // Xử lý hiển thị giá
        if (item.getDiscountedTotalAmount() != null) {
            // Có giá giảm: hiển thị totalAmount gạch ngang đỏ và giá giảm màu đỏ
            holder.priceTextView.setText("$" + item.getTotalAmount());
            holder.priceTextView.setPaintFlags(holder.priceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.priceTextView.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.red));
            holder.discountedPriceTextView.setText("$" + item.getDiscountedTotalAmount());
            holder.discountedPriceTextView.setVisibility(View.VISIBLE);
        } else {
            // Không có giá giảm: chỉ hiển thị totalAmount màu đen
            holder.priceTextView.setText("$" + item.getTotalAmount());
            holder.priceTextView.setPaintFlags(holder.priceTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.priceTextView.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.black));
            holder.discountedPriceTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        public TextView productNameTextView, priceTextView, quantityTextView, totalAmountTextView, discountedPriceTextView;

        public CartViewHolder(View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.cart_product_name);
            priceTextView = itemView.findViewById(R.id.cart_price);
            quantityTextView = itemView.findViewById(R.id.cart_quantity);
            totalAmountTextView = itemView.findViewById(R.id.cart_total_amount);
            discountedPriceTextView = itemView.findViewById(R.id.cart_discounted_price);
        }
    }
}