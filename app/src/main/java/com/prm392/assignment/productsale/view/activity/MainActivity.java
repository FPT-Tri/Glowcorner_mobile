package com.prm392.assignment.productsale.view.activity;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RadioButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.prm392.assignment.productsale.R;
import com.prm392.assignment.productsale.databinding.ActivityMainBinding;
import com.prm392.assignment.productsale.model.UserModel;
import com.prm392.assignment.productsale.util.AppSettingsManager;
import com.prm392.assignment.productsale.util.NetworkBroadcastReceiver;
import com.prm392.assignment.productsale.util.SharedPrefManager;
import com.prm392.assignment.productsale.util.UserAccountManager;
import com.prm392.assignment.productsale.view.UnderlayNavigationDrawer;
import com.prm392.assignment.productsale.viewmodel.activity.MainActivityViewModel;

import java.util.Locale;

//import vn.zalopay.sdk.ZaloPaySDK;

//import vn.zalopay.sdk.ZaloPaySDK;


public class MainActivity extends AppCompatActivity {
    public static final String JUST_SIGNED_IN = "justSignedIn";

    private static final int REQUEST_NOTIFICATION_PERMISSION = 1;
    private NavController navController;
    private ActivityMainBinding vb;
    private MainActivityViewModel viewModel;
    private UnderlayNavigationDrawer underlayNavigationDrawer;
    private NetworkBroadcastReceiver networkBroadcastReceiver;
    private boolean rememberMe;
    private boolean firstLaunch;
    private boolean signedIn;
    private boolean justSignedIn;
    private String token;
    private UserModel user;

    @Override
    protected void attachBaseContext(Context newBase) {
        String language;
        if (AppSettingsManager.isLanguageSystemDefault(newBase))
            language = Locale.getDefault().getLanguage();
        else language = AppSettingsManager.getLanguageKey(newBase);

        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        newBase.getResources().getConfiguration().setLocale(locale);
        newBase.getResources().getConfiguration().setLayoutDirection(locale);

        super.attachBaseContext(newBase);
    }

    void changeLocale() {
        String language;
        if (AppSettingsManager.isLanguageSystemDefault(this))
            language = Locale.getDefault().getLanguage();
        else language = AppSettingsManager.getLanguageKey(this);

        Locale locale = new Locale(language);
        Configuration config = getResources().getConfiguration();
        Locale.setDefault(locale);
        config.setLocale(locale);
        config.setLayoutDirection(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestNotificationPermission();

        //Set App Settings
        switch (AppSettingsManager.getTheme(this)) {
            case AppSettingsManager.THEME_LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case AppSettingsManager.THEME_DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) changeLocale();

        super.onCreate(savedInstanceState);
        setTheme(R.style.SaleHunter);

        vb = ActivityMainBinding.inflate(getLayoutInflater());

        View view = vb.getRoot();
        setContentView(view);

        overridePendingTransition(R.anim.lay_on, R.anim.lay_off);

        try {
            viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
            NavHostFragment navHostFragment =
                    (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.main_FragmentContainer);
            navController = navHostFragment.getNavController();
            firstLaunch = SharedPrefManager.get(this).isFirstLaunch();
            rememberMe = SharedPrefManager.get(this).isRememberMeChecked();
            signedIn = SharedPrefManager.get(this).isSignedIn();
//            token = UserAccountManager.getToken(this, UserAccountManager.TOKEN_TYPE_BEARER);
            user = UserAccountManager.getUser(this);

            justSignedIn = getIntent().getBooleanExtra(JUST_SIGNED_IN, false);

            if (firstLaunch) {
                startActivity(new Intent(this, AppIntro.class));
                finish();
            } else if (!signedIn) {
                startActivity(new Intent(this, AccountSign.class));
                finish();
            } else if (!(rememberMe || justSignedIn)) {
                UserAccountManager.signOut(this, true);
            } else {

                // Comment these two lines to skip sign in
                loadUserData(user);
//                if (!justSignedIn) syncUserData(); //From Server

                //Side Menu
                underlayNavigationDrawer = new UnderlayNavigationDrawer(this, vb.menuFrontView, findViewById(R.id.main_FragmentContainer), vb.menuBackView, vb.menuButton);
                vb.menu.setOnCheckedChangeListener((radioGroup, i) -> {

                    vb.currentFragmentTitle.setText(((RadioButton) findViewById(i)).getText().toString());
                    if (i == R.id.menu_home) {
                        navigateToFragment(R.id.homeFragment);
                    } else if (i == R.id.menu_profile) {
                        navigateToFragment(R.id.profileFragment);
                    } else if (i == R.id.menu_signout) {
                        UserAccountManager.signOut(MainActivity.this, false);
                    } else {
                        navigateToFragment(R.id.underConstructionFragment2);
                    }

                });

                //Network Checker
                networkBroadcastReceiver = new NetworkBroadcastReceiver(this);
                registerReceiver(networkBroadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

                //Sale Hunter links detection
//                vb.appVersion.setText(BuildConfig.VERSION_NAME);
                Uri appLinkData = getIntent().getData();

                if (appLinkData != null) {
                    String url = appLinkData.getPath();
                    if (url.lastIndexOf("/") == url.length() - 1)
                        url = url.substring(0, url.lastIndexOf("/"));

                    if (appLinkData.getPath().contains("pid=")) {
                        String productId = url.substring(url.indexOf("=") + 1);

                        Bundle bundle = new Bundle();
                        bundle.putLong("productId", Long.parseLong(productId));
                        navController.navigate(R.id.productPageFragment, bundle);
                    } else if (appLinkData.getPath().equals("/profile"))
                        vb.menuProfile.performClick();
                }
            }
        } catch (Exception e) {
            Log.e("MainActivity", "Error inflating layout", e);
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_NOTIFICATION_PERMISSION);
            }
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
//        ZaloPaySDK.getInstance().onResult(intent);
        Log.e("MainActivity", "onNewIntent");
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        try {

        } catch (Exception e) {
            Log.e("MainActivity", "Error inflating layout", e);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        underlayNavigationDrawer.detectTouch(event);

        return super.onTouchEvent(event);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkBroadcastReceiver != null) unregisterReceiver(networkBroadcastReceiver);
    }


    void navigateToFragment(int id) {
        try {
            underlayNavigationDrawer.closeMenu();
            new Handler().postDelayed(() -> {
                navController.popBackStack(id, true);
                navController.navigate(id, null, new NavOptions.Builder().setEnterAnim(R.anim.fragment_in).setExitAnim(R.anim.fragment_out).build());

            }, underlayNavigationDrawer.getAnimationDuration());
        } catch (Exception e) {
            Log.e("MainActivity", "Error inflating layout", e);
        }
    }


    public void loadUserData(UserModel userModel) {
        if (userModel != null) user = userModel;
        else user = UserAccountManager.getUser(this);

        vb.menuUsername.setText(user.getUserName());
        vb.menuAccountType.setText(user.getAccountType());
        Glide.with(this).load(user.getImageLink())
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade(50))
                .placeholder(R.drawable.profile_placeholder)
                .circleCrop()
                .into(vb.menuProfilePic);
    }

    public NavController getAppNavController() {
        return navController;
    }

    public void setTitle(String title) {
        vb.currentFragmentTitle.post(() -> {
            vb.currentFragmentTitle.setText(title);
        });
    }

}