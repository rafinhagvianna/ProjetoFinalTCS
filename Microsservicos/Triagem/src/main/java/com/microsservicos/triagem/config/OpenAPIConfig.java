package com.microsservicos.triagem.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI triagemServiceAPI(){
        return new OpenAPI()
                .info(new Info().title("Triagem Service API")
                        .description("REST API de Triagem Service")
                        .version("v0.0.1")
                        .license(new License().name("Apache 2.0")));
//                .externalDocs(new ExternalDocumentation()
//                        .description("Esse link não te leva a lugar nenhum.")
//                        .url("https://setor-service-dummy-url.com/docs"));
    }
}