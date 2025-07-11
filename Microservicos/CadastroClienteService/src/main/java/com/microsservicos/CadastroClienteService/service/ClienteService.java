package com.microsservicos.CadastroClienteService.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.microsservicos.CadastroClienteService.dto.LoginRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.microsservicos.CadastroClienteService.dto.ClienteRequest;
import com.microsservicos.CadastroClienteService.dto.ClienteResponse;
import com.microsservicos.CadastroClienteService.model.Cliente;
import com.microsservicos.CadastroClienteService.repository.ClienteRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class ClienteService {

    private final ClienteRepository repository;
    private final PasswordEncoder passwordEncoder;

    public ClienteService(ClienteRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public ClienteResponse criarCliente(ClienteRequest req) {

        String senhaCodificada = passwordEncoder.encode(req.senha());

        Cliente entidade = new Cliente(
                req.nome(),
                req.cpf(),
                req.telefone(),
                req.email(),
                senhaCodificada, // <-- Usamos a senha já codificada
                req.agencia(),
                req.conta()
        );
        Cliente salvo = repository.save(entidade);
        return toResponse(salvo);
    }

    public Cliente verificarLogin(LoginRequest req) {
        Optional<Cliente> optCliente = repository.findByEmail(req.email());

        // Se o email não for encontrado, o login falha.
        if (optCliente.isEmpty()) {
            return null;
        }

        Cliente cliente = optCliente.get();
        String senhaPura = req.senha(); // A senha que o usuário digitou
        String hashDoBanco = cliente.getSenha(); // O hash que está salvo

        // O 'matches' compara a senha pura com o hash do banco de forma segura.
        if (passwordEncoder.matches(senhaPura, hashDoBanco)){
            return cliente;
        }else{
            return null;
        }
    }

    public List<ClienteResponse> listarClientes() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // O método buscarPorEmail não é mais necessário ser público, pois a lógica de login foi centralizada.
    // Mas pode manter se usar em outro lugar.
    public Optional<Cliente> buscarPorEmail(String email) {
        return repository.findByEmail(email);
    }

    public Optional<Cliente> buscarPorId(UUID id) {
        return repository.findById(id);
    }

    private ClienteResponse toResponse(Cliente c) {
        // IMPORTANTE: Evite retornar a senha (mesmo hashada) em respostas de API.
        // Por simplicidade, mantive aqui, mas o ideal seria ter um DTO sem o campo senha.
        return new ClienteResponse(
                c.getNome(),
                c.getCpf(),
                c.getTelefone(),
                c.getEmail(),
                c.getSenha(),
                c.getAgencia(),
                c.getConta()
        );
    }

    public String solicitarRedefinicaoSenha(String email) {
        Optional<Cliente> optCliente = repository.findByEmail(email);

        if (optCliente.isEmpty()) {
            // Não informe ao usuário se o e-mail existe ou não por segurança.
            // Apenas retorne, o fluxo continua normalmente.
            return null;
        }

        Cliente cliente = optCliente.get();
        String token = UUID.randomUUID().toString();

        // Define o token e uma validade (ex: 1 hora a partir de agora)
        cliente.setResetPasswordToken(token);
        cliente.setResetPasswordTokenExpiry(LocalDateTime.now().plusHours(1));

        System.out.println("Link de redefinição: http://localhost:4200/redefinir-senha?token=" + token);
        return token;
    }

    public boolean redefinirSenha(String token, String novaSenha) {
        // Busca um cliente pelo token de redefinição
        Optional<Cliente> optCliente = repository.findByResetPasswordToken(token);

        if (optCliente.isEmpty()) {
            return false; // Token inválido
        }

        Cliente cliente = optCliente.get();

        // Verifica se o token expirou
        if (cliente.getResetPasswordTokenExpiry().isBefore(LocalDateTime.now())) {
            return false; // Token expirado
        }

        // Codifica a nova senha
        String senhaCodificada = passwordEncoder.encode(novaSenha);
        cliente.setSenha(senhaCodificada);

        // Limpa o token para que não possa ser usado novamente
        cliente.setResetPasswordToken(null);
        cliente.setResetPasswordTokenExpiry(null);

        repository.save(cliente);
        return true;
    }

//    public record RedefinirSenhaRequest(String token, String novaSenha) {}
}
