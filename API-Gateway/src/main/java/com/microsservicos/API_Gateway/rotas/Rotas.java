package com.microsservicos.API_Gateway.rotas;

import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;


@Configuration
public class Rotas {

    @Bean
    public RouterFunction<ServerResponse> setorServiceRota(){
        return GatewayRouterFunctions.route("setor_service")
                .route(RequestPredicates.path("/api/setor"), HandlerFunctions.http("http://localhost:8084"))
                .build();
    }

    public RouterFunction<ServerResponse> triagemServiceRota(){
        return GatewayRouterFunctions.route("triagem_service")
                .route(RequestPredicates.path("/api/triagem"), HandlerFunctions.http("http://localhost:8081"))
                .build();
    }

    public RouterFunction<ServerResponse> agendamentoServiceRota(){
        return GatewayRouterFunctions.route("agendamento_service")
                .route(RequestPredicates.path("/api/agendamento"), HandlerFunctions.http("http://localhost:8082"))
                .build();
    }

    public RouterFunction<ServerResponse> atendimentoServiceRota(){
        return GatewayRouterFunctions.route("atendimento_service")
                .route(RequestPredicates.path("/api/atendimento"), HandlerFunctions.http("http://localhost:8083"))
                .build();
    }

    public RouterFunction<ServerResponse> documentacaoServiceRota(){
        return GatewayRouterFunctions.route("documentacao_service")
                .route(RequestPredicates.path("/api/documentacao"), HandlerFunctions.http("http://localhost:8085"))
                .build();
    }
}
