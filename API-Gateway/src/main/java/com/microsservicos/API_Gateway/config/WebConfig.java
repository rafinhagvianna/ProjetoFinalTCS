package com.microsservicos.API_Gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

     @Bean
    public CorsWebFilter corsFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.addAllowedOrigin("http://localhost:4200"); // Seu frontend Angular
	corsConfig.addAllowedOrigin("https://bankflow.ddns-ip.net");
        corsConfig.addAllowedMethod("*"); // GET, POST, PUT, DELETE, OPTIONS, etc.
        corsConfig.addAllowedHeader("*"); // Todos os cabeçalhos (Authorization, Content-Type, etc.)
        corsConfig.setAllowCredentials(true); // Permite credenciais (cookies, headers de auth)
        corsConfig.setMaxAge(3600L); // Cache preflight response por 1 hora

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig); // Aplica a todos os caminhos do Gateway

        return new CorsWebFilter(source);
    }


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // todos os caminhos do Gateway
                .allowedOrigins("http://localhost:4200") // url do Angular
		.allowedOrigins("https://bankflow.ddns-ip.net")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}

