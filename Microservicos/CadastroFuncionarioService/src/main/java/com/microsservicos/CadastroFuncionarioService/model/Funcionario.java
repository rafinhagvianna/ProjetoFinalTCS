package com.microsservicos.CadastroFuncionarioService.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import jakarta.persistence.GenerationType;

import java.util.UUID;

@Entity
@Table(name = "funcionario",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "nome"),
                @UniqueConstraint(columnNames = "cpf")
        })
public class Funcionario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "Nome não pode ficar em branco")
    private String nome;

    @Column(unique = true)
    @NotBlank(message = "Email não pode ficar em branco")
    @Email(message = "Formato de email inválido")
    private String email;

    @NotBlank(message = "CPF não pode ficar em branco")
    @Pattern(regexp = "\\d{11}", message = "CPF deve ter exatamente 11 dígitos")
    private String cpf;

    @NotBlank(message = "Senha não pode ficar em branco")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    private String senha;

    public Funcionario() {}

    public Funcionario(String nome, String cpf, String senha, String email) {
        this.nome = nome;
        this.cpf = cpf;
        this.senha = senha;
        this.email = email;
    }

    public UUID getId() { return id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
