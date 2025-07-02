// src/main/java/com/microsservicos/CadastroClienteService/config/OpenAPIConfig.java
package com.microsservicos.CadastroClienteService.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI setorServiceAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Setor Service API")
                        .description("REST API de Setor Service")
                        .version("v0.0.1")
                        .license(new License().name("Apache 2.0"))
                );
    }
}
