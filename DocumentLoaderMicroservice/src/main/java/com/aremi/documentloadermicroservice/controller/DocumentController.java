package com.aremi.documentloadermicroservice.controller;

import com.aremi.documentloadermicroservice.exception.ForcedFailureException;
import com.aremi.documentloadermicroservice.service.DocumentCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

/**
 * Controller che gestisce gli Endpoint legati alla consultazione dei documenti
 */

@Slf4j
@RestController
@RequiredArgsConstructor
public class DocumentController {
    private final DocumentCacheService documentCacheService;

    /**
     * Endpoint per ottenere la prima pagina del documento di un utente.
     *
     * @param userId ID dell'utente
     * @return Contenuto della prima pagina
     */
    @GetMapping("/first-pages/{userId}")
    public ResponseEntity<String> getFirstPages(@PathVariable Long userId) {
        log.info("getFirstPages:: Richiesta ricevuta per userId: {}", userId);

        // Logica per decidere se lanciare l'eccezione
        Random random = new Random();
        boolean shouldFail = random.nextDouble() < 0.75; // % di probabilità

        if (shouldFail) {
            log.error("getFirstPages:: [FORCED FAILURE] Simulata eccezione per userId: {}", userId);
            throw new ForcedFailureException("Errore simulato per testing.");
        }

        return ResponseEntity.ok(documentCacheService.loadFirstPages(userId)); // Recupera le prime pagine
    }
}
