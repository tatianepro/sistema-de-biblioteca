package com.github.tatianepro.biblioteca;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableScheduling
public class BibliotecaApplication {

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	@Scheduled(cron = "30 45 11 1/1 * ?")	// http://www.cronmaker.com/
	public void ScheduledTest() {
		System.out.println("TESTE DE AGENDAMENTO FUNCIONANDO COM SUCESSO!!!");
	}

	public static void main(String[] args) {
		SpringApplication.run(BibliotecaApplication.class, args);
	}

}
