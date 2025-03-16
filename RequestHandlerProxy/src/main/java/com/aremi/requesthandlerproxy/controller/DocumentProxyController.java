package com.aremi.requesthandlerproxy.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
     * Endpoint che inoltra la richiesta verso il microservizio adibito più scarico
     *
     * @param userId
     * @return
     */
    @GetMapping("/first-page/{userId}")
    public ResponseEntity<String> getFirstPage(@PathVariable Long userId) {
        log.info("getFirstPage:: started with params: {}", userId);

        try {
            // Crea una richiesta con WebClient
            String response = webClientBuilder.build()
                    .get()
                    .uri("http://DocumentLoaderMicroservice/first-page/{userId}", userId)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(); // Bloccante: se hai bisogno di una risposta immediata

            log.info("getFirstPage:: [SUCCESS] Risposta ricevuta per userId: {}", userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("getFirstPage:: [ERROR] Errore durante la chiamata al microservizio per userId: {} | {}", userId, e.getMessage());
            return ResponseEntity.status(500).body("Errore durante il recupero della prima pagina.");
        }
    }
}
