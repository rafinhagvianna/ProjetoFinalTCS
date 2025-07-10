package com.example.Agendamento_Service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    // Precisa da URL base do microsserviço de serviços.

    @Value("${servicos.api.base-url}") // Spring vai injetar o valor de application.yaml aqui
    private String servicosApiBaseUrl;

    @Bean
    public WebClient webClientServicos(WebClient.Builder builder) {
        return builder
                .baseUrl(servicosApiBaseUrl)
                .build();
    }
}