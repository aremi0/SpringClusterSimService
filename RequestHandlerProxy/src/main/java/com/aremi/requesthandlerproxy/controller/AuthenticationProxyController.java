package com.aremi.requesthandlerproxy.controller;

import com.aremi.authentication.LoginRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthenticationProxyController {
    private final RestTemplate restTemplate;

    @PostMapping("/register")
    public ResponseEntity<String> registrationRequest(@RequestBody @Validated LoginRequestDTO registrationDTO) {
        log.info("registrationRequest:: started with params: {}", registrationDTO);
        // Inoltra la richiesta al microservizio di autenticazione
        String response = restTemplate.postForObject("http://AuthenticationMicroservice/register", registrationDTO, String.class);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginRequest(@RequestBody @Validated LoginRequestDTO loginRequestDTO) {
        log.info("loginRequest:: started with params: {}", loginRequestDTO);
        // Inoltra la richiesta al microservizio di autenticazione
        String response = restTemplate.postForObject("http://authenticationmicroservice/login", loginRequestDTO, String.class);
        return ResponseEntity.ok(response);
    }
}
