package com.aremi.documentloadermicroservice.controller;

import com.aremi.documentloadermicroservice.service.DocumentCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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
    @GetMapping("/first-page/{userId}")
    public ResponseEntity<String> getFirstPage(@PathVariable Long userId) {
        log.info("getFirstPage:: Richiesta ricevuta per userId: {}", userId);
        try {
            String firstPageContent = documentCacheService.loadFirstPages(userId); // Recupera la prima pagina dalla cache o dal file
            log.info("getFirstPage:: [SUCCESS] Prima pagina restituita per userId: {}", userId);
            return ResponseEntity.ok(firstPageContent);
        } catch (Exception e) {
            log.error("getFirstPage:: [ERROR] Errore durante il recupero della prima pagina per userId: {} | {}", userId, e.getMessage());
            return ResponseEntity.status(500).body("Errore durante il recupero della prima pagina.");
        }
    }
}
