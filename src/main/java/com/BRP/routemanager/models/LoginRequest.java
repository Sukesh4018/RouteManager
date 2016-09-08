package com.BRP.routemanager.models;

/**
 * Created by durgesh on 5/11/16.
 */
public class LoginRequest {
    public String username, password, fromApp;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
        fromApp = "1";
    }
}
