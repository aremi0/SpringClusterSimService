package com.aremi.springclustersimservice.service;

import com.aremi.springclustersimservice.model.UserEntity;
import com.aremi.springclustersimservice.dto.LoginRequestDTO;
import com.aremi.springclustersimservice.repository.UserRepository;
import com.aremi.springclustersimservice.util.DecryptUtil;
import com.aremi.springclustersimservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    /**
     * Salva il nuovo utente sul DB
     * @param registrationDTO DTO con dati cifrati con AES
     * @return Ritorna una stringa di successo o fallimento
     */
    public ResponseEntity<String> registerNewUser(LoginRequestDTO registrationDTO) {
        log.info("registerNewUser:: started");

        String decryptedUsername;
        String decryptedPassword;

        try {
            decryptedUsername = DecryptUtil.decrypt(registrationDTO.getUsername());
            decryptedPassword = DecryptUtil.decrypt(registrationDTO.getPassword());
        } catch (Throwable e) {
            log.error("registerNewUser:: [ERROR] decryption failed", e);
            return ResponseEntity.internalServerError().body("Error during decryption");
        }

        if(userRepository.existsByUsername(decryptedUsername)) {
            log.error("registerNewUser:: [ERROR] username already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
        }

        String hashedPassword = DecryptUtil.hashPassword(decryptedPassword);
        UserEntity userEntity = UserEntity.builder()
                .username(decryptedUsername)
                .hashedPassword(hashedPassword)
                .build();

        userRepository.save(userEntity);
        log.info("registerNewUser:: [SUCCESS] user registered successfully");
        return ResponseEntity.ok("User registered successfully");
    }

    /**
     * Verifica validità credenziali per la login dell'utenza
     * @param loginDTO DTO con dati cifrati con AES
     * @return Ritorna un token JWT se l'autenticazione avviene con successo
     */
    public ResponseEntity<String> logUser(LoginRequestDTO loginDTO) {
        log.info("logUser:: started");

        String decryptedUsername;
        String decryptedPassword;

        try {
            decryptedUsername = DecryptUtil.decrypt(loginDTO.getUsername());
            decryptedPassword = DecryptUtil.decrypt(loginDTO.getPassword());
        } catch (Throwable e) {
            log.error("logUser:: [ERROR] decryption failed", e);
            return ResponseEntity.internalServerError().body("Error during decryption");
        }

        String dbHashedPwd = userRepository.findHashedPasswordByUsername(decryptedUsername);
        if(Objects.isNull(dbHashedPwd)) {
            log.warn("logUser:: [WARNING] user not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        var isPasswordCorrect = DecryptUtil.checkPassword(decryptedPassword, dbHashedPwd);

        if(isPasswordCorrect) {
            log.info("logUser:: [SUCCESS] user logged in successfully");
            String token = JwtUtil.generateToken(decryptedUsername);
            log.info("logUser:: token created successfully");
            return ResponseEntity.ok(token);
        } else {
            log.warn("logUser:: [WARNING] password verification failed");
            return ResponseEntity.badRequest().body("Password verification failed");
        }
    }
}
