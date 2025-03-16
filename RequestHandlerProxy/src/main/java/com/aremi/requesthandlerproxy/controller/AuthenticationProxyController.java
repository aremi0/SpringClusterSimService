package com.aremi.requesthandlerproxy.controller;

import com.aremi.authentication.LoginRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
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
     * Endpoint che inoltra la richiesta verso il microservizio adibito più scarico
     *
     * @param registrationDTO
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity<String> registrationRequest(@RequestBody @Validated LoginRequestDTO registrationDTO) {
        log.info("registrationRequest:: started with params: {}", registrationDTO);

        try {
            // Usa WebClient per inoltrare la richiesta al microservizio di autenticazione
            String response = webClientBuilder.build()
                    .post()
                    .uri("http://AuthenticationMicroservice/register")
                    .bodyValue(registrationDTO)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(); // Operazione bloccante

            log.info("registrationRequest:: [SUCCESS] Risposta ricevuta dal microservizio.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("registrationRequest:: [ERROR] Errore durante la chiamata al microservizio | {}", e.getMessage());
            return ResponseEntity.status(500).body("Errore durante la registrazione.");
        }
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

        try {
            // Usa WebClient per inoltrare la richiesta al microservizio di autenticazione
            String response = webClientBuilder.build()
                    .post()
                    .uri("http://AuthenticationMicroservice/login")
                    .bodyValue(loginRequestDTO)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(); // Operazione bloccante

            log.info("loginRequest:: [SUCCESS] Risposta ricevuta dal microservizio.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("loginRequest:: [ERROR] Errore durante la chiamata al microservizio | {}", e.getMessage());
            return ResponseEntity.status(500).body("Errore durante il login.");
        }
    }
}

