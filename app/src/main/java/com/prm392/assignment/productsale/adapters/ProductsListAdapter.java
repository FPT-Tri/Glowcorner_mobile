package com.prm392.assignment.productsale.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.prm392.assignment.productsale.R;
import com.prm392.assignment.productsale.model.ProductModel;
import com.prm392.assignment.productsale.util.AppSettingsManager;

import lombok.Getter;
import lombok.Setter;

public class ProductsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    @Getter
    ArrayList<ProductModel> Data;
    private final RecyclerView recyclerView;
    Context context;

    private final ProductModel loadingCardObject = new ProductModel();

    private final int TYPE_DATA_VIEW_HOLDER = 0;
    private final int TYPE_LOADING_VIEW_HOLDER = 1;

    @Setter
    private LastItemReachedListener lastItemReachedListener;
    @Setter
    private ItemInteractionListener itemInteractionListener;

    @Setter
    private boolean hideFavButton = false;
    @Setter
    private boolean showDate = false;

    public interface LastItemReachedListener {
        void onLastItemReached();
    }

    public interface ItemInteractionListener {
        void onProductClicked(ProductModel product);

        void onProductAddedToFav(long productId, boolean favChecked);
    }

    public ProductsListAdapter(Context context, RecyclerView recyclerView) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.Data = new ArrayList<>();
    }

    //item view inner class
    public static class DataViewHolder extends RecyclerView.ViewHolder {

        TextView brand, name, price, rate, date, sale;
        ImageView image, store;
        CheckBox favourite;
        ImageView rateIcon;

        public DataViewHolder(View view) {
            super(view);

            brand = view.findViewById(R.id.product_list_item_brand);
            name = view.findViewById(R.id.product_list_item_Name);
            price = view.findViewById(R.id.product_list_item_price);
            rate = view.findViewById(R.id.product_list_item_rate);
            image = view.findViewById(R.id.product_list_item_image);
            store = view.findViewById(R.id.product_list_item_store);
            favourite = view.findViewById(R.id.product_list_item_favourite);
            rateIcon = view.findViewById(R.id.product_list_item_rate_icon);
            date = view.findViewById(R.id.product_list_item_date);
            sale = view.findViewById(R.id.product_list_item_salePercent);
        }

    }

    public static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public LoadingViewHolder(View view) {
            super(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (Data.get(position) == loadingCardObject) return TYPE_LOADING_VIEW_HOLDER;
        else return TYPE_DATA_VIEW_HOLDER;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_LOADING_VIEW_HOLDER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_loading_layout, parent, false);
            return new ProductsListAdapter.LoadingViewHolder(view);
        }

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_item_layout, parent, false);
        return new ProductsListAdapter.DataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == TYPE_DATA_VIEW_HOLDER) {
            DataViewHolder holder = (DataViewHolder) viewHolder;

            if (renderDataInLocalLanguage())
                holder.name.setText(Data.get(position).getNameArabic());
            else holder.name.setText(Data.get(position).getName());
            holder.brand.setText(Data.get(position).getBrand());
            holder.price.setText(Data.get(position).getPrice() + context.getString(R.string.currency));
            holder.rate.setText(String.valueOf(Data.get(position).getRate()));
            holder.favourite.setChecked(Data.get(position).isFavorite());
            holder.date.setText(dateTimeConvert(Data.get(position).getDate()));
            holder.sale.setText(Data.get(position).getSalePercent() + context.getString(R.string.sale_percent));

            if (hideFavButton) holder.favourite.setVisibility(View.GONE);
            if (Data.get(position).getSalePercent() == 0) holder.sale.setVisibility(View.GONE);
            if (Data.get(position).getBrand() == null) holder.brand.setVisibility(View.INVISIBLE);

            if (showDate) holder.date.setVisibility(View.VISIBLE);

            if (Data.get(position).getRate() == 0) {
                holder.rate.setVisibility(View.INVISIBLE);
                holder.rateIcon.setVisibility(View.INVISIBLE);
            }

            //Store
            //if(isDarkModeEnabled()) holder.store.setImageTintList(ColorStateList.valueOf(Color.WHITE));

            if (Data.get(position).getStoreName() == null) holder.store.setVisibility(View.GONE);
            else {
                Glide.with(context)
                        .load(Data.get(position).getStoreLogo())
                        .transition(DrawableTransitionOptions.withCrossFade(250))
                        .into(holder.store);
            }

            //Image
            Glide.with(context)
                    .load(Data.get(position).getImage())
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade(250))
                    .into(holder.image);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemInteractionListener != null)
                        itemInteractionListener.onProductClicked(Data.get(holder.getAdapterPosition()));
                }
            });

            holder.favourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Data.get(holder.getAdapterPosition()).setFavorite(holder.favourite.isChecked());
                    if (itemInteractionListener != null)
                        itemInteractionListener.onProductAddedToFav(Data.get(holder.getAdapterPosition()).getId(), holder.favourite.isChecked());
                }
            });

        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (holder.getAdapterPosition() == Data.size() - 1 && lastItemReachedListener != null && !isLoading())
            lastItemReachedListener.onLastItemReached();
    }

    @Override
    public int getItemCount() {
        return Data.size();
    }

    public void addProduct(ProductModel product) {
        recyclerView.post(() -> {
            Data.add(product);
            notifyItemInserted(getItemCount());
        });
    }

    public void removeProduct(ProductModel product) {
        recyclerView.post(() -> {
            int index = Data.indexOf(product);
            Data.remove(product);
            notifyItemRemoved(index);
        });
    }

    public void removeProductByIndex(int i) {
        Data.remove(i);
        notifyItemRemoved(i);
    }

    public void removeProductById(long id) {
        recyclerView.post(() -> {
            for (int i = 0; i < Data.size(); i++) {
                if (Data.get(i).getId() == id) {
                    Data.remove(i);
                    notifyItemRemoved(i);
                    break;
                }
            }
        });
    }

    public void addProducts(ArrayList<ProductModel> products) {
        recyclerView.post(() -> {
            Data.addAll(products);
            notifyItemRangeInserted(getItemCount(), products.size());
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clearProducts() {
        Data.clear();
        notifyDataSetChanged();
    }

    public boolean isLoading() {
        return Data.contains(loadingCardObject);
    }

    public void setLoading(boolean loading) {
        recyclerView.post(() -> {
            if (isLoading() == loading) return;

            if (loading) {
                Data.add(loadingCardObject);
                notifyItemInserted(getItemCount());
            } else {
                Data.remove(loadingCardObject);
                notifyItemChanged(getItemCount());
            }
        });

    }

    public boolean isDarkModeEnabled() {
        int currentMode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return currentMode == Configuration.UI_MODE_NIGHT_YES;
    }

    public String dateTimeConvert(String dateTime) {
        String inputPattern = "yyyy-MM-dd'T'HH:mm:ss";
        String outputPattern = "dd/MM/yyyy";

        try {
            SimpleDateFormat input = new SimpleDateFormat(inputPattern);
            input.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date parsed = input.parse(dateTime);

            SimpleDateFormat destFormat = new SimpleDateFormat(outputPattern);
            destFormat.setTimeZone(TimeZone.getDefault());

            return destFormat.format(parsed);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "-";
    }

    boolean renderDataInLocalLanguage() {
        switch (AppSettingsManager.getLanguageKey(context)) {
            case AppSettingsManager.LANGUAGE_ENGLISH:
                return false;
            case AppSettingsManager.LANGUAGE_ARABIC:
                return true;
            default:
                String systemLanguage = Locale.getDefault().getLanguage();
                return systemLanguage.equals(AppSettingsManager.LANGUAGE_ARABIC);
        }
    }
}
