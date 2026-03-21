package com.example.practica2.repository;
import com.example.practica2.encapsulators.Mockup;
import com.example.practica2.encapsulators.security.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MockyRepository extends JpaRepository<Mockup, Long> {
    List<Mockup> findAll();
    List<Mockup> findAllByUsuario(Usuario user);
    void deleteById(Long id);

    Optional<Mockup> findByCodigoAndNombre(String codigo, String nombre);

    Optional<Mockup> findById(Long id);
    //void updateBy(Mockup moki)

}
