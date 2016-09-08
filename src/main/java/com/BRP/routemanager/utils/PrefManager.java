package com.BRP.routemanager.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by durgesh on 5/11/16.
 */
public class PrefManager {
    private static SharedPreferences sharedPreferences;

    public static void init(Context context) {
        if (sharedPreferences == null)
            sharedPreferences = context.getSharedPreferences(Constants.Pref.NAME, Context.MODE_PRIVATE);
    }

    public static void login(String username, String pass) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.Pref.IS_LOGGEDIN, true);
        editor.putString(Constants.Pref.USERNAME, username);
        editor.putString(Constants.Pref.PASSWORD, pass);
        editor.apply();
    }

    public static boolean isLoggedIn() {
        return sharedPreferences.getBoolean(Constants.Pref.IS_LOGGEDIN, false);
    }
}
