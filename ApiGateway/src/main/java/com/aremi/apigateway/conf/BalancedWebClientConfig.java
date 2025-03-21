package com.aremi.apigateway.conf;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Classe di configurazione di un WebClient balanced tramite Spring Cloud e Eureka (Spring WebFlux)
 */

@Configuration
public class BalancedWebClientConfig {
    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
