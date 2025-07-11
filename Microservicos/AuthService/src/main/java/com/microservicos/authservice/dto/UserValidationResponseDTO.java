package com.microservicos.authservice.dto;

import java.util.Objects; // Importar para equals e hashCode
import java.util.UUID;

public class UserValidationResponseDTO {
    private String message;
    private String name;
    private String email;
    private UUID id;
    private String role; // Role do usuário (ex: "CLIENTE", "FUNCIONARIO") - Opcional

    // Construtor padrão (sem argumentos)
    public UserValidationResponseDTO() {
    }

    // Construtor com todos os argumentos
    public UserValidationResponseDTO(String message, String name, String email, UUID id, String role) {
        this.message = message;
        this.name = name;
        this.email = email;
        this.id = id;
        this.role = role;
    }

    // --- Getters ---
    public String getMessage() {
        return message;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public UUID getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    // --- Setters ---
    public void setMessage(String message) {
        this.message = message;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // --- Métodos toString(), equals() e hashCode() ---
    // (Gerados automaticamente pela IDE para conveniência, mas você pode implementá-los manualmente)

    @Override
    public String toString() {
        return "UserValidationResponseDTO{" +
                "message='" + message + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", id=" + id +
                ", role='" + role + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserValidationResponseDTO that = (UserValidationResponseDTO) o;
        return Objects.equals(message, that.message) &&
                Objects.equals(name, that.name) &&
                Objects.equals(email, that.email) &&
                Objects.equals(id, that.id) &&
                Objects.equals(role, that.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, name, email, id, role);
    }
}