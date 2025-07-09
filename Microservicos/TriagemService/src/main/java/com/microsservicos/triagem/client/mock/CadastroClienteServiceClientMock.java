package com.microsservicos.triagem.client.mock;

// import com.microsservicos.triagem.client.CadastroClienteServiceClient;
// // import com.microsservicos.triagem.client.ClienteResponse; // Se estiver usando e precisar do mock

// import org.springframework.context.annotation.Profile;
// import org.springframework.stereotype.Component;

// import java.util.HashMap;
// import java.util.Map;
// import java.util.UUID; // Importe UUID

// @Component // Torna esta classe um componente Spring
// @Profile("dev") // Ativa este mock apenas quando o perfil 'dev' estiver ativo
public class CadastroClienteServiceClientMock {//implements CadastroClienteServiceClient {

    // // Dados mockados para simular o cadastro de clientes
    // private final Map<UUID, String> clienteNomes = new HashMap<>();

    // // Defina UUIDs fixos para seus clientes mockados para consistência nos testes
    // // Você pode usar UUID.randomUUID() se não precisar de consistência em execuções.
    // // Exemplo de UUIDs válidos:
    // public static final UUID CLIENTE_JOAO_ID = UUID.fromString("11111111-2222-3333-4444-555555555555");
    // public static final UUID CLIENTE_MARIA_ID = UUID.fromString("66666666-7777-8888-9999-aaaaaaaaaaaa");
    // public static final UUID CLIENTE_PEDRO_ID = UUID.fromString("bbbbbbbb-cccc-dddd-eeee-ffffffffffff");

    // public CadastroClienteServiceClientMock() {
    //     // Preencha o mapa com UUIDs reais como chaves
    //     clienteNomes.put(CLIENTE_JOAO_ID, "João da Silva");
    //     clienteNomes.put(CLIENTE_MARIA_ID, "Maria Oliveira");
    //     clienteNomes.put(CLIENTE_PEDRO_ID, "Pedro Souza");

    //     // Você também pode adicionar um UUID aleatório se quiser testar cenários genéricos
    //     clienteNomes.put(UUID.randomUUID(), "Ana Santos");
    // }

    // @Override
    // public String getNomeCliente(UUID clienteId) {
    //     System.out.println("DEBUG: Usando CadastroClienteServiceClientMock - getNomeCliente para ID: " + clienteId);
    //     return clienteNomes.getOrDefault(clienteId, "Cliente Desconhecido [MOCK]");
    // }

    // Se você tiver outros métodos na interface CadastroClienteServiceClient, implemente-os aqui
    // e certifique-se de que eles também usem UUIDs para IDs.
    /*
    // Exemplo de implementação de getClienteById se a interface tiver este método
    @Override
    public ClienteResponse getClienteById(UUID clienteId) {
        System.out.println("DEBUG: Usando CadastroClienteServiceClientMock - getClienteById para ID: " + clienteId);
        String nome = clienteNomes.getOrDefault(clienteId, "Cliente Desconhecido [MOCK]");
        // Adapte ClienteResponse se ele também tiver campos UUID ou outros dados
        return new ClienteResponse(clienteId, nome, "123.456.789-00", "mock@email.com");
    }
    */
}