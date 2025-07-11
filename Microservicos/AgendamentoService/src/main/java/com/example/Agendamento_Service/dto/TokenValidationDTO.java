package com.example.Agendamento_Service.dto;

import java.util.UUID;
import java.util.Objects;

public class TokenValidationDTO  {
    private String message;
    private UUID userId;
    private String fullName;
    private String email;
    private String role;

    // Construtor padrão (sem argumentos)
    public TokenValidationDTO() {
    }

    // Construtor com todos os argumentos
    public TokenValidationDTO(String message, UUID userId, String fullName, String email, String role) {
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
        TokenValidationDTO that = (TokenValidationDTO) o;
        return Objects.equals(message, that.getMessage()) &&
                Objects.equals(userId, that.getUserId()) &&
                Objects.equals(fullName, that.getFullName()) &&
                Objects.equals(email, that.getEmail()) &&
                Objects.equals(role, that.getRole());
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, userId, fullName, email, role);
    }
}