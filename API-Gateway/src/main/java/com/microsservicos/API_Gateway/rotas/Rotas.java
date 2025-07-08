//package com.microsservicos.API_Gateway.rotas;
//
//import org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions;
//import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
//import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.servlet.function.RequestPredicates;
//import org.springframework.web.servlet.function.RouterFunction;
//import org.springframework.web.servlet.function.ServerResponse;
//
//import java.net.URI;
//
//import static org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.setPath;
//import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
//
//
//@Configuration
//public class Rotas {
//
//    @Bean
//    public RouterFunction<ServerResponse> setorServiceRota(){
//        return route("setor_service")
//                .route(RequestPredicates.path("/api/setor"), HandlerFunctions.http("http://localhost:8084"))
//                .route(RequestPredicates.path("/api/setor/ativos"), HandlerFunctions.http("http://localhost:8084"))
//                .route(RequestPredicates.path("/api/setor/inativos"), HandlerFunctions.http("http://localhost:8084"))
//                .route(RequestPredicates.path("/api/setor/status"), HandlerFunctions.http("http://localhost:8084"))
//                .route(RequestPredicates.path("/api/setor/{id}"), HandlerFunctions.http("http://localhost:8084"))
//                .filter(CircuitBreakerFilterFunctions.circuitBreaker("setorServiceCircuitBreaker",
//                        URI.create("forward://fallbackRoute")))
//                .build();
//    }
//
//    @Bean
//    public RouterFunction<ServerResponse> setorServiceSwaggerRota(){
//        return route("setor_service_swagger")
//                .route(RequestPredicates.path("/aggregate/setor-service/v3/api-docs"), HandlerFunctions.http("http://localhost:8084"))
//                .filter(setPath("/v3/api-docs"))
//                .build();
//    }
//
//
//    @Bean
//    public RouterFunction<ServerResponse> triagemServiceRota(){
//        return route("triagem_service")
//                .route(RequestPredicates.path("/api/triagem"), HandlerFunctions.http("http://localhost:8081"))
//                .route(RequestPredicates.path("api/triagem/proxima"), HandlerFunctions.http("http://localhost:8081"))
//                .build();
//    }
//
//    @Bean
//    public RouterFunction<ServerResponse> triagemServiceSwaggerRota(){
//        return route("triagem_service_swagger")
//                .route(RequestPredicates.path("/aggregate/triagem-service/v3/api-docs"), HandlerFunctions.http("http://localhost:8081"))
//                .filter(setPath("/v3/api-docs"))
//                .build();
//    }
//
//    @Bean
//    public RouterFunction<ServerResponse> agendamentoServiceRota(){
//        return route("agendamento_service")
//                .route(RequestPredicates.path("/api/agendamento"), HandlerFunctions.http("http://localhost:8082"))
//                .build();
//    }
//
//    @Bean
//    public RouterFunction<ServerResponse> atendimentoServiceRota(){
//        return route("atendimento_service")
//                .route(RequestPredicates.path("/api/atendimento"), HandlerFunctions.http("http://localhost:8083"))
//                .build();
//    }
//
//    @Bean
//    public RouterFunction<ServerResponse> documentacaoServiceRota(){
//        return route("documentacao_service")
//                .route(RequestPredicates.path("/api/documentacao"), HandlerFunctions.http("http://localhost:8085"))
//                .build();
//    }
//
//    @Bean
//    public RouterFunction<ServerResponse> clienteServiceRota(){
//        return route("cliente_service")
//                .route(RequestPredicates.path("/api/cliente"), HandlerFunctions.http("http://localhost:8086"))
//                .route(RequestPredicates.POST("/api/cliente"), HandlerFunctions.http("http://localhost:8086"))
//                .route(RequestPredicates.GET("/api/cliente"), HandlerFunctions.http("http://localhost:8086"))
//                .route(RequestPredicates.path("/api/cliente/login"), HandlerFunctions.http("http:localhost:8086"))
//                .build();
//    }
//
//    @Bean
//    public RouterFunction<ServerResponse> clienteSwaggerRota(){
//        return route("cliente_service_swagger")
//                .route(RequestPredicates.path("/aggregate/cliente-service/v3/api-docs"), HandlerFunctions.http("http://localhost:8086"))
//                .filter(setPath("/v3/api-docs"))
//                .build();
//    }
//
//
//
//    // Circuit Breaker
//
//    @Bean
//    public RouterFunction<ServerResponse> fallbackRoute(){
//        return route("fallbackRoute")
//                .GET("/falbackRoute", request -> ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE).body("Serviço indisponível, tente novamente mais tarde."))
//                .build();
//
//    }
//}


