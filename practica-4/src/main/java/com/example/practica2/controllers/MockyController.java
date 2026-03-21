package com.example.practica2.controllers;

import com.example.practica2.encapsulators.Mockup;
import com.example.practica2.encapsulators.security.Usuario;
import com.example.practica2.services.JwtServices;
import com.example.practica2.services.MockyServices;
import com.example.practica2.services.SecurityServices;
import com.example.practica2.services.UserServices;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/mockycrud")
public class MockyController {
   @Autowired
   JwtServices sjwt;
   @Autowired
   MockyServices mock;
   @Autowired
   private HttpServletRequest request;
   @Autowired
   SecurityServices securityService;
   @Autowired
   UserServices usuarioService;

   @GetMapping("/")
   public String listado(Model model) {
      model.addAttribute("titulo", "CRUD Mocky");
      Usuario usuario = securityService.getAuthorizedUser();
      model.addAttribute("lista", usuario.getRols().get(0).getRole().equalsIgnoreCase("ROLE_ADMIN") ? mock.buscarTodo()
            : mock.buscarTodoByUsuario(usuario));
      model.addAttribute("jwt", sjwt);
      model.addAttribute("url", getBaseUrl());
      return "thymeleaf/listar";
   }

   private String getBaseUrl() {
      return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/api/";
   }

   @GetMapping("/crear")
   public String crearmodel(Model model) {
      populateModelForCreateOrEdit(model, "crear", "/mockycrud/creacion", "GET", "200", "application/json");
      return "thymeleaf/crearEditarVisualizar";
   }

   private void populateModelForCreateOrEdit(Model model, String tipo, String accion, String selectmet,
         String selectstat, String conselect) {
      model.addAttribute("headerplace",
            "{\n  \"saludo\": \"Hola\",\n  \"mensaje\": \"¡Bienvenido al mundo de los encabezados!\"\n}");
      model.addAttribute("bodyplace",
            "{\n  \"saludo\": \"Hola\",\n  \"mensaje\": \"¡Bienvenido al mundo de los body!\"\n}");
      model.addAttribute("titulo", "CRUD Mocky");
      model.addAttribute("visualizar", "false");
      model.addAttribute("listStatus", getStatusList());
      model.addAttribute("metodo", getMethodList());
      model.addAttribute("listExp", getExpirationList());
      model.addAttribute("listDem", getDelayList());
      model.addAttribute("content", getContentList());
      model.addAttribute("accion", accion);
      model.addAttribute("selectexp", "año");
      model.addAttribute("selectmet", selectmet);
      model.addAttribute("selectstat", selectstat);
      model.addAttribute("conselect", conselect);
      model.addAttribute("tipo", tipo);
   }

   private List<String> getStatusList() {
      return List.of("100", "101", "102", "200", "201", "202", "203", "204", "205", "206", "207", "208", "226", "300",
            "301", "302", "303", "304", "305", "307", "308", "400", "401", "402", "403", "404", "405", "406", "407",
            "408", "409", "410", "411", "412", "413", "414", "415", "416", "417", "418", "421", "422", "423", "424",
            "426", "428", "429", "431", "451", "500", "501", "502", "503", "504", "505", "506", "507", "508", "510",
            "511");
   }

   private List<String> getMethodList() {
      return List.of("GET", "POST", "PUT", "PATH", "DELETE", "HEAD", "OPTIONS");
   }

   private List<String> getExpirationList() {
      return List.of("minuto", "hora", "dia", "semana", "mes", "año");
   }

   private List<String> getDelayList() {
      return List.of("0", "1", "5", "10", "30", "40", "60");
   }

   private List<String> getContentList() {
      return List.of("text/plain", "text/html", "application/json", "application/xml");
   }

   @GetMapping("/ver/{name}/{codigo}")
   public String verUrlModel(Model model, @PathVariable String name, @PathVariable String codigo,
         @RequestParam("token") String token) {
      model.addAttribute("titulo", "Tu Mocky");
      String baseUrl = getBaseUrl() + name + "/" + codigo + "?token=" + token;
      model.addAttribute("url", baseUrl);
      return "thymeleaf/showURL";
   }

   @PostMapping("/creacion")
   public String crearMocky(@RequestParam("status") String status, @RequestParam("metodo") String metodo,
         @RequestParam("content") String content, @RequestParam("nombre") String nombre,
         @RequestParam("desc") String desc, @RequestParam("headers") String headers,
         @RequestParam("body") String body, @RequestParam("exp") String exp, @RequestParam("dem") int dem,
         @RequestParam("seguridadJwt") boolean sec) {
      LocalDateTime fechaActual = calculateExpirationDate(exp);
      UUID uuid = UUID.randomUUID();
      Usuario usuario = securityService.getAuthorizedUser();
      Mockup temp = new Mockup(nombre, desc, status, metodo, content, headers, body, fechaActual, dem,
            uuid.toString().replaceAll("[^a-zA-Z0-9]", ""), sec, usuario);
      mock.crearMockup(temp);
      return sec
            ? "redirect:/mockycrud/ver/" + temp.getNombre() + "/" + temp.getCodigo() + "?token="
                  + sjwt.generateJwt(temp)
            : "redirect:/mockycrud/ver/" + temp.getNombre() + "/" + temp.getCodigo() + "?token=";
   }

   private LocalDateTime calculateExpirationDate(String exp) {
      LocalDateTime fechaActual = LocalDateTime.now();
      switch (exp) {
         case "minuto":
            return fechaActual.plus(1, ChronoUnit.MINUTES);
         case "hora":
            return fechaActual.plus(1, ChronoUnit.HOURS);
         case "dia":
            return fechaActual.plusDays(1);
         case "semana":
            return fechaActual.plusWeeks(1);
         case "mes":
            return fechaActual.plusMonths(1);
         case "año":
            return fechaActual.plusYears(1);
         default:
            return fechaActual;
      }
   }

   @GetMapping("/editar/{id}")
   public String editar(Model model, @PathVariable long id) {
      Mockup mm = mock.buscarMockById(id);
      populateModelForCreateOrEdit(model, "editar", "/mockycrud/modificar/" + mm.getId(), mm.getMetodo(),
            mm.getStatus(), mm.getContent());
      return getString(model, mm);
   }

   @PostMapping("/modificar/{id}")
   public String modificar(@PathVariable long id, @RequestParam("status") String status,
         @RequestParam("metodo") String metodo,
         @RequestParam("content") String content, @RequestParam("nombre") String nombre,
         @RequestParam("desc") String desc, @RequestParam("headers") String headers,
         @RequestParam("body") String body, @RequestParam("exp") String exp, @RequestParam("dem") int dem,
         @RequestParam("seguridadJwt") boolean sec) {
      LocalDateTime fechaActual = calculateExpirationDate(exp);
      Mockup temp = mock.buscarMockById(id);
      temp.setNombre(nombre);
      temp.setDescripcion(desc);
      temp.setStatus(status);
      temp.setMetodo(metodo);
      temp.setContent(content);
      temp.setHeaders(headers);
      temp.setBody(body);
      temp.setExp(fechaActual);
      temp.setDemora(dem);
      temp.setSeguridad(sec);
      mock.crearMockup(temp);
      return "redirect:/mockycrud/";
   }

   @GetMapping("/eliminar/{id}")
   public String eliminar(@PathVariable long id) {
      mock.eliminarMockById(id);
      return "redirect:/mockycrud/";
   }

   @GetMapping("/visualizar/{id}")
   public String visualizar(Model model, @PathVariable long id) {
      Mockup mm = mock.buscarMockById(id);
      populateModelForCreateOrEdit(model, "Ver", "", mm.getMetodo(), mm.getStatus(), mm.getContent());
      return getString(model, mm);
   }

   private String getString(Model model, Mockup mm) {
      model.addAttribute("fname", mm.getNombre());
      model.addAttribute("fdesc", mm.getDescripcion());
      model.addAttribute("fheaders", mm.getHeaders());
      model.addAttribute("fbody", mm.getBody());
      model.addAttribute("selecdem", mm.getDemora());
      return "thymeleaf/crearEditarVisualizar";
   }
}
