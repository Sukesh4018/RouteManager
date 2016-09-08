package com.BRP.routemanager.utils;

import android.util.Log;
import android.widget.Toast;

import com.BRP.routemanager.app.rmApp;

/**
 * Created by durgesh on 5/11/16.
 */
public class CommonUtils {
    public static void log(String message) {
        logStatus("Log", message);
    }

    public static void logStatus(String key, String message) {
        Log.i(key, message);
    }

    public static void toast(String message) {
        Toast.makeText(rmApp.getAppContext(), message, Toast.LENGTH_SHORT).show();
    }

    public static void toastLong(String message) {
        Toast.makeText(rmApp.getAppContext(), message, Toast.LENGTH_LONG).show();
    }
}
