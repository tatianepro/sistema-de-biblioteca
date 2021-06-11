package com.github.tatianepro.biblioteca;

import com.github.tatianepro.biblioteca.service.EmailService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@EnableScheduling
public class BibliotecaApplication {

	@Autowired
	private EmailService emailService;

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	@Bean
	public CommandLineRunner runner() {
		return args -> {
			List<String> emails = Arrays.asList("user-975177@inbox.mailtrap.io");
			emailService.sendMails("Testando serviço de email", emails);
			System.out.println("EMAIL ENVIADO!");
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(BibliotecaApplication.class, args);
	}

}
