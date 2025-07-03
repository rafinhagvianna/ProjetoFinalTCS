package com.microsservicos.triagem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient; // Para @EnableDiscoveryClient
import org.springframework.cloud.openfeign.EnableFeignClients;           // Para @EnableFeignClients


@SpringBootApplication
//@EnableDiscoveryClient
//@EnableFeignClients(basePackages = "com.microsservicos.triagem.client")
public class TriagemApplication {

	public static void main(String[] args) {
		SpringApplication.run(TriagemApplication.class, args);
	}

}
