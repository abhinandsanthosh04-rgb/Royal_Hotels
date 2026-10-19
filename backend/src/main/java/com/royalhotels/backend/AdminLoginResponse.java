package com.royalhotels.backend;

/**
 * AdminLoginResponse - DTO returned on successful admin authentication.
 * Contains JWT bearer token, user display details, and token expiration.
 */
public class AdminLoginResponse {

    private boolean success;
    private String token;
    private String tokenType = "Bearer";
    private String username;
    private String fullName;
    private String role;
    private long expiresInMs;
    private String message;

    public AdminLoginResponse() {
    }

    public AdminLoginResponse(boolean success, String token, String username, String fullName, String role, long expiresInMs, String message) {
        this.success = success;
        this.token = token;
        this.tokenType = "Bearer";
        this.username = username;
        this.fullName = fullName;
        this.role = role;
        this.expiresInMs = expiresInMs;
        this.message = message;
    }

    public static AdminLoginResponse success(String token, String username, String fullName, String role, long expiresInMs) {
        return new AdminLoginResponse(true, token, username, fullName, role, expiresInMs, "Authentication successful");
    }

    public static AdminLoginResponse error(String message) {
        AdminLoginResponse resp = new AdminLoginResponse();
        resp.setSuccess(false);
        resp.setMessage(message);
        return resp;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public long getExpiresInMs() {
        return expiresInMs;
    }

    public void setExpiresInMs(long expiresInMs) {
        this.expiresInMs = expiresInMs;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
