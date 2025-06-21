package com.prm392.assignment.productsale.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.prm392.assignment.productsale.R;
import com.prm392.assignment.productsale.model.products.ProductSaleModel;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

public class ProductSaleCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_FOOTER = 1;
    private final ArrayList<ProductSaleModel> data;
    private final RecyclerView recyclerView;
    private final Context context;
    @Getter
    private boolean hasMore = false; // Flag to show/hide the "Load More" button
    //    private final boolean noResultsFound = false;
    @Setter
    private boolean hideFavButton = false;

    @Setter
    private ProductSaleCardAdapter.ItemInteractionListener itemInteractionListener;

    public ProductSaleCardAdapter(Context context, RecyclerView recyclerView) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.data = new ArrayList<>();
    }

    // Method to update hasMore and refresh the adapter
    @SuppressLint("NotifyDataSetChanged")
    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
        notifyDataSetChanged(); // Or use notifyItemInserted/Removed for efficiency
    }

    @Override
    public int getItemViewType(int position) {
        if (hasMore && position == data.size()) {
            return VIEW_TYPE_FOOTER; // Footer is the last item when hasMore is true
        }
        return VIEW_TYPE_ITEM; // Regular product item
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_ITEM) {
            //Default ViewHolder
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_sale_card_layout, parent, false);
            return new ProductSaleCardAdapter.DataViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_load_more, parent, false);
            return new FooterViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof DataViewHolder) {
            ProductSaleCardAdapter.DataViewHolder holder = (ProductSaleCardAdapter.DataViewHolder) viewHolder;


            holder.name.setText(data.get(position).getProductName());
            holder.category.setText(data.get(position).getCategoryName());
            holder.price.setText(data.get(position).getCurrencyPrice());
            holder.favourite.setChecked(false);

            if (hideFavButton) holder.favourite.setVisibility(View.GONE);

            //Image
            Glide.with(context)
                    .load(data.get(position).getImageUrl())
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade(250))
                    .into(holder.image);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemInteractionListener != null)
                        itemInteractionListener.onProductClicked(data.get(holder.getBindingAdapterPosition()).getProductId(), "");
                }
            });

            holder.favourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //data.get(holder.getAbsoluteAdapterPosition()).setFavorite(holder.favourite.isChecked());
                    if (itemInteractionListener != null)
                        itemInteractionListener.onProductAddedToFav(data.get(holder.getBindingAdapterPosition()).getProductId(), holder.favourite.isChecked());
                }
            });

        } else if (viewHolder instanceof FooterViewHolder) {
            FooterViewHolder holder = (FooterViewHolder) viewHolder;
            holder.loadMoreButton.setOnClickListener(v -> {
                if (itemInteractionListener != null) {
                    itemInteractionListener.onLoadMoreClicked(); // New callback for "Load More"
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return data.size() + (hasMore ? 1 : 0); // Add 1 for footer if hasMore is true
    }

    public void addProducts(ArrayList<ProductSaleModel> products, boolean replace) {

        recyclerView.post(() -> {
            int startPosition = data.size();
            data.addAll(products);
            notifyItemRangeInserted(startPosition, products.size());
            if (replace) {
                recyclerView.scrollToPosition(0);
            }
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    public void clearProducts() {
        data.clear();
        notifyDataSetChanged();
    }

    public interface ItemInteractionListener {
        void onProductClicked(long productId, String storeType);

        void onProductAddedToFav(long productId, boolean favChecked);

        void onLoadMoreClicked();
    }

    public static class DataViewHolder extends RecyclerView.ViewHolder {
        TextView category, name, price;
        ImageView image;
        CheckBox favourite;

        public DataViewHolder(View view) {
            super(view);
            category = view.findViewById(R.id.product_sale_card_category);
            name = view.findViewById(R.id.product_sale_card_Name);
            price = view.findViewById(R.id.product_sale_card_price);
            image = view.findViewById(R.id.product_sale_card_image);
            favourite = view.findViewById(R.id.product_sale_card_favourite);
        }
    }

    public static class FooterViewHolder extends RecyclerView.ViewHolder {
        Button loadMoreButton;

        public FooterViewHolder(View view) {
            super(view);
            loadMoreButton = view.findViewById(R.id.loadMoreButton);
        }
    }

}