// Em com.microsservicos.API_Gateway.rotas.Rotas.java

package com.microsservicos.API_Gateway.rotas;


import org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;

import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;

@Configuration
public class Rotas {

    // Simplificado para usar wildcard e Service Discovery
    @Bean
    public RouterFunction<ServerResponse> setorServiceRota(){
        return route("setor_service")
                .route(RequestPredicates.path("/api/setor/**"), HandlerFunctions.http("lb://setor-service")) // <-- MUDANÇA
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("setorServiceCircuitBreaker",
                        URI.create("forward://fallbackRoute")))
                .build();
    }

    // Simplificado para usar wildcard e Service Discovery
    @Bean
    public RouterFunction<ServerResponse> triagemServiceRota(){
        return route("triagem_service")
                .route(RequestPredicates.path("/api/triagem/**"), HandlerFunctions.http("lb://triagem-service")) // <-- MUDANÇA
                .build();
    }

    // Simplificado para usar wildcard e Service Discovery
    @Bean
    public RouterFunction<ServerResponse> agendamentoServiceRota(){
        return route("agendamento_service")
                .route(RequestPredicates.path("/api/agendamento/**"), HandlerFunctions.http("lb://agendamento-service")) // <-- MUDANÇA
                .build();
    }

    // Simplificado para usar wildcard e Service Discovery
    @Bean
    public RouterFunction<ServerResponse> atendimentoServiceRota(){
        return route("atendimento_service")
                .route(RequestPredicates.path("/api/atendimento/**"), HandlerFunctions.http("lb://atendimento-service")) // <-- MUDANÇA
                .build();
    }

    // Simplificado para usar wildcard e Service Discovery
    @Bean
    public RouterFunction<ServerResponse> documentacaoServiceRota(){
        return route("documentacao_service")
                .route(RequestPredicates.path("/api/documentacao/**"), HandlerFunctions.http("lb://documentacao-service")) // <-- MUDANÇA
                .build();
    }

    // *** A ROTA DO CLIENTE, CORRIGIDA E MELHORADA ***
    @Bean
    public RouterFunction<ServerResponse> clienteServiceRota(){
        return route("cliente_service")
                .route(RequestPredicates.POST("/api/cliente"), HandlerFunctions.http("http://localhost:8086"))
                // Usa wildcard para cobrir /api/cliente, /api/cliente/login, etc.
                // E usa o nome do serviço no Eureka.
//                .route(RequestPredicates.path("/api/cliente/**"), HandlerFunctions.http("lb://CLIENTE")) // <-- MUDANÇA PRINCIPAL
                .route(RequestPredicates.POST("/api/cliente/login"), HandlerFunctions.http("http://localhost:8086"))
                .build();
    }


    // --- Rotas do Swagger (Não precisam mudar, mas podem) ---
    // Elas podem continuar com URLs fixas se preferir, ou também usar Service Discovery.
    // Manterei como estão para simplicidade, já que funcionam.

//    @Bean
//    public RouterFunction<ServerResponse> setorServiceSwaggerRota(){
//        // ... (sem alteração)
//    }
//
//    @Bean
//    public RouterFunction<ServerResponse> triagemServiceSwaggerRota(){
//        // ... (sem alteração)
//    }
//
//    @Bean
//    public RouterFunction<ServerResponse> clienteSwaggerRota(){
//        // ... (sem alteração)
//    }
//
//    // ... (sua rota de fallback, sem alteração) ...
//    @Bean
//    public RouterFunction<ServerResponse> fallbackRoute(){
//        // ... (sem alteração)
//    }
}
