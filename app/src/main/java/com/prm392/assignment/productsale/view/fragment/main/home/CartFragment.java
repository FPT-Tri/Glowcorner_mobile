package com.prm392.assignment.productsale.view.fragment.main.home;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import com.prm392.assignment.productsale.R;
import com.prm392.assignment.productsale.adapters.CartListAdapter;
import com.prm392.assignment.productsale.databinding.FragmentCartBinding;
import com.prm392.assignment.productsale.model.BaseResponseModel;
import com.prm392.assignment.productsale.model.cart.CartItemModel;
import com.prm392.assignment.productsale.util.DialogsProvider;
import com.prm392.assignment.productsale.view.activity.MainActivity;
import com.prm392.assignment.productsale.viewmodel.fragment.main.home.OnSaleViewModel;

public class CartFragment extends Fragment {
    private FragmentCartBinding vb;
    private NavController navController;
    private OnSaleViewModel viewModel;
    private CartListAdapter adapter;

    private static final int REQUEST_NOTIFICATION_PERMISSION = 1;

    public CartFragment() {
        // Required empty public constructor
    }
    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_NOTIFICATION_PERMISSION);
            }
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestNotificationPermission();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (vb == null) vb = FragmentCartBinding.inflate(inflater, container, false);
        return vb.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        vb = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new Handler().post(() -> {
            navController = ((MainActivity) getActivity()).getAppNavController();
        });
        viewModel = new ViewModelProvider(this, ViewModelProvider.Factory.from(OnSaleViewModel.initializer)).get(OnSaleViewModel.class);

        adapter = new CartListAdapter(getContext(), vb.onSaleRecyclerVeiw, getViewLifecycleOwner(), viewModel);
        vb.onSaleRecyclerVeiw.setLayoutManager(new LinearLayoutManager(getContext()));
        vb.onSaleRecyclerVeiw.setAdapter(adapter);

        vb.checkout.setOnClickListener(v -> {
            navController.navigate(R.id.action_homeFragment_to_checkoutPageFragment);
        });

        vb.resetCartButton.setOnClickListener(v -> {
            clearCart();  // Gọi phương thức resetCart() khi nút được nhấn
        });

        loadCartItems();
    }

    private void clearCart() {
        int userId = viewModel.getUserModel().getId();  // Sử dụng userId thực tế

        // Gọi phương thức clearCart trong ViewModel
        viewModel.clearCart(userId).observe(getViewLifecycleOwner(), response -> {
            switch (response.code()) {
                case BaseResponseModel.SUCCESSFUL_OPERATION:
                    // Nếu xóa giỏ hàng thành công
                    adapter.clearCartItems();  // Xóa tất cả các sản phẩm trong adapter
                    Toast.makeText(getContext(), "Giỏ hàng đã được làm sạch", Toast.LENGTH_SHORT).show();
                    sendCartNotification(requireContext());
                    loadCartItems();
                    break;

                case BaseResponseModel.FAILED_REQUEST_FAILURE:
                    // Nếu xóa giỏ hàng thất bại
                    Toast.makeText(getContext(), "Không thể xóa giỏ hàng, vui lòng kiểm tra lại kết nối", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    // Xử lý lỗi khác
                    Toast.makeText(getContext(), "Lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }
    private void sendCartNotification(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        String channelId = "cart_notifications";
        String channelName = "Cart Notifications";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        // Tạo thông báo
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_cart)
                .setContentTitle("Giỏ hàng")
                .setContentText("Giỏ hàng đã được làm sạch")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        // Hiển thị thông báo
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1001, builder.build());
    }

    void loadCartItems() {
        vb.onSaleLoading.setVisibility(View.VISIBLE);
        int userId = viewModel.getUserModel().getId();  // Sử dụng userId thực tế
        // Lấy giỏ hàng từ ViewModel
        viewModel.getCart(userId).observe(getViewLifecycleOwner(), response -> {
            switch (response.code()) {
                case BaseResponseModel.SUCCESSFUL_OPERATION:
                    vb.onSaleLoading.setVisibility(View.GONE);
                    // Kiểm tra nếu giỏ hàng trống
                    if (response.body() == null) {
                        return;
                    }
                    if (response.body().getCartItems().stream().count() == 0) {
                        vb.checkout.setVisibility(View.GONE);
                    }
                    adapter.clearCartItems();
                    // Cập nhật giỏ hàng vào adapter
                    ArrayList<CartItemModel> cartItems = response.body().getCartItems(); // Đảm bảo rằng response trả về có danh sách cartItems
                    adapter.addCartItems(cartItems); // Trực tiếp dùng cartItems
                    break;

                case BaseResponseModel.FAILED_REQUEST_FAILURE:
                    DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Loading_Failed), getString(R.string.Please_Check_your_connection));
                    break;

                default:
                    DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Server_Error), getString(R.string.Code) + response.code());
            }
        });
    }

}