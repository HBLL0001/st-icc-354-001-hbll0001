package com.example.practica2.encapsulators;


import com.example.practica2.encapsulators.security.Usuario;
import com.example.practica2.services.JwtServices;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;

import static javax.crypto.Cipher.SECRET_KEY;

@Entity
public class Mockup {


    @Id
    @GeneratedValue
    private Long id;

    private String nombre;
    private String descripcion;
    private String status;
    private String metodo;
    private String content;
    @Lob
    private String headers;
    @Lob
    private String body;
    @DateTimeFormat
    private LocalDateTime exp;
    private int demora;

    private String codigo;

    private boolean seguridad;

    @ManyToOne
    Usuario usuario;

    public Mockup(){}

    public Mockup(String nombre, String descripcion, String status, String metodo, String content, String headers, String body, LocalDateTime exp, int demora, String codigo, boolean seguridad, Usuario usuario) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.status = status;
        this.metodo = metodo;
        this.content = content;
        this.headers = headers;
        this.body = body;
        this.exp = exp;
        this.demora = demora;
        this.codigo = codigo;
        this.seguridad = seguridad;
        this.usuario = usuario;
    }
    public boolean isSeguridad() {
        return seguridad;
    }

    public void setSeguridad(boolean seguridad) {
        this.seguridad = seguridad;
    }
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMetodo() {
        return metodo;
    }

    public void setMetodo(String metodo) {
        this.metodo = metodo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public LocalDateTime getExp() {
        return exp;
    }

    public void setExp(LocalDateTime exp) {
        this.exp = exp;
    }

    public int getDemora() {
        return demora;
    }

    public void setDemora(int demora) {
        this.demora = demora;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String expParseada(){
        DateTimeFormatter nt = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");
        return this.exp.format(nt);
    }
    public String gettoken(){
        JwtServices jw = new JwtServices();
        return jw.generateJwt(this);
    }


}

