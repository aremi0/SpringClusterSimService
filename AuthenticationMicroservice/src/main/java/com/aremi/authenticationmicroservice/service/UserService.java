package com.aremi.authenticationmicroservice.service;

import com.aremi.authenticationmicroservice.exception.DecryptionException;
import com.aremi.authenticationmicroservice.exception.user.InvalidPasswordException;
import com.aremi.authenticationmicroservice.exception.user.UserNotFoundException;
import com.aremi.authenticationmicroservice.exception.user.UsernameAlreadyExistsException;
import com.aremi.authenticationmicroservice.model.UserEntity;
import com.aremi.authentication.LoginRequestDTO;
import com.aremi.authenticationmicroservice.repository.UserRepository;
import com.aremi.authenticationmicroservice.util.DecryptUtil;
import com.aremi.authenticationmicroservice.util.JwtInternalMap;
import com.aremi.authenticationmicroservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Classe che gestisce la logica di business legata all'autenticazione
 */

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final MessageProducer messageProducer;

    /**
     * Salva il nuovo utente sul DB. Riceve Username e Password cifrati con AES, li decifra e salva i dati a DB nel seguente
     * modo:
     *      username: in chiaro
     *      password: viene decifrata da AES in chiaro e poi da in chiaro viene hashata on sale tramite BCrypt, poi salvata hashata.
     *
     * @param registrationDTO DTO con dati cifrati con AES
     * @return Ritorna una stringa di successo o fallimento
     */
    public ResponseEntity<String> registerNewUser(LoginRequestDTO registrationDTO) {
        String decryptedUsername;
        String decryptedPassword;

        try {
            decryptedUsername = DecryptUtil.decrypt(registrationDTO.getUsername());
            decryptedPassword = DecryptUtil.decrypt(registrationDTO.getPassword());
        } catch (Throwable e) {
            throw new DecryptionException("Error during decryption", e);
        }

        if(userRepository.existsByUsername(decryptedUsername)) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }

        String hashedPassword = DecryptUtil.hashPassword(decryptedPassword);
        UserEntity userEntity = UserEntity.builder()
                .username(decryptedUsername)
                .hashedPassword(hashedPassword)
                .build();

        userRepository.save(userEntity);
        return ResponseEntity.ok("User registered successfully");
    }


    /**
     * Verifica validità credenziali per la login dell'utenza
     * @param loginDTO DTO con dati cifrati con AES
     * @return Ritorna un token JWT se l'autenticazione avviene con successo
     */
    public ResponseEntity<String> loginUser(LoginRequestDTO loginDTO) {
        String decryptedUsername;
        String decryptedPassword;

        // Decrypt username e password
        try {
            decryptedUsername = DecryptUtil.decrypt(loginDTO.getUsername());
            decryptedPassword = DecryptUtil.decrypt(loginDTO.getPassword());
        } catch (Throwable e) {
            throw new DecryptionException("Error during decryption", e);
        }

        // Recupera la password hashata dal DB
        String dbHashedPwd = userRepository.findHashedPasswordByUsername(decryptedUsername);
        if (Objects.isNull(dbHashedPwd)) {
            throw new UserNotFoundException("User not found");
        }

        // Verifica la password
        if (!DecryptUtil.checkPassword(decryptedPassword, dbHashedPwd)) {
            throw new InvalidPasswordException("Password verification failed");
        }

        // Genera il token e aggiorna RabbitMQ
        var token = JwtUtil.generateToken(decryptedUsername);
        JwtInternalMap.addOrUpdateValue(decryptedUsername, token);
        var userId = userRepository.findIdByUsername(decryptedUsername);
        messageProducer.sendSuccessfulAuthenticationMessage(userId);

        return ResponseEntity.ok(token);
    }

}
