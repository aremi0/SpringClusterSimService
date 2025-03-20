package com.aremi.documentloadermicroservice.controller;

import com.aremi.documentloadermicroservice.exception.ForcedFailureException;
import com.aremi.documentloadermicroservice.service.DocumentCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@ExtendWith(MockitoExtension.class)
public class DocumentControllerTest {
    @Mock
    private DocumentCacheService documentCacheService;
    @InjectMocks
    private DocumentController documentController;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(documentController).build();
    }

    @Test
    void testGetFirstPagesSuccess() throws Exception {
        // Mock del comportamento del service
        Long userId = 1L;
        String mockDocument = "Document content";
        when(documentCacheService.loadFirstPages(userId)).thenReturn(mockDocument);

        // Simula una richiesta GET e verifica il risultato
        mockMvc.perform(get("/first-pages/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().string(mockDocument));
    }

    @Test
    void testGetFirstPagesFailure() throws Exception {
        // Simula il lancio dell'eccezione
        Long userId = 2L;
        doThrow(new ForcedFailureException("Errore simulato per userId: " + userId))
                .when(documentCacheService).loadFirstPages(userId);

        // Simula una richiesta GET e verifica il risultato
        mockMvc.perform(get("/first-pages/{userId}", userId))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Errore simulato per userId: " + userId));
    }
}

