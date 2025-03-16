package com.aremi.documentloadermicroservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * Classe che gestisce la logica di ricezione dei messaggi di autenticazione avvenuta con successo da una coda rabbitMQ.
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class AutenticationMessageConsumer {
    private final DocumentCacheService documentCacheService;

    /**
     * Metodo che intercetta i messaggi dalla coda rabbitMQ e che si limita a precaricare il documento richiesto in una
     * cache distribuita (redis).
     * @param userId
     */
    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void handleSuccessfulAuthentication(Long userId) {
        log.info("Received Successful Authentication with userId:{}", userId);

        documentCacheService.loadFirstPages(userId); // Precarica le prime pagine del documento nella cache, non ritorna nulla
    }
}
