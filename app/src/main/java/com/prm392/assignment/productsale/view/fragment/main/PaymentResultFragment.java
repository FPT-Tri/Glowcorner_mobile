package com.prm392.assignment.productsale.view.fragment.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import com.prm392.assignment.productsale.R;
import com.prm392.assignment.productsale.databinding.FragmentPaymentResultBinding;
import com.prm392.assignment.productsale.databinding.FragmentProductPageBinding;
import com.prm392.assignment.productsale.view.activity.MainActivity;


public class PaymentResultFragment extends Fragment {
    private FragmentPaymentResultBinding vb;
    private NavController navController;

    public PaymentResultFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        vb = FragmentPaymentResultBinding.inflate(inflater, container, false);
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
        ((MainActivity) getActivity()).setTitle("Payment Result");
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new Handler().post(() -> {
            navController = ((MainActivity) getActivity()).getAppNavController();
        });
        vb.imgFailedStatus.setVisibility(View.GONE);
        vb.imgSuccessStatus.setVisibility(View.GONE);

        String result = getArguments().getString("Result");
        String title = getArguments().getString("Title");
        String message = getArguments().getString("Message");

        if (result.equals("Success")) {
            vb.imgSuccessStatus.setVisibility(View.VISIBLE);
        } else {
            vb.imgFailedStatus.setVisibility(View.VISIBLE);
        }
        vb.textViewNotify.setText(title);
        vb.textViewDetail.setText(message);

        vb.btnBackHome.setOnClickListener((v) -> {
            navController.navigate(R.id.action_paymentResultFragment_to_homeFragment);
        });

    }

}