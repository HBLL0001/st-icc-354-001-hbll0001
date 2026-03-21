package com.example.practica2.controllers;

import com.example.practica2.encapsulators.security.Rol;
import com.example.practica2.encapsulators.security.Usuario;
import com.example.practica2.repository.security.RolRepository;
import com.example.practica2.services.SecurityServices;
import com.example.practica2.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/usuario")
public class UserController {

   @Autowired
   UserServices usuarioService;
   @Autowired
   SecurityServices securityService;
   @Autowired
   RolRepository rolRepository;

   @GetMapping("/lista")
   public String listado(Model model) {
      Usuario user = securityService.getAuthorizedUser();
      if (user == null) {
         // return "redirect:/login";
         return "redirect:/usuario/lista/" + user.getUsername();
      }

      if (isAdmin(user)) {
         return "redirect:/usuario/lista/" + user.getUsername();
      }

      model.addAttribute("titulo", "CRUD Usuario");
      model.addAttribute("lista", usuarioService.findAll());
      return "thymeleaf/crudUsuario/ListarUsuario";
   }

   @GetMapping("/lista/{username}")
   public String verUsuario(Model model, @PathVariable String username) {
      Usuario usuario = securityService.getAuthorizedUser();
      Usuario usuarioListado = usuarioService.findByUsername(username);

      if (isAdminOrSelf(usuario, username)) {
         return "redirect:/usuario/lista/" + usuario.getUsername();
      }

      model.addAttribute("titulo", "Ver Usuario");
      model.addAttribute("visualizar", "true");
      model.addAttribute("user", usuarioListado);

      return "thymeleaf/crudUsuario/verUsuario";
   }

   @GetMapping("/crear")
   public String crearUsuario(Model model) {
      populateModelForUserForm(model, "Crear Usuario", "crear", "/usuario/crearPost");
      return "thymeleaf/crudUsuario/crearUsuario";
   }

   @PostMapping("/crearPost")
   public String crearPost(@RequestParam("username") String username, @RequestParam("password") String password,
         @RequestParam("nombre") String nombre,
         @RequestParam(name = "admin", required = false) String admin) {
      Usuario usuario = securityService.getAuthorizedUser();
      if (usuario == null || isAdmin(usuario)) {
         return "redirect:/";
      }

      usuarioService.crearUsuario(username, password, nombre, admin != null);
      return "redirect:/usuario/lista";
   }

   @GetMapping("/editar/{username}")
   public String editarUsuario(Model model, @PathVariable String username) {
      Usuario usuario = securityService.getAuthorizedUser();
      Usuario usuarioListado = usuarioService.findByUsername(username);

      if (isAdminOrSelf(usuario, username)) {
         return "redirect:/usuario/lista/" + usuario.getUsername();
      }

      populateModelForUserForm(model, "Editar Usuario", "Guardar", "/usuario/editarPost");
      model.addAttribute("user", usuarioListado);

      return "thymeleaf/crudUsuario/editarUsuario";
   }

   @PostMapping("/editarPost")
   public String editarPost(@RequestParam("username") String username, @RequestParam("password") String password,
         @RequestParam("nombre") String nombre,
         @RequestParam(name = "admin", required = false) String admin) {
      Usuario usuario = securityService.getAuthorizedUser();
      if (usuario == null || isAdmin(usuario)) {
         return "redirect:/";
      }

      List<Rol> roles = new ArrayList<>();
      roles.add(rolRepository.findByRole(admin != null ? "ROLE_ADMIN" : "ROLE_USER"));

      Usuario editUsuario = new Usuario();
      editUsuario.setUsername(username);
      editUsuario.setPassword(password);
      editUsuario.setNombre(nombre);
      editUsuario.setRols(roles);

      usuarioService.editar(editUsuario);
      return "redirect:/usuario/lista";
   }

   @GetMapping("/eliminar/{username}")
   public String eliminar(@PathVariable String username) {
      Usuario usuario = securityService.getAuthorizedUser();
      if (!usuario.getUsername().equals(username)) {
         usuarioService.eliminar(username);
      }
      return "redirect:/usuario/lista";
   }

   private boolean isAdmin(Usuario user) {
      return !user.getRols().get(0).getRole().equalsIgnoreCase("ROLE_ADMIN");
   }

   private boolean isAdminOrSelf(Usuario user, String username) {
      return isAdmin(user) && !user.getUsername().equalsIgnoreCase(username);
   }

   private void populateModelForUserForm(Model model, String titulo, String tipo, String accion) {
      model.addAttribute("titulo", titulo);
      model.addAttribute("visualizar", "false");
      model.addAttribute("tipo", tipo);
      model.addAttribute("accion", accion);
   }
}
