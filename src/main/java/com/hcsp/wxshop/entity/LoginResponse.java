package com.hcsp.wxshop.entity;

import com.hcsp.wxshop.generate.User;

public class LoginResponse {
    private boolean login;
    private User user;

    public static LoginResponse notLogin() {
        return new LoginResponse(false, null);
    }

    public static LoginResponse login(User user) {
        return new LoginResponse(true, user);
    }

    public LoginResponse() {
    }

    private LoginResponse(boolean login, User user) {
        this.login = login;
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public boolean isLogin() {
        return login;
    }
}
