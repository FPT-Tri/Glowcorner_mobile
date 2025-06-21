package com.prm392.assignment.productsale.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.prm392.assignment.productsale.model.UserModel;
import com.prm392.assignment.productsale.view.activity.AccountSign;

public final class UserAccountManager {
    //Token Type Parameters
    public static final int TOKEN_TYPE_NORMAL = 0;
    public static final int TOKEN_TYPE_BEARER = 1;
    public static final String FORCED_SIGN_OUT = "forcedSignOut";
    private static UserModel user;

    public static void signIn(Activity activity, Intent intent, String token, UserModel userModel) {
        user = userModel;
        Context context = activity.getApplicationContext();
        //Save User Data
        SharedPrefManager.get(context).setSignedIn(true);
        SharedPrefManager.get(context).setToken(token);
        SharedPrefManager.get(context).setUser(userModel);

        //Launch Main Activity
        activity.startActivity(intent);
        activity.onBackPressed();
    }

    public static UserModel getUser(Context context) {
        if (user == null) user = SharedPrefManager.get(context).getUser();
        return user;
    }

//    public static void updateUser(Context context, UserModel userModel) {
//        user = userModel;
//        SharedPrefManager.get(context).setUser(user);
//    }

    public static String getToken(Context context, int type) {
        String output = "";
        switch (type) {
            case TOKEN_TYPE_NORMAL:
                output = SharedPrefManager.get(context).getToken();
                break;

            case TOKEN_TYPE_BEARER:
                output = "Bearer " + SharedPrefManager.get(context).getToken();
                break;
        }

        return output;
    }

    public static void signOut(Activity activity, boolean forced) {
        DialogsProvider.get(activity).setLoading(true);

        Context context = activity.getApplicationContext();

        //clear user account data
        SharedPrefManager.get(context).setSignedIn(false);
        SharedPrefManager.get(context).setToken("");
        SharedPrefManager.get(context).setUser(null);

        DialogsProvider.get(activity).setLoading(false);

        //Launch Account Sign Activity
        Intent intent = new Intent(context, AccountSign.class);
        intent.putExtra(FORCED_SIGN_OUT, forced);
        activity.startActivity(intent);
        activity.finish();
    }
}
