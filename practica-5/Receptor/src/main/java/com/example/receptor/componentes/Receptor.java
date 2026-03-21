package com.example.receptor.componentes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
@Component
public class Receptor {

    private final SimpMessagingTemplate messagingTemplate;

    public Receptor(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    @JmsListener(destination = "pruebajms.cola")

    public void receiveMessage(String message) {
        messagingTemplate.convertAndSend("/topic/greetings", message);
        System.out.println("Mensaje recibido: " + message);
    }
}
