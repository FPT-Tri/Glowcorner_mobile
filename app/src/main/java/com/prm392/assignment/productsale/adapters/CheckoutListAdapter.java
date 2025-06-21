package com.prm392.assignment.productsale.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.prm392.assignment.productsale.R;
import com.prm392.assignment.productsale.model.ProductModel;
import com.prm392.assignment.productsale.model.cart.CartItemModel;

import java.util.ArrayList;

import lombok.Setter;

public class CheckoutListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final ArrayList<CartItemModel> data;
    private final RecyclerView recyclerView;
    private final Context context;

    private final CartItemModel loadingCardObject = new CartItemModel();

    private final int TYPE_DATA_VIEW_HOLDER = 0;
    private final int TYPE_LOADING_VIEW_HOLDER = 1;


    public CheckoutListAdapter(Context context, RecyclerView recyclerView) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.data = new ArrayList<>();
    }

    public static class DataViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, productQuantity, productCategory;
        ImageView productImage;

        public DataViewHolder(View view) {
            super(view);
            // Initialize views based on the updated layout XML
            productName = view.findViewById(R.id.product_list_item_Name);
            productPrice = view.findViewById(R.id.product_list_item_price);
            productQuantity = view.findViewById(R.id.product_list_item_quantity); // Add a quantity field if necessary
            productImage = view.findViewById(R.id.product_list_item_image);
            productCategory = view.findViewById(R.id.product_list_item_brand);
        }
    }

    public static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public LoadingViewHolder(View view) {
            super(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (data.get(position) == loadingCardObject) return TYPE_LOADING_VIEW_HOLDER;
        else return TYPE_DATA_VIEW_HOLDER;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_LOADING_VIEW_HOLDER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_loading_layout, parent, false);
            return new ProductsListAdapter.LoadingViewHolder(view);
        }

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkout_item_layout, parent, false);
        return new CheckoutListAdapter.DataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == TYPE_DATA_VIEW_HOLDER) {

            DataViewHolder holder  = (DataViewHolder) viewHolder;

            holder.productName.setText(data.get(position).getProduct().getProductName());
            holder.productPrice.setText(String.format("%s %s", data.get(position).getPrice(), context.getString(R.string.currency)));
            holder.productQuantity.setText("Quantity: " + data.get(position).getQuantity());
            holder.productCategory.setText(data.get(position).getProduct().getCategory().getCategoryName());

            Glide.with(context)
                    .load(Uri.parse(data.get(position).getProduct().getProductImage()))
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade(250))
                    .into(holder.productImage);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public void addCartItems(ArrayList<CartItemModel> cartItems) {
        recyclerView.post(() -> {
            data.addAll(cartItems);
            notifyItemRangeInserted(getItemCount(), cartItems.size());
        });
    }

    public void clearCartItems() {
        data.clear();  // Xóa tất cả các item trong danh sách giỏ hàng
        notifyDataSetChanged();  // Cập nhật lại RecyclerView
    }
    public boolean isLoading() {
        return data.contains(loadingCardObject);
    }

    public void setLoading(boolean loading) {
        recyclerView.post(() -> {
            if (isLoading() == loading) return;
            if (loading) {
                data.add(loadingCardObject);
                notifyItemInserted(getItemCount());
            } else {
                int index = data.indexOf(loadingCardObject);
                data.remove(index);
                notifyItemRemoved(index);
            }
        });
    }

}
