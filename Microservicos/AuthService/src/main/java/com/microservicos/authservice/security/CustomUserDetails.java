package com.microservicos.authservice.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID; // Para o ID do usuário

public class CustomUserDetails implements UserDetails {

    private UUID userId;
    private String email;
    private String fullName;
    private String role; // A role principal
    private Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(UUID userId, String email, String fullName, String role) {
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        // Adiciona a role como uma SimpleGrantedAuthority
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        // Não armazenamos a senha aqui, pois a autenticação é via token
        return null;
    }

    @Override
    public String getUsername() {
        // O username pode ser o email ou o ID, dependendo de como você quer identificá-lo.
        // Se o subject do token é o UUID, use o UUID como username, ou o email.
        return email; // Ou String.valueOf(userId); ou fullName;
    }

    // Getters para os campos adicionais
    public UUID getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public String getRole() {
        return role;
    }

    // Métodos da interface UserDetails (geralmente true para JWTs stateless)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}