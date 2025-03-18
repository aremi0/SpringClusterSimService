package com.aremi.requesthandlerproxy.controller;

import com.aremi.authentication.LoginRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Classe PROXY che gestisce gli Endpoint legati all'autenticazione.
 */

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthenticationProxyController {

    private final WebClient.Builder webClientBuilder;

    /**
     * Endpoint che intercetta le richieste del container Prometheus per le metriche del cluster AuthenticationMicroservice
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
                .uri("http://AuthenticationMicroservice/actuator/prometheus")
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
     * @param registrationDTO
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity<String> registrationRequest(@RequestBody @Validated LoginRequestDTO registrationDTO) {
        log.info("registrationRequest:: started with params: {}", registrationDTO);

        return webClientBuilder.build()
                .post()
                .uri("http://AuthenticationMicroservice/register")
                .bodyValue(registrationDTO)
                .retrieve()
                .toEntity(String.class)
                .block(); // Non serve gestione specifica degli errori, è nel RestControllerAdvice

    }

    /**
     * Endpoint che inoltra la richiesta verso il microservizio adibito più scarico
     *
     * @param loginRequestDTO
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<String> loginRequest(@RequestBody @Validated LoginRequestDTO loginRequestDTO) {
        log.info("loginRequest:: started with params: {}", loginRequestDTO);

        return webClientBuilder.build()
                .post()
                .uri("http://AuthenticationMicroservice/login")
                .bodyValue(loginRequestDTO)
                .retrieve()
                .toEntity(String.class)
                .block(); // Non serve gestione specifica degli errori, è nel RestControllerAdvice
    }

}

