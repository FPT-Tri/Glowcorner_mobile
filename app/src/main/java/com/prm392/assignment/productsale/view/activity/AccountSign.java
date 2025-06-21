package com.prm392.assignment.productsale.view.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.prm392.assignment.productsale.R;
import com.prm392.assignment.productsale.databinding.ActivityAccountSignBinding;
import com.prm392.assignment.productsale.util.DialogsProvider;
import com.prm392.assignment.productsale.util.NetworkBroadcastReceiver;
import com.prm392.assignment.productsale.util.UserAccountManager;
import com.prm392.assignment.productsale.viewmodel.activity.AccountSignViewModel;

public class AccountSign extends AppCompatActivity {
    private ActivityAccountSignBinding vb;
    private NavController navController;
    private AccountSignViewModel viewModel;

    NetworkBroadcastReceiver networkBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.SaleHunter);

        try {
            vb = ActivityAccountSignBinding.inflate(getLayoutInflater());
            View view = vb.getRoot();
            setContentView(view);
            NavHostFragment navHostFragment =
                    (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.account_sign_fragmentsContainer);

            overridePendingTransition(R.anim.lay_on,R.anim.null_anim);
            viewModel = new ViewModelProvider(this).get(AccountSignViewModel.class);
            navController = navHostFragment.getNavController();
            vb.accountSignBack.setOnClickListener(button -> {
                onBackPressed();
            });
        } catch (Exception e) {
            Log.d("AccountSign", "onCreate: " + e.getMessage());
        }

        networkBroadcastReceiver = new NetworkBroadcastReceiver(this);
        registerReceiver(networkBroadcastReceiver , new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        boolean forcedSignOut = getIntent().getBooleanExtra(UserAccountManager.FORCED_SIGN_OUT,false);
        if(forcedSignOut) DialogsProvider.get(this).messageDialog(getString(R.string.Session_Expired),getString(R.string.You_are_signed_out_for_account_security));
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkBroadcastReceiver);
    }

    public void setTitle(String title) {
        vb.accountSignTitle.setText(title);
    }

    public boolean isBackButtonVisible(){return vb.accountSignBack.getVisibility() == View.VISIBLE;}

    public void setBackButton(boolean backButton){

        if(backButton){
            vb.accountSignBack.setVisibility(View.VISIBLE);
            vb.accountSignBack.startAnimation(AnimationUtils.loadAnimation(this, R.anim.back_button_in));
        }
        else {
            vb.accountSignBack.setVisibility(View.GONE);
            vb.accountSignBack.startAnimation(AnimationUtils.loadAnimation(this, R.anim.back_button_out));
        }

    }

}