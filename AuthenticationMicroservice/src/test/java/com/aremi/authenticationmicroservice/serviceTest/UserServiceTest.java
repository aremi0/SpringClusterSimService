//package com.aremi.springclustersimservice.serviceTest;
//
//import com.aremi.authentication.LoginRequestDTO;
//import com.aremi.springclustersimservice.model.UserEntity;
//import com.aremi.springclustersimservice.repository.UserRepository;
//import com.aremi.springclustersimservice.service.MessageProducer;
//import com.aremi.springclustersimservice.service.UserService;
//import com.aremi.springclustersimservice.util.DecryptUtil;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class UserServiceTest {
//    @Mock private UserRepository userRepository;
//    @Mock private MessageProducer messageProducer;
//    @InjectMocks private UserService userService;
//
//    @Test
//    public void testRegisterNewUser_Success() {
//        // Simula il DTO in input
//        LoginRequestDTO request = new LoginRequestDTO("encryptedUsername", "encryptedPassword");
//
//        // Simula il comportamento di DecryptUtil
//        mockStatic(DecryptUtil.class);
//        when(DecryptUtil.decrypt("encryptedUsername")).thenReturn("testUser");
//        when(DecryptUtil.decrypt("encryptedPassword")).thenReturn("testPass");
//        when(DecryptUtil.hashPassword("testPass")).thenReturn("hashedTestPass");
//
//        // Simula il comportamento del repository
//        when(userRepository.existsByUsername("testUser")).thenReturn(false);
//
//        // Esegui il metodo
//        ResponseEntity<String> response = userService.registerNewUser(request);
//
//        // Verifica il risultato
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("User registered successfully", response.getBody());
//
//        // Verifica che i metodi siano stati chiamati
//        verify(userRepository).save(any(UserEntity.class));
//    }
//
//    @Test
//    public void testRegisterNewUser_UsernameAlreadyExists() {
//        // Simula il DTO in input
//        LoginRequestDTO request = new LoginRequestDTO("encryptedUsername", "encryptedPassword");
//
//        // Simula il comportamento di DecryptUtil
//        mockStatic(DecryptUtil.class);
//        when(DecryptUtil.decrypt("encryptedUsername")).thenReturn("testUser");
//
//        // Simula il comportamento del repository
//        when(userRepository.existsByUsername("testUser")).thenReturn(true);
//
//        // Esegui il metodo
//        ResponseEntity<String> response = userService.registerNewUser(request);
//
//        // Verifica il risultato
//        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
//        assertEquals("Username already exists", response.getBody());
//
//        // Verifica che il metodo `save` non venga chiamato
//        verify(userRepository, never()).save(any(UserEntity.class));
//    }
//}
