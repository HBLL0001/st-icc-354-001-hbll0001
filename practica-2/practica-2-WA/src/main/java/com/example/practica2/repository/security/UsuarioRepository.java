package com.example.practica2.repository.security;

import com.example.practica2.encapsulators.security.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, String> {

    Usuario findByUsername(String username);

    Usuario findUsuarioByUsernameAndPassword(String username, String password);

}
