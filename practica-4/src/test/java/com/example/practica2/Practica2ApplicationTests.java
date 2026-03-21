package com.example.practica2;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;

@SpringBootTest
class Practica2ApplicationTests {

	// Simular el AuthenticationManager durante las pruebas
	@MockBean
	private AuthenticationManager authenticationManager;

	@Test
	void contextLoads() {
		// Prueba que el contexto de la aplicación se carga correctamente
	}
}
