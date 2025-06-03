package com.roomies.util;

import com.roomies.model.Klient;

public class UserSession {

    private static UserSession instance;
    private Klient zalogowanyKlient;

    private UserSession() {
    }

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void loginUser(Klient klient) {
        this.zalogowanyKlient = klient;
    }

    public void logoutUser() {
        this.zalogowanyKlient = null;
    }

    public Klient getZalogowanyKlient() {
        return zalogowanyKlient;
    }

    public boolean isUserLoggedIn() {
        return zalogowanyKlient != null;
    }
}