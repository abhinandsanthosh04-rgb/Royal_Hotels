package com.royalhotels.backend;

/**
 * AdminLoginRequest - DTO representing admin login credentials submitted via POST /api/admin/auth/login.
 */
public class AdminLoginRequest {

    private String username;
    private String password;

    public AdminLoginRequest() {
    }

    public AdminLoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
