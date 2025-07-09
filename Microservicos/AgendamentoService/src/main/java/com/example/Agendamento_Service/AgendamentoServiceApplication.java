package com.example.Agendamento_Service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class AgendamentoServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AgendamentoServiceApplication.class, args);
	}

}
