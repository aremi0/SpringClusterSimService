package com.aremi.documentloadermicroservice.conf;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

/**
 * Classe di configurazione del Consumer RabbitMQ per la coda "auth-routing-key".
 * La coda "auth-queue" riceverà messaggi dall'exchange "auth-exchange" usando la routing key "auth-routing-key".
 */

@Configuration
public class RabbitMQConfig {
    @Value("${rabbitmq.queue.name}")
    private String queueName;
    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;
    @Value("${rabbitmq.routing-key}")
    private String routingKey;

    @Bean
    public Queue authQueue() {
        return new Queue(queueName, true); // Coda persistente
    }

    @Bean
    public DirectExchange authExchange() {
        return new DirectExchange(exchangeName);
    }

    @Bean
    public Binding binding(Queue authQueue, DirectExchange authExchange) {
        return BindingBuilder.bind(authQueue).to(authExchange).with(routingKey);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
