package com.example.practica2.services;

import com.example.practica2.encapsulators.security.Rol;
import com.example.practica2.encapsulators.security.Usuario;
import com.example.practica2.repository.security.RolRepository;
import com.example.practica2.repository.security.UsuarioRepository;
import lombok.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UserServices {

   UsuarioRepository usuarioRepository;
   RolRepository rolRepository;
   PasswordEncoder passwordEncoder;

   UserServices(RolRepository rolRepository, UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
      this.rolRepository = rolRepository;
      this.usuarioRepository = usuarioRepository;
      this.passwordEncoder = passwordEncoder;
   }

   public Usuario crearUsuario(@NonNull String username, @NonNull String password, @NonNull String nombre,
         boolean admin, boolean activo) {

      Rol rolAdmin = rolRepository.findByRole("ROLE_ADMIN");
      Rol rolSUser = rolRepository.findByRole("ROLE_USER");

      Usuario user = new Usuario();
      user.setUsername(username);
      user.setPassword(passwordEncoder.encode(password));
      user.setNombre(nombre);
      user.setActivo(activo);
      if (admin) {
         user.setRols(Arrays.asList(rolAdmin));
      } else {
         user.setRols(Arrays.asList(rolSUser));
      }
      usuarioRepository.save(user);
      return user;
   }

   public Usuario crearUsuario(@NonNull String username, @NonNull String password, @NonNull String nombre,
         boolean admin) {
      return crearUsuario(username, password, nombre, admin, true);
   }

   public Usuario findByUsername(@NonNull String username) {
      return usuarioRepository.findByUsername(username);
   }

   public Usuario findUsuarioByUsernameAndPassword(String username, String password) {
      return usuarioRepository.findUsuarioByUsernameAndPassword(username, password);
   }

   public void eliminar(String username) {
      Usuario usuario = usuarioRepository.findByUsername(username);
      if (usuario != null) {
         usuarioRepository.delete(usuario);
      }
   }

   public void editar(Usuario usuario) {
      usuarioRepository.save(usuario);
   }

   public List<Usuario> findAll() {
      return usuarioRepository.findAll();
   }

   public void initializeUsuario() {

      System.out.println("Creación del usuario y rol en la base de datos");

      Rol rolAdmin = new Rol("ROLE_ADMIN");
      rolRepository.save(rolAdmin);

      Rol rolUsuario = new Rol("ROLE_USER");
      rolRepository.save(rolUsuario);

      crearUsuario("admin", "admin", "admin1", true);
      crearUsuario("user", "user", "user1", false);
   }
}
