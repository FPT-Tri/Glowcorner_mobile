package com.prm392.assignment.productsale.view.fragment.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.prm392.assignment.productsale.R;
import com.prm392.assignment.productsale.adapters.CheckoutListAdapter;
import com.prm392.assignment.productsale.databinding.FragmentCheckoutPageBinding;
import com.prm392.assignment.productsale.model.BaseResponseModel;
import com.prm392.assignment.productsale.model.UserModel;
import com.prm392.assignment.productsale.model.cart.CartItemModel;
import com.prm392.assignment.productsale.model.cart.CartModel;
import com.prm392.assignment.productsale.util.DialogsProvider;
import com.prm392.assignment.productsale.view.activity.MainActivity;
import com.prm392.assignment.productsale.view.activity.PaymentNotification;
import com.prm392.assignment.productsale.viewmodel.fragment.main.CheckoutPageViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import lecho.lib.hellocharts.view.LineChartView;
//import vn.zalopay.sdk.Environment;
//import vn.zalopay.sdk.ZaloPaySDK;


public class CheckoutPageFragment extends Fragment {
    private FragmentCheckoutPageBinding vb;
    private CheckoutPageViewModel viewModel;

    private CheckoutListAdapter adapter;
    private NavController navController;

    public CheckoutPageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // ZaloPay SDK Init
//        ZaloPaySDK.init(2553, Environment.SANDBOX);
    }

//


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        vb = FragmentCheckoutPageBinding.inflate(inflater, container, false);
        return vb.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        vb = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setTitle("Checkout");
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this, ViewModelProvider.Factory.from(CheckoutPageViewModel.initializer)).get(CheckoutPageViewModel.class);

        new Handler().post(() -> {
            navController = ((MainActivity) getActivity()).getAppNavController();
        });

        adapter = new CheckoutListAdapter(getContext(), vb.checkoutRecyclerView);
        vb.checkoutRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        vb.checkoutRecyclerView.setAdapter(adapter);

        vb.paymentGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.cash) {
                viewModel.setPaymentMethod("Cash");
            } else if (checkedId == R.id.creditCard) {
                viewModel.setPaymentMethod("ZaloPay");
            }
        });

        vb.cash.setChecked(true);

        vb.buyNowBtn.setOnClickListener((v) -> {
            viewModel.buyNow(getActivity());
//            navController.navigate(R.id.action_checkoutPageFragment_to_paymentResultFragment);
        });

        vb.navBack.setOnClickListener((v) -> {
            getActivity().getOnBackPressedDispatcher().onBackPressed();
        });

        loadCheckoutData();
    }

    void loadCheckoutData() {
        vb.checkoutPageLoadingPage.setVisibility(View.VISIBLE);
        int userId = viewModel.getUserModel().getId();
        // Lấy giỏ hàng từ ViewModel
        viewModel.getCart(userId).observe(getViewLifecycleOwner(), response -> {
            switch (response.code()) {
                case BaseResponseModel.SUCCESSFUL_OPERATION:
                    if (response.body() == null) {
                        return;
                    }
                    viewModel.setCartModel(response.body());
                    adapter.clearCartItems();
                    ArrayList<CartItemModel> cartItems = response.body().getCartItems();
                    adapter.addCartItems(cartItems);

                    renderCheckoutPage();
                    vb.checkoutPageLoadingPage.setVisibility(View.GONE);
                    vb.getRoot().startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.lay_on));
                    break;

                case BaseResponseModel.FAILED_REQUEST_FAILURE:
                    DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Loading_Failed), getString(R.string.Please_Check_your_connection));
                    break;

                default:
                    DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Server_Error), getString(R.string.Code) + response.code());
            }
        });
    }



    void renderCheckoutPage() {
        CartModel cartModel = viewModel.getCartModel();
        UserModel userModel = viewModel.getUserModel();
        vb.txtTotalAmount.setText(cartModel.getTotalPrice() + "$");
        vb.lblUsername.setText(userModel.getUserName());
        vb.lblUserPhone.setText(userModel.getPhone());
        vb.lblUserAddress.setText(userModel.getAddress());

    }

}