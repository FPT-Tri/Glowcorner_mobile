package com.prm392.assignment.productsale.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.ArrayList;

import com.prm392.assignment.productsale.R;
import com.prm392.assignment.productsale.model.BaseResponseModel;
import com.prm392.assignment.productsale.model.ProductModel;
import com.prm392.assignment.productsale.model.UserModel;
import com.prm392.assignment.productsale.model.cart.CartItemModel;
import com.prm392.assignment.productsale.model.cart.CartModel;
import com.prm392.assignment.productsale.util.AppSettingsManager;
import com.prm392.assignment.productsale.util.UserAccountManager;
import com.prm392.assignment.productsale.viewmodel.fragment.main.home.OnSaleViewModel;

import lombok.Getter;
import lombok.Setter;

public class CartListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final ArrayList<CartItemModel> data;
    private final RecyclerView recyclerView;
    private final Context context;
    private final LifecycleOwner lifecycleOwner;
    private final OnSaleViewModel viewModel;

    @Getter
    @Setter
    private UserModel userModel;

    private final CartItemModel loadingCardObject = new CartItemModel();

    private final int TYPE_DATA_VIEW_HOLDER = 0;
    private final int TYPE_LOADING_VIEW_HOLDER = 1;
    @Setter
    private ProductsListAdapter.LastItemReachedListener lastItemReachedListener;
    @Setter
    private ProductsListAdapter.ItemInteractionListener itemInteractionListener;
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

    public CartListAdapter(Context context, RecyclerView recyclerView, LifecycleOwner lifecycleOwner, OnSaleViewModel onSaleViewModel) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.data = new ArrayList<>();
        this.lifecycleOwner = lifecycleOwner;
        this.viewModel = onSaleViewModel;
        this.userModel = UserAccountManager.getUser(context);
    }

    public static class DataViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, productQuantity, brand;
        ImageView productImage;
        CheckBox deleteCheckBox;
        ImageButton increaseButton, decreaseButton;

        public DataViewHolder(View view) {
            super(view);
            // Initialize views based on the updated layout XML
            brand = view.findViewById(R.id.product_list_item_brand);
            productName = view.findViewById(R.id.product_list_item_Name);
            productPrice = view.findViewById(R.id.product_list_item_price);
            productQuantity = view.findViewById(R.id.product_list_item_quantity); // Add a quantity field if necessary
            productImage = view.findViewById(R.id.product_list_item_image);
            deleteCheckBox = view.findViewById(R.id.product_list_item_delete); // Changed ID
            increaseButton = view.findViewById(R.id.increaseBtn);
            decreaseButton = view.findViewById(R.id.decreaseBtn);
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

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout, parent, false);
        return new CartListAdapter.DataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == TYPE_DATA_VIEW_HOLDER) {

            DataViewHolder holder  = (DataViewHolder) viewHolder;
            CartItemModel cartItem = data.get(position);

            holder.productName.setText(data.get(position).getProduct().getProductName());
            holder.brand.setText((data.get(position).getProduct().getCategory().getCategoryName()));
            holder.productPrice.setText(String.format("%s %s", data.get(position).getPrice(), context.getString(R.string.currency)));
            holder.productQuantity.setText(String.valueOf(data.get(position).getQuantity()));
            Glide.with(context)
                    .load(Uri.parse(data.get(position).getProduct().getProductImage()))
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade(250))
                    .into(holder.productImage);

            holder.increaseButton.setOnClickListener(v -> {
                int updatedQuantity = cartItem.getQuantity() + 1;
                cartItem.setQuantity(updatedQuantity);
                cartItem.setPrice(cartItem.getProduct().getPrice() * updatedQuantity);
                holder.productQuantity.setText(String.valueOf(updatedQuantity));
                holder.productPrice.setText(String.format("%s %s", cartItem.getPrice(), context.getString(R.string.currency)));
                updateCartItemQuantity(cartItem);  // Call API to update quantity
            });

            // Handle decrease button click
            holder.decreaseButton.setOnClickListener(v -> {
                if (cartItem.getQuantity() > 1) {  // Prevent going below 1
                    int updatedQuantity = cartItem.getQuantity() - 1;
                    cartItem.setQuantity(updatedQuantity);
                    cartItem.setPrice(cartItem.getProduct().getPrice() * updatedQuantity);
                    holder.productQuantity.setText(String.valueOf(updatedQuantity));
                    holder.productPrice.setText(String.format("%s %s", cartItem.getPrice(), context.getString(R.string.currency)));
                    updateCartItemQuantity(cartItem);  // Call API to update quantity
                }
            });

            holder.deleteCheckBox.setOnClickListener(v -> {
                // Khi nút Delete được nhấn, gọi phương thức xóa
                removeCartItem(cartItem);  // Gọi hàm xóa item khỏi RecyclerView
            });
        }
    }

    private void updateCartItemQuantity(CartItemModel cartItem) {
        int userId = viewModel.getUserModel().getId();
        int productId = cartItem.getProductId(); // Get the product ID
        int newQuantity = cartItem.getQuantity(); // Get the updated quantity


        // Call the ViewModel's method to update the cart item quantity
        viewModel.updateCartItem(userId, productId, newQuantity).observe(lifecycleOwner, response -> {
            switch (response.code()) {
                case BaseResponseModel.SUCCESSFUL_OPERATION:
                    // If the update is successful, notify the user
                    Toast.makeText(context, "Cart item updated successfully", Toast.LENGTH_SHORT).show();
                    getCartTotalPrice();
                    break;

                case BaseResponseModel.FAILED_REQUEST_FAILURE:
                    // If the update fails, show an error
                    Toast.makeText(context, "Error: Failed to update cart item", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    // Handle any other errors
                    Toast.makeText(context, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    public void removeCartItem(CartItemModel cartItem) {
        int userId = viewModel.getUserModel().getId();
        int productId = cartItem.getProductId(); // Lấy productId của item cần xóa

        // Gọi phương thức removeCartItem từ ViewModel
            viewModel.removeCartItem(userId, productId).observe(lifecycleOwner, response -> {
                switch (response.code()) {
                    case BaseResponseModel.SUCCESSFUL_OPERATION:
                        // Nếu xóa thành công, xóa item khỏi RecyclerView
                        recyclerView.post(() -> {
                            int index = data.indexOf(cartItem);
                            if (index != -1) {
                                data.remove(index);
                                notifyItemRemoved(index);
                            }
                        });



                        getCartTotalPrice();
                        Toast.makeText(context, "Product removed from cart", Toast.LENGTH_SHORT).show();
                        break;

                    case BaseResponseModel.FAILED_REQUEST_FAILURE:
                        Toast.makeText(context, "Error: Failed to remove item", Toast.LENGTH_SHORT).show();
                        break;

                    default:
                        Toast.makeText(context, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                        break;
                }
            });
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (holder.getBindingAdapterPosition() == data.size() - 1 && lastItemReachedListener != null && !isLoading())
            lastItemReachedListener.onLastItemReached();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void addCartItem(CartItemModel cartItem) {
        recyclerView.post(() -> {
            data.add(cartItem);
            notifyItemInserted(getItemCount());
        });
    }

    public void clearCartItems() {
        data.clear();  // Xóa tất cả các item trong danh sách giỏ hàng
        notifyDataSetChanged();  // Cập nhật lại RecyclerView
    }

//    public  void removeCartItem(CartItemModel cartItem) {
//        recyclerView.post(() -> {
//            int index = data.indexOf(cartItem);
//            data.remove(index);
//            notifyItemRemoved(index);
//        });
//    }

    private void getCartTotalPrice() {
        int userId = viewModel.getUserModel().getId();

        // Gọi API để lấy tổng giá trị giỏ hàng từ server
        viewModel.getCartTotal(userId).observe(lifecycleOwner, response -> {
            if (response != null && response.body() != null) {
                // Lấy tổng giá trị từ response và cập nhật vào giao diện
                double totalPrice = response.body().getTotal();
                viewModel.updateTotalPrice(totalPrice);  // Cập nhật giá trị tổng vào ViewModel
            } else {
                Toast.makeText(context, "Failed to fetch cart total", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addCartItems(ArrayList<CartItemModel> cartItems) {
        recyclerView.post(() -> {
            data.addAll(cartItems);
            notifyItemRangeInserted(getItemCount(), cartItems.size());
        });
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
