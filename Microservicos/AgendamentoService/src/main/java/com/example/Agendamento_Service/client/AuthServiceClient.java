package com.example.Agendamento_Service.client; // Ajuste o pacote se necessário, para client ou auth

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.example.Agendamento_Service.dto.TokenValidationDTO;

@FeignClient(name = "AuthService") 
public interface AuthServiceClient {

    @GetMapping("/api/auth/validate") 
    TokenValidationDTO validateToken(@RequestHeader("Authorization") String authorizationHeader);

}