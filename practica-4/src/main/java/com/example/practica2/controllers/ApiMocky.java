package com.example.practica2.controllers;

import com.example.practica2.encapsulators.Mockup;
import com.example.practica2.services.JwtServices;
import com.example.practica2.services.MockyServices;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api")
public class ApiMocky {
   @Autowired
   MockyServices mockS;
   @Autowired
   JwtServices jwts;

   @RequestMapping("/{name}/{codigo}")
   public ResponseEntity<String> consulta(@PathVariable String name, @PathVariable String codigo,
         @RequestParam("token") String token) {
      Mockup omock = mockS.buscarCodigoAndNombre(name, codigo);
      logCurrentTimeAndExpiration(omock);

      if (omock.isSeguridad() && !jwts.generateJwt(omock).equals(token)) {
         return redirectToMockyCrud();
      }

      if (LocalDateTime.now().isBefore(omock.getExp())) {
         return createResponseEntity(omock);
      } else {
         return redirectToMockyCrud();
      }
   }

   private void logCurrentTimeAndExpiration(Mockup omock) {
      DateTimeFormatter nt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
      System.out.println(LocalDateTime.now().format(nt) + " otra: " + omock.getExp().format(nt));
   }

   private ResponseEntity<String> redirectToMockyCrud() {
      HttpHeaders headers = new HttpHeaders();
      headers.add("Location", "/mockycrud/");
      return new ResponseEntity<>(headers, HttpStatus.FOUND);
   }

   private ResponseEntity<String> createResponseEntity(Mockup omock) {
      MediaType mediaType = getMediaType(omock.getContent());
      HttpHeaders headers = createHeaders(omock.getHeaders());

      try {
         Thread.sleep(omock.getDemora() * 1000);
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
         e.printStackTrace();
      }

      String body = omock.getBody().isEmpty() ? "{}" : omock.getBody();
      return ResponseEntity.status(Integer.parseInt(omock.getStatus()))
            .contentType(mediaType)
            .headers(headers)
            .body(body);
   }

   private MediaType getMediaType(String content) {
      switch (content) {
         case "text/plain":
            return MediaType.TEXT_PLAIN;
         case "text/html":
            return MediaType.TEXT_HTML;
         case "application/json":
            return MediaType.APPLICATION_JSON;
         case "application/xml":
            return MediaType.APPLICATION_XML;
         default:
            return MediaType.APPLICATION_OCTET_STREAM;
      }
   }

   private HttpHeaders createHeaders(String headersJsonString) {
      HttpHeaders headers = new HttpHeaders();
      if (!headersJsonString.isEmpty()) {
         JSONObject headersJson = new JSONObject(headersJsonString);
         for (String key : headersJson.keySet()) {
            headers.add(key, headersJson.getString(key));
         }
      }
      return headers;
   }
}