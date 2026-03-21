package com.example.practica2;

import com.example.practica2.services.UserServices;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Practica2Application {
	public static void main(String[] args) {
		ApplicationContext applicationContext = SpringApplication.run(Practica2Application.class, args);
		UserServices usuarioService = (UserServices) applicationContext.getBean("userServices");
		usuarioService.initializeUsuario();
	}
}
