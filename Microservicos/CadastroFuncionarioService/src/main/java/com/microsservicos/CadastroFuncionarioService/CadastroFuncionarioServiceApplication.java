package com.microsservicos.CadastroFuncionarioService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CadastroFuncionarioServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(CadastroFuncionarioServiceApplication.class, args);
	}
}
