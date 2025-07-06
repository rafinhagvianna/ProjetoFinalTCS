//package com.microsservicos.API_Gateway.rotas;
//
//import org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.servlet.function.RequestPredicates;
//import org.springframework.web.servlet.function.RouterFunction;
//import org.springframework.web.servlet.function.ServerResponse;
//import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
//import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
//import static org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.setPath;
//
//
//import java.net.URI;
//
//
//@Configuration
//public class Rotas {
//
//    // Rota única e simplificada para o serviço de Setor
//    @Bean
//    public RouterFunction<ServerResponse> setorServiceRota() {
//        return route("setor_service")
//                .route(RequestPredicates.path("/api/setor/**"), http("lb://SETOR-SERVICE"))
//                .filter(CircuitBreakerFilterFunctions.circuitBreaker("setorServiceCircuitBreaker", URI.create("forward:/fallbackRoute")))
//                .build();
//    }
//
//    // Rota para o Swagger do serviço de Setor
//    @Bean
//    public RouterFunction<ServerResponse> setorServiceSwaggerRota() {
//        return route("setor_service_swagger")
//                .route(RequestPredicates.path("/aggregate/setor-service/v3/api-docs"), http("lb://SETOR-SERVICE"))
//                .filter(setPath("/v3/api-docs")) // Reescreve o path para o microserviço
//                .build();
//    }
//
//    // Rota única e simplificada para o serviço de Triagem
//    @Bean
//    public RouterFunction<ServerResponse> triagemServiceRota() {
//        return route("triagem_service")
//                .route(RequestPredicates.path("/api/triagens/**"), http("lb://TRIAGEM-SERVICE"))
//                .build();
//    }
//
//    // Demais rotas refatoradas...
//
//    @Bean
//    public RouterFunction<ServerResponse> agendamentoServiceRota() {
//        return route("agendamento_service")
//                .route(RequestPredicates.path("/api/agendamento/**"), http("lb://AGENDAMENTO-SERVICE"))
//                .build();
//    }
//
//    @Bean
//    public RouterFunction<ServerResponse> atendimentoServiceRota() {
//        return route("atendimento_service")
//                .route(RequestPredicates.path("/api/atendimento/**"), http("lb://ATENDIMENTO-SERVICE"))
//                .build();
//    }
//
//    @Bean
//    public RouterFunction<ServerResponse> clienteServiceRota() {
//        return route("cliente_service")
//                .route(RequestPredicates.path("/api/cliente/**"), http("lb://CADASTROCLIENTE-SERVICE")) // Use o nome do serviço do seu diagrama
//                .build();
//    }
//
//    // ... adicione as outras rotas (documentacao, etc.) da mesma forma.
//
//    // Rota de fallback do Circuit Breaker (não precisa de alteração)
//    @Bean
//    public RouterFunction<ServerResponse> fallbackRoute() {
//        return route("fallbackRoute")
//                .GET("/fallbackRoute", request -> ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE).body("Serviço indisponível no momento. Tente novamente mais tarde."))
//                .build();
//    }
//}