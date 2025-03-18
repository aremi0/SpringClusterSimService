package com.aremi.springclustersimservice.controller;

import com.aremi.authentication.LoginRequestDTO;
import com.aremi.springclustersimservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Classe che gestisce gli Endpoint legati all'autenticazione
 */

@Slf4j
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService service;

    /**
     * Endpoint per la registrazione in database di un nuovo utente
     * @param registrationDTO DTO con dati cifrati con AES
     * @return Ritorna una stringa di successo o fallimento
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody LoginRequestDTO registrationDTO) {
        return service.registerNewUser(registrationDTO);
    }

    /**
     * Endpoint per la login di un utente
     * @param loginDTO DTO con dati cifrati con AES
     * @return Ritorna un token JWT se l'autenticazione avviene con successo
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO loginDTO) {
        return service.loginUser(loginDTO);
    }
}
