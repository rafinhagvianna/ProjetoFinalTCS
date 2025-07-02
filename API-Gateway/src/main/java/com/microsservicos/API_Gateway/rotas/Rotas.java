package com.microsservicos.API_Gateway.rotas;

import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.setPath;


@Configuration
public class Rotas {

    @Bean
    public RouterFunction<ServerResponse> setorServiceRota(){
        return GatewayRouterFunctions.route("setor_service")
                .route(RequestPredicates.path("/api/setor"), HandlerFunctions.http("http://localhost:8084"))
                .route(RequestPredicates.path("/api/setor/ativos"), HandlerFunctions.http("http://localhost:8084"))
                .route(RequestPredicates.path("/api/setor/inativos"), HandlerFunctions.http("http://localhost:8084"))
                .route(RequestPredicates.path("/api/setor/status"), HandlerFunctions.http("http://localhost:8084"))
                .route(RequestPredicates.path("/api/setor/{id}"), HandlerFunctions.http("http://localhost:8084"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> setorServiceSwaggerRota(){
        return GatewayRouterFunctions.route("setor_service_swagger")
                .route(RequestPredicates.path("/aggregate/setor-service/v3/api-docs"), HandlerFunctions.http("http://localhost:8084"))
                .filter(setPath("/v3/api-docs"))
                .build();
    }


    @Bean
    public RouterFunction<ServerResponse> triagemServiceRota(){
        return GatewayRouterFunctions.route("triagem_service")
                .route(RequestPredicates.path("/api/triagem"), HandlerFunctions.http("http://localhost:8081"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> agendamentoServiceRota(){
        return GatewayRouterFunctions.route("agendamento_service")
                .route(RequestPredicates.path("/api/agendamento"), HandlerFunctions.http("http://localhost:8082"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> atendimentoServiceRota(){
        return GatewayRouterFunctions.route("atendimento_service")
                .route(RequestPredicates.path("/api/atendimento"), HandlerFunctions.http("http://localhost:8083"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> documentacaoServiceRota(){
        return GatewayRouterFunctions.route("documentacao_service")
                .route(RequestPredicates.path("/api/documentacao"), HandlerFunctions.http("http://localhost:8085"))
                .build();
    }
}
