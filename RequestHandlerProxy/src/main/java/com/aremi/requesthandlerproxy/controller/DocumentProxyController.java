package com.aremi.requesthandlerproxy.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Classe PROXY che gestisce gli Endpoint legati alla consultazione dei documenti.
 */

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/documents")
public class DocumentProxyController {

    private final WebClient.Builder webClientBuilder;

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
        String response = webClientBuilder.build()
                .get()
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
    public ResponseEntity<String> getFirstPage(@PathVariable Long userId) {
        log.info("getFirstPage:: started with params: {}", userId);

            // Crea una richiesta con WebClient
            return webClientBuilder.build()
                    .get()
                    .uri("http://DocumentLoaderMicroservice/first-pages/{userId}", userId)
                    .retrieve()
                    .toEntity(String.class)
                    .block(); // Bloccante: se hai bisogno di una risposta immediata

    }
}
