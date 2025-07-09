package com.microsservicos.documentacao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class DocumentacaoServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DocumentacaoServiceApplication.class, args);
	}

}
