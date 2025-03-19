package com.aremi.documentloadermicroservice.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Classe che gestisce la logica di caricamento in una cache distribuita (redis) dei documenti richiesti di recente
 */

@Slf4j
@Service
public class DocumentCacheService {

    @Value("${documents.path}")
    private String documentsPath;
    private final short PARAGRAPH_TO_READ = 50;

    /**
     * Il metodo cerca prima nella cache esistente se c'è già un valore associato alla chiave userId nella cache
     * denominata "documents".
     *
     * Se un valore è presente in cache, viene restituito direttamente, evitando di eseguire la logica contenuta nel
     * metodo (in questo caso la chiamata a extractFirstPages).
     *
     * Precarica i dati nella cache, anche se è tipo void l'operazione di caching avverrà comunque automaticamente
     * con l'annotazione @Cacheable
     *
     * @param userId
     * @return
     */
    @Cacheable(value = "documents", key = "#userId")
    public String loadFirstPages(Long userId) {
        try {
            return extractFirstPages(userId); // Usa il metodo per estrarre il testo dalla prima pagina
        } catch (IOException e) {
            throw new RuntimeException("Errore durante l'estrazione delle prime pagine per userId: " + userId, e);
        }
    }

    /**
     * Metodo che estrae le prime pagine dal documento, se lo trova
     *
     * @param userId
     * @return
     */
    private String extractFirstPages(Long userId) throws IOException {
        Optional<Path> documentPath = findDocumentByUserId(userId); // Trova il file corrispondente nella directory

        if (documentPath.isEmpty()) {
            log.error("extractFirstPages:: [ERROR] Nessun documento trovato per l'utente:{} nella directory:{}", userId, documentsPath);
            throw new FileNotFoundException("Documento non trovato per l'utente " + userId); // Lancia un'eccezione custom
        }

        log.info("extractFirstPages:: documento trovato '{}', inizio lettura...", documentPath);

        try (FileInputStream fis = new FileInputStream(documentPath.get().toFile());
             XWPFDocument document = new XWPFDocument(fis)) {

            StringBuilder firstPageText = new StringBuilder();
            List<XWPFParagraph> paragraphs = document.getParagraphs(); // Carica solamente i primi PARAGRAPH_TO_READ paragrafi
            for (int i = 0; i < Math.min(PARAGRAPH_TO_READ, paragraphs.size()); i++) {
                firstPageText.append(paragraphs.get(i).getText()).append("\n");
            }

            log.info("extractFirstPages:: [SUCCESS] Prime pagine estratte con successo!");
            return firstPageText.toString();
        }
    }


    /**
     * Metodo che trova i documenti associati agli {userId}:
     * /app/documents/SistemiOperativi_1.docx
     * /app/documents/Informatica Avanzata_2.docx
     *
     * @param userId
     * @return
     */
    private Optional<Path> findDocumentByUserId(Long userId) {
        try (Stream<Path> paths = Files.list(Paths.get(documentsPath))) {
            return paths.filter(path -> path.toString().endsWith("_" + userId + ".docx"))
                    .findFirst();
        } catch (IOException e) {
            log.error("findDocumentByUserId:: [ERROR] Errore durante la ricerca del documento per l'utente:{} | {}", userId, e.getMessage());
            return Optional.empty();
        }
    }

}
