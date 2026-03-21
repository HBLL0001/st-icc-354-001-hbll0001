package com.example.practica2.services;


import com.example.practica2.encapsulators.Mockup;
import com.example.practica2.encapsulators.security.Usuario;
import com.example.practica2.repository.MockyRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MockyServices {
    @Autowired
    private MockyRepository repo;

    @Transactional
    public Mockup crearMockup( Mockup mock){
        repo.save(mock);
        return mock;
    }
    @Transactional
    public List<Mockup> buscarTodo(){
        return repo.findAll();
    }
    @Transactional
    public List<Mockup> buscarTodoByUsuario(Usuario ususario){
        return repo.findAllByUsuario(ususario);
    }
    @Transactional
    public Mockup buscarMockById(Long id){
        Optional<Mockup> mcc= repo.findById(id);
        return mcc.orElse(null);
    }
    @Transactional
    public void eliminarMockById(Long id){
        repo.deleteById(id);
    }
    @Transactional
    public Mockup buscarCodigoAndNombre(String nombre,String codigo){
        Optional<Mockup> mcc= repo.findByCodigoAndNombre(codigo,nombre);
        return mcc.orElse(null);
    }


}
