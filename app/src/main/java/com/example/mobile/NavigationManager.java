package com.example.mobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavigationManager {
    private static final String TAG = "NavigationManager";
    private static final String PREF_NAME = "UserPrefs";

    public static void setupNavigation(AppCompatActivity activity, BottomNavigationView bottomNavigationView) {
        if (bottomNavigationView != null) {
            bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_sign_in) {
                    Log.d(TAG, "Navigating to SignInActivity");
                    Intent intent = new Intent(activity, SignInActivity.class);
                    activity.startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_cart) {
                    Log.d(TAG, "Navigating to CartActivity");
                    Intent intent = new Intent(activity, CartActivity.class);
                    activity.startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_logout) {
                    Log.d(TAG, "Logging out and clearing SharedPreferences");
                    // Clear all SharedPreferences data
                    SharedPreferences prefs = activity.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.clear();
                    editor.apply();
                    Toast.makeText(activity, "Logged out successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(activity, SignInActivity.class);
                    activity.startActivity(intent);
                    activity.finish();
                    return true;
                }
                return false;
            });
        } else {
            Log.e(TAG, "BottomNavigationView not found in layout");
        }
    }
}
