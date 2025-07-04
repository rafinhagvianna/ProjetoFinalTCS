package com.microsservicos.triagem.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "cadastro-cliente-service", url = "${clients.cadastro-cliente-service.url:}")
public interface CadastroClienteServiceClient {

    /**
     * Busca o nome completo de um cliente específico.
     * Ex: GET http://cadastro-cliente-service/clientes/{clienteId}/nome
     * (Este endpoint deve retornar apenas uma String no CadastroCliente-Service)
     *
     * @param clienteId O ID do cliente.
     * @return O nome completo do cliente como String.
     */
    @GetMapping("/clientes/{clienteId}/nome")
    String getNomeCliente(@PathVariable("clienteId") UUID clienteId);

    /*
     * Opcional: Se o seu CadastroCliente-Service tiver um endpoint que retorna o objeto completo
     * ClienteResponse para o ID do cliente.
     * @GetMapping("/clientes/{clienteId}")
     * ClienteResponse getClienteById(@PathVariable("clienteId") UUID clienteId);
     */
}