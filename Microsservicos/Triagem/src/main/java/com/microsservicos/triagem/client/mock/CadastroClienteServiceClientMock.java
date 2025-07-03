package com.microsservicos.triagem.client.mock;

import com.microsservicos.triagem.client.CadastroClienteServiceClient;
import com.microsservicos.triagem.client.ClienteResponse; // Se estiver usando

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component // Torna esta classe um componente Spring
@Profile("dev") // Ativa este mock apenas quando o perfil 'dev' estiver ativo
public class CadastroClienteServiceClientMock implements CadastroClienteServiceClient {

    // Dados mockados para simular o cadastro de clientes
    private final Map<Long, String> clienteNomes = new HashMap<>();

    public CadastroClienteServiceClientMock() {
        clienteNomes.put(1L, "João da Silva");
        clienteNomes.put(2L, "Maria Oliveira");
        clienteNomes.put(3L, "Pedro Souza");
    }

    @Override
    public String getNomeCliente(Long clienteId) {
        System.out.println("DEBUG: Usando CadastroClienteServiceClientMock - getNomeCliente para ID: " + clienteId);
        return clienteNomes.getOrDefault(clienteId, "Cliente Desconhecido [MOCK]");
    }

    // Se você tiver outros métodos na interface CadastroClienteServiceClient, implemente-os aqui
    // Ex: @Override
    // public ClienteResponse getClienteById(Long clienteId) {
    //     System.out.println("DEBUG: Usando CadastroClienteServiceClientMock - getClienteById para ID: " + clienteId);
    //     String nome = clienteNomes.getOrDefault(clienteId, "Cliente Desconhecido [MOCK]");
    //     return new ClienteResponse(clienteId, nome, "123.456.789-00", "mock@email.com");
    // }
}