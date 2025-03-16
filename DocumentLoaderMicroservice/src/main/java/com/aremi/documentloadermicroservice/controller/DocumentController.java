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
    @GetMapping("/first-pages/{userId}")
    public ResponseEntity<String> getFirstPages(@PathVariable Long userId) {
        log.info("getFirstPages:: Richiesta ricevuta per userId: {}", userId);
        try {
            String firstPageContent = documentCacheService.loadFirstPages(userId); // Recupera le prime pagine dalla cache o dal file
            log.info("getFirstPages:: [SUCCESS] Prime pagine restituite per userId: {}", userId);
            return ResponseEntity.ok(firstPageContent);
        } catch (Exception e) {
            log.error("getFirstPages:: [ERROR] Errore durante il recupero delle prime pagine per userId: {} | {}", userId, e.getMessage());
            return ResponseEntity.status(500).body("Errore durante il recupero delle prime pagine del documento.");
        }
    }
}
