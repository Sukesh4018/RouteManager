package com.BRP.routemanager.utils;

/**
 * Created by durgesh on 5/11/16.
 */
public class Constants {
    public static final String server = "ec2-54-187-129-120.us-west-2.compute.amazonaws.com/Nav/public/index.php/";
    public static final String LOGIN = server + "login";
    public static final String STATUS_SUCCESS = "success";
    public static final String STATUS_ERROR = "error";
    public static final float ZOOM = 15;

    public static class Pref {
        public static final String NAME = "Preferences";
        public static final String IS_LOGGEDIN = "is_loggedin";
        public static final String USERNAME = "username";
        public static final String PASSWORD = "password";
    }
}
