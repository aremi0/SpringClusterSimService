package com.aremi.apigateway.controller;

import com.aremi.apigateway.annotation.Retryable;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * Classe PROXY che gestisce gli Endpoint legati alla consultazione dei documenti.
 */

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/documents")
public class DocumentProxyController {

    private final WebClient balancedWebClient;

    /**
     * Endpoint che intercetta le richieste del container Prometheus per le metriche del cluster DocumentLoaderMicroservice
     * e le instrada verso il cluster
     *
     * @return
     */
    @GetMapping("/prometheus")
    public ResponseEntity<String> prometheusMetrics() {
        log.info("prometheusMetrics:: started");

        // Usa WebClient per inoltrare la richiesta all'endpoint del microservizio di autenticazione
        String response = balancedWebClient.get()
                .uri("http://DocumentLoaderMicroservice/actuator/prometheus")
                .retrieve()
                .bodyToMono(String.class)
                .block(); // Operazione bloccante

        log.info("prometheusMetrics:: [SUCCESS] Risposta ricevuta dal microservizio. Forcing EOF...");
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(response + "\n# EOF");
    }

    /**
     * Endpoint che inoltra la richiesta verso il microservizio adibito più scarico
     *
     * @param userId
     * @return
     */
    @GetMapping("/first-pages/{userId}")
    @Retryable(maxAttempts = 3, retryOnExceptions = {WebClientResponseException.class})
    public ResponseEntity<String> getFirstPage(@PathVariable Long userId,
                                               HttpSession session) {
        String sessionId = session.getId();
        return balancedWebClient.get()
                .uri("http://DocumentLoaderMicroservice/first-pages/{userId}", userId)
                .header("Session-ID", sessionId)
                .retrieve()
                .toEntity(String.class)
                .block(); // Bloccante: se hai bisogno di una risposta immediata
    }
}
