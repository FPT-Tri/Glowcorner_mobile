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
                if (itemId == R.id.nav_home) {
                    Log.d(TAG, "Navigating to HomeActivity");
                    Intent intent = new Intent(activity, HomeActivity.class);
                    activity.startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_cart) {
                    Log.d(TAG, "Navigating to CartActivity");
                    Intent intent = new Intent(activity, CartActivity.class);
                    activity.startActivity(intent);
                    return true;
                }else if (itemId == R.id.nav_quiz) {
                        Log.d(TAG, "Navigating to QuizFragment");
                        Intent intent = new Intent(activity, QuizActivity.class);
                        activity.startActivity(intent);
                        return true;
                }
                return false;
            });
        } else {
            Log.e(TAG, "BottomNavigationView not found in layout");
        }
    }
}