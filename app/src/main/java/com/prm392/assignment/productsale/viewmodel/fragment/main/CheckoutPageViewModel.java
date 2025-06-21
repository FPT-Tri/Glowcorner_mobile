package com.prm392.assignment.productsale.viewmodel.fragment.main;

import static android.app.PendingIntent.getActivity;
import static androidx.core.content.ContextCompat.startActivity;
import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.gesture.OrientedBoundingBox;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;
import androidx.navigation.NavController;

import com.prm392.assignment.productsale.Api.CreateOrder;
import com.prm392.assignment.productsale.R;
import com.prm392.assignment.productsale.data.repository.CartRepository;
import com.prm392.assignment.productsale.data.repository.ProductsSaleRepository;
import com.prm392.assignment.productsale.model.BaseResponseModel;
import com.prm392.assignment.productsale.model.UserModel;
import com.prm392.assignment.productsale.model.cart.CartModel;
import com.prm392.assignment.productsale.util.UserAccountManager;
import com.prm392.assignment.productsale.view.activity.MainActivity;
import com.prm392.assignment.productsale.view.activity.PaymentNotification;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.Locale;

import lombok.Getter;
import lombok.Setter;
import retrofit2.Response;
//import vn.zalopay.sdk.ZaloPayError;
//import vn.zalopay.sdk.ZaloPaySDK;
//import vn.zalopay.sdk.listeners.PayOrderListener;

public class CheckoutPageViewModel extends ViewModel {
    private final ProductsSaleRepository productsSaleRepository;
    private final CartRepository cartRepository;
    private LiveData<Response<CartModel>> cartLiveData;

    private MutableLiveData<String> paymentResult = new MutableLiveData<>();

    private long productId;
    private final String token;

    @Getter
    @Setter
    private CartModel cartModel;

    @Getter
    private final UserModel userModel;

    @Getter
    @Setter
    private String paymentMethod;

    private NavController controller;

    private final Application app;

    public LiveData<String> getPaymentResult() {
        return paymentResult;
    }

    public CheckoutPageViewModel(@NotNull Application application) {
        super();
        app = application;
        cartRepository = new CartRepository();
        productsSaleRepository = new ProductsSaleRepository();

        token = UserAccountManager.getToken(application, UserAccountManager.TOKEN_TYPE_BEARER);
        userModel = UserAccountManager.getUser(application);

    }

    public static final ViewModelInitializer<CheckoutPageViewModel> initializer = new ViewModelInitializer<>(
            CheckoutPageViewModel.class,
            creationExtras -> {
                Application app = creationExtras.get(APPLICATION_KEY);
                assert app != null;
                return new CheckoutPageViewModel(app);
            }
    );

    public LiveData<Response<BaseResponseModel>> completePaymentAndConvertCartToOrder(
            int userId, String paymentMethod, String billingAddress) {
        return cartRepository.completePaymentAndConvertCartToOrder(token, userId, paymentMethod, billingAddress);
    }

    public LiveData<Response<CartModel>> getCart(int userId) {
        cartLiveData = cartRepository.getCart(token, userId);
        return cartLiveData;
    }

    public void buyNow(Context context) {
//        int result = 0;
//        if (paymentMethod == "Cash") {
//            if (cartModel == null) return;
//            int userId = userModel.getId();
//            String billingAddress = userModel.getAddress();
//
//            completePaymentAndConvertCartToOrder(userId, "Cash", billingAddress).observeForever(response -> {
//                if (response != null && response.isSuccessful()) {
//                    NavController navController = ((MainActivity) context).getAppNavController();
//                    Bundle bundle = new Bundle();
//                    bundle.putString("Result", "Success");
//                    bundle.putString("Title", "Thanh toán thành công");
//                    bundle.putString("Message", "Giỏ hàng đã được chuyển thành đơn hàng.");
//                    navController.navigate(R.id.action_checkoutPageFragment_to_paymentResultFragment, bundle);
//                } else {
//                    NavController navController = ((MainActivity) context).getAppNavController();
//                    Bundle bundle = new Bundle();
//                    bundle.putString("Result", "Failed");
//                    bundle.putString("Title", "Thanh toán thất bại");
//                    bundle.putString("Message", "Có lỗi xảy ra trong khi thanh toán đơn hàng.");
//                    navController.navigate(R.id.action_checkoutPageFragment_to_paymentResultFragment, bundle);
//                }
//            });
//        } else if (paymentMethod == "ZaloPay") {
//            if (cartModel == null) return;
//            CreateOrder orderApi = new CreateOrder();
//            int userId = userModel.getId();
//            String billingAddress = userModel.getAddress();
//
//            String totalString = String.format(Locale.US, "%.0f", cartModel.getTotalPrice());
//
//            try {
//                JSONObject data = orderApi.createOrder(totalString);
//                String code = data.getString("return_code");
//                if (code.equals("1")) {
//                    String token = data.getString("zp_trans_token");
//                    ZaloPaySDK.getInstance().payOrder((Activity) context, token, "demozpdk://app", new PayOrderListener() {
//                        @Override
//                        public void onPaymentSucceeded(String s, String s1, String s2) {
//                            NavController navController = ((MainActivity) context).getAppNavController();
//                            Bundle bundle = new Bundle();
//                            completePaymentAndConvertCartToOrder(userId, "ZaloPay", billingAddress).observeForever(response -> {
//                                if (response != null && response.isSuccessful()) {
//                                    bundle.putString("Result", "Success");
//                                    bundle.putString("Title", "Thanh toán thành công");
//                                    bundle.putString("Message", "Giỏ hàng đã được chuyển thành đơn hàng. Xin cảm ơn!");
//                                    sendCartNotification(context);
//                                    navController.navigate(R.id.action_checkoutPageFragment_to_paymentResultFragment, bundle);
//                                } else {
//                                    bundle.putString("Result", "Failed");
//                                    bundle.putString("Title", "Thanh toán thất bại");
//                                    bundle.putString("Message", "Có lỗi xảy ra trong khi thanh toán đơn hàng.");
//                                    navController.navigate(R.id.action_checkoutPageFragment_to_paymentResultFragment, bundle);
//                                }
//                            });
//                        }
//
//                        @Override
//                        public void onPaymentCanceled(String s, String s1) {
//                            NavController navController = ((MainActivity) context).getAppNavController();
//                            Bundle bundle = new Bundle();
//                            bundle.putString("Result", "Failed");
//                            bundle.putString("Title", "Thanh toán thất bại");
//                            bundle.putString("Message", "Bạn đã từ chối thanh toán đơn hàng.");
//                            navController.navigate(R.id.action_checkoutPageFragment_to_paymentResultFragment, bundle);
//                        }
//
//                        @Override
//                        public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {
//                            NavController navController = ((MainActivity) context).getAppNavController();
//                            Bundle bundle = new Bundle();
//                            bundle.putString("Result", "Failed");
//                            bundle.putString("Title", "Thanh toán thất bại");
//                            bundle.putString("Message", "Bạn đã từ chối thanh toán đơn hàng.");
//                            navController.navigate(R.id.action_checkoutPageFragment_to_paymentResultFragment, bundle);
//                        }
//                    });
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
    }

    private void sendCartNotification(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        String channelId = "checkout_notifications";
        String channelName = "Checkout Notifications";

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
                .setContentTitle("Thanh toán")
                .setContentText("Thanh toán thành công")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        // Hiển thị thông báo
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1001, builder.build());
    }
}
