// src/main/java/com/microsservicos/CadastroClienteService/model/Cliente.java
package com.microsservicos.CadastroClienteService.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome não pode ficar em branco")
    private String nome;

    @NotBlank(message = "CPF não pode ficar em branco")
    @Pattern(regexp = "\\d{11}", message = "CPF deve ter exatamente 11 dígitos")
    private String cpf;

    @NotBlank(message = "Telefone não pode ficar em branco")
    @Pattern(regexp = "\\d{10,11}", message = "Telefone deve ter 10 ou 11 dígitos")
    private String telefone;

    @NotBlank(message = "Email não pode ficar em branco")
    @Email(message = "Formato de email inválido")
    private String email;

    @NotBlank(message = "Senha não pode ficar em branco")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    private String senha;

    @NotBlank(message = "Agência não pode ficar em branco")
    private String agencia;

    @NotBlank(message = "Conta não pode ficar em branco")
    private String conta;

    public Cliente() {}

    public Cliente(String nome, String cpf, String telefone,
                   String email, String senha,
                   String agencia, String conta) {
        this.nome = nome;
        this.cpf = cpf;
        this.telefone = telefone;
        this.email = email;
        this.senha = senha;
        this.agencia = agencia;
        this.conta = conta;
    }

    public Long getId() { return id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getAgencia() { return agencia; }
    public void setAgencia(String agencia) { this.agencia = agencia; }

    public String getConta() { return conta; }
    public void setConta(String conta) { this.conta = conta; }
}
