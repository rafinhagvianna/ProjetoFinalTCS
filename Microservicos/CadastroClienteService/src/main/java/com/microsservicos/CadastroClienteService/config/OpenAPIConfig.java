package com.microsservicos.CadastroClienteService.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI ClienteServiceAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Cliente Service API")
                        .description("REST API de Cliente Service")
                        .version("v0.0.1")
                        .license(new License().name("Apache 2.0"))
                );
    }
}
