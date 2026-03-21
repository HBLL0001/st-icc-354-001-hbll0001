package com.example.practica2.repository.security;

import com.example.practica2.encapsulators.security.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolRepository extends JpaRepository<Rol, String> {

    Rol findByRole(String rol);
}
