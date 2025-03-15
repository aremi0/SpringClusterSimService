package com.aremi.springclustersimservice.service;

import com.aremi.springclustersimservice.util.ExecutionUtil;
import com.aremi.springclustersimservice.util.TaskResult;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

@Slf4j
@Service
public class ExecutionService extends ExecutionUtil {
    @Value("${app.execution.thread.poolSize}")
    private int THREAD_POOL_SIZE;
    private ExecutorService executor;

    @PostConstruct
    public void init() {
        executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }

    /**
     * Metodo che crea 10 thread ed aspetta la loro conclusione prima di ritornare
     * @return
     */
    public ResponseEntity<String> startExec(String authorization) {
        log.info("startExec:: started");
        try {
            var token = this.extractTokenFromAuthorization(authorization);
            if(!this.checkTokenValidity(token)) {
                log.error("startExec:: [ERROR] Invalid token");
                return ResponseEntity.badRequest().body("Invalid token");
            } else {
                log.error("startExec:: [SUCCESS] request authorized!");
            }
        } catch (BadRequestException e) {
            log.error("startExec:: [ERROR] bad request with authorization: {}", authorization);
            return ResponseEntity.badRequest().body(e.getMessage());
        }


        // Uso i thread della pool per creare n TASK e catchare l'exception in caso di fallimento di ciascuno di essi
        List<CompletableFuture<TaskResult>> futures = IntStream.range(0, this.THREAD_POOL_SIZE)
                .mapToObj(i -> this.task(i, this.executor)
                        .exceptionally(ex -> {
                            log.warn("startExec:: [WARNING] threadNum:{} failed with error: {}", i, ex.getMessage());
                            return new TaskResult(false, 0); // Fallimento -> restituisco 0
                        }))
                .toList();

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allOf.join(); // Ora join() non lancerà eccezioni perché tutti i futures gestiscono gli errori con exceptionally()

        long successCount = futures.stream()
                .map(CompletableFuture::join) // Ora join() è sicuro perché tutti i task hanno catturato gli errori
                .filter(TaskResult::isSuccess)
                .count();

        long totalWaitTime = futures.stream()
                .map(CompletableFuture::join)
                .mapToLong(TaskResult::getExecutionTime)
                .sum();

        log.info("startExec:: all tasks completed. Success count: {}, Total wait time: {} seconds", successCount, totalWaitTime);
        return ResponseEntity.ok("Completed. Success count: " + successCount + ", Total wait time: " + totalWaitTime + " seconds");
    }
}
