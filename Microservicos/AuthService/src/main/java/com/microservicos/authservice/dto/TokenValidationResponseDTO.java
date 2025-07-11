package com.microservicos.authservice.dto;

import java.util.UUID;
import java.util.Objects; // Para equals e hashCode

public class TokenValidationResponseDTO {
    private String message;
    private UUID userId;
    private String fullName;
    private String email;
    private String role;

    // Construtor padrão (sem argumentos)
    public TokenValidationResponseDTO() {
    }

    // Construtor com todos os argumentos
    public TokenValidationResponseDTO(String message, UUID userId, String fullName, String email, String role) {
        this.message = message;
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }

    // --- Getters ---
    public String getMessage() {
        return message;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    // --- Setters ---
    public void setMessage(String message) {
        this.message = message;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // --- Métodos toString(), equals() e hashCode() ---
    @Override
    public String toString() {
        return "TokenValidationResponseDTO{" +
                "message='" + message + '\'' +
                ", userId=" + userId +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TokenValidationResponseDTO that = (TokenValidationResponseDTO) o;
        return Objects.equals(message, that.message) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(fullName, that.fullName) &&
                Objects.equals(email, that.email) &&
                Objects.equals(role, that.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, userId, fullName, email, role);
    }
}