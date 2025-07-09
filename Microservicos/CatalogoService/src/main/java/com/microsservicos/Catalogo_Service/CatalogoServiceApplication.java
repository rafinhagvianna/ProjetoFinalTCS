package com.microsservicos.Catalogo_Service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
@EnableFeignClients
public class CatalogoServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CatalogoServiceApplication.class, args);
	}

}
