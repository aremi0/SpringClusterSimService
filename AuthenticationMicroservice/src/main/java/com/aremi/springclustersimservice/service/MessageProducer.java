package com.aremi.springclustersimservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * Classe per lo scambio di messaggi tramite server RabbitMQ (protocollo AMQP)
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageProducer {
    // private final AmqpTemplate amqpTemplate; // Interfaccia generica per lo scambio di messaggi tramite protocollo AMQP (Fornisce meno metodi più generici)
    private final RabbitTemplate rabbitTemplate; // Implementazione concreta di AmqpTemplate specifica per RabbitMQ (Fornisce più metodi specifici)

    /**
     * Metodi che permette di inviare un messaggio alla coda senza dover scrivere manualmente tutta la logica per la
     * conversione degli oggetti.
     *
     * Cosa avviene dietro le quinte:
     * 1. Serializzazione: convertAndSend usa un convertitore di messaggi predefinito, come il SimpleMessageConverter,
     * per trasformare l'oggetto in un formato AMQP (tipicamente byte o JSON).
     * 2. Invio al broker: Il messaggio viene inviato a RabbitMQ attraverso l'Exchange specificato.
     * 3. Routing: RabbitMQ utilizza la routing key e il tipo di Exchange (es. Direct, Topic, Fanout) per inviare il
     * messaggio alla coda corretta.
     *
     * exchange: Nome dell'Exchange su RabbitMQ
     * routingKey: Chiave di routing per indirizzare il messaggio verso la coda desiderata
     * message: Oggetto da inviare
     * MessagePostProcessor lambda: Permette di accedere e modificare le proprietà del messaggio
     */
    public void sendSuccessfulAuthenticationMessage(Long userId) {
        rabbitTemplate.convertAndSend("auth-exchenge", "auth-routing-key", userId, msg -> {
            msg.getMessageProperties().setHeader("eventType", "successfulAuthentication");
            msg.getMessageProperties().setHeader("timestamp", System.currentTimeMillis());
            msg.getMessageProperties().setContentType("text/plain");
            return msg;
        });
    }
}
