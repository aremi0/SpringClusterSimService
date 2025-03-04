package com.aremi.springclustersimservice.controller;

import com.aremi.springclustersimservice.dto.LoginRequestDTO;
import com.aremi.springclustersimservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService service;

    /**
     * Metodo per la registrazione in database di un nuovo utente
     * @param registrationDTO DTO con dati cifrati con AES
     * @return Ritorna una stringa di successo o fallimento
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Validated LoginRequestDTO registrationDTO) {
        log.info("register:: started with params: {}", registrationDTO);
        return service.registerNewUser(registrationDTO);
    }

    /**
     * Metodo per la login di un utente
     * @param loginDTO DTO con dati cifrati con AES
     * @return Ritorna un token JWT se l'autenticazione avviene con successo
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Validated LoginRequestDTO loginDTO) {
        log.info("login:: started with params: {}", loginDTO);
        return service.logUser(loginDTO);
    }
}
