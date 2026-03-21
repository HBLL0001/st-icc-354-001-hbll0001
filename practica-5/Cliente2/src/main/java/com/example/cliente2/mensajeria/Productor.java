package com.example.cliente2.mensajeria;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class Productor {

    private final JmsTemplate jmsTemplate;

    public Productor(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void send(String cola, String mensajeEnviar, TipoCola tipoCola) {
        System.out.println("Enviando Mensaje - Cola: "+tipoCola.toString());
        jmsTemplate.convertAndSend(cola, mensajeEnviar);
    }
}
