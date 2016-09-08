package com.BRP.routemanager.app;

import android.content.Context;
import android.text.TextUtils;

import com.BRP.routemanager.utils.LocationUtil;
import com.BRP.routemanager.utils.PrefManager;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class rmApp extends android.app.Application {
    private static final Object TAG = rmApp.class.getSimpleName();
    private static Context mContext;
    private static RequestQueue mRequestQueue;
    private static LocationUtil locationUtil;

    public static LocationUtil getLocationUtil() {
        if (locationUtil == null)
            locationUtil = new LocationUtil(mContext);
        return locationUtil;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        rmApp.mContext = getApplicationContext();
        PrefManager.init(this);
    }

    public static Context getAppContext() {
        return rmApp.mContext;
    }

    public static RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getAppContext());
        }

        return mRequestQueue;
    }

    public static <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }
}