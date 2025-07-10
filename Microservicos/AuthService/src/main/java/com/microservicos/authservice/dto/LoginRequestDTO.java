package com.microservicos.authservice.dto;

import java.util.Objects;

public class AuthenticationRequest {
    private String username;
    private String password;
    private String userType; // "CLIENT" ou "EMPLOYEE"

    // Construtor sem argumentos
    public AuthenticationRequest() {
    }

    // Construtor com todos os argumentos
    public AuthenticationRequest(String username, String password, String userType) {
        this.username = username;
        this.password = password;
        this.userType = userType;
    }

    // Getters
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getUserType() { return userType; }

    // Setters
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setUserType(String userType) { this.userType = userType; }

    // equals() e hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthenticationRequest that = (AuthenticationRequest) o;
        return Objects.equals(username, that.username) && Objects.equals(password, that.password) && Objects.equals(userType, that.userType);
    }
    @Override
    public int hashCode() { return Objects.hash(username, password, userType); }

    // toString()
    @Override
    public String toString() {
        return "AuthenticationRequest{" + "username='" + username + '\'' + ", password='[PROTECTED]'" + ", userType='" + userType + '\'' + '}';
    }

    // Simulação do Builder
    public static AuthenticationRequestBuilder builder() { return new AuthenticationRequestBuilder(); }
    public static class AuthenticationRequestBuilder {
        private String username; private String password; private String userType;
        public AuthenticationRequestBuilder username(String username) { this.username = username; return this; }
        public AuthenticationRequestBuilder password(String password) { this.password = password; return this; }
        public AuthenticationRequestBuilder userType(String userType) { this.userType = userType; return this; }
        public AuthenticationRequest build() { return new AuthenticationRequest(username, password, userType); }
    }
}