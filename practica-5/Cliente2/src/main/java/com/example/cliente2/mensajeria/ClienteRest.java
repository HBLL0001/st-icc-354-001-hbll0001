package com.example.cliente2.mensajeria;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.jms.JmsException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@Component
public class ClienteRest {
    @Autowired
    private Productor pro;
    TipoCola colas;

    private static final String URL = "https://api.openweathermap.org/data/2.5/weather?lat=18.486043846610258&lon=-69.93139677855117&appid=4bcc7ac13c67d37391190c3ea14859fb&units=metric&lang=es";

    @Scheduled(fixedRate = 120000) //  2 minutos

    public void consultarAPI() throws JmsException, JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        Respuesta  response = restTemplate.getForObject(URL, Respuesta.class);
        ObjectMapper map = new ObjectMapper();
        map.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        response.setSd(new Date());

        System.out.println("Mensaje enviado");
        pro.send("pruebajms.cola",map.writeValueAsString(response),colas.TOPIC);
    }
}
