package com.example.receptor.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class controladorweb {
    @GetMapping("/")
    public String index() {
        return "index.html";
    }
}
