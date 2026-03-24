package com.example.practicaspringboot;

import com.example.practicaspringboot.services.StudentServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PracticaSpringbootApplication {
    @Autowired
    private StudentServices studentServices;

    public static void main(String[] args) {
        SpringApplication.run(PracticaSpringbootApplication.class, args);
    }

}
