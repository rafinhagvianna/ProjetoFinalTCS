package com.example.Agendamento_Service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "ClienteService")
public interface CadastroClienteServiceClient {

    @GetMapping("/api/cliente/{id}/nome")
    String getNomeCliente(@PathVariable("id") UUID id);


}
