package com.aremi.springclustersimservice.service;

import com.aremi.springclustersimservice.util.ServiceUtil;
import com.aremi.springclustersimservice.util.TaskResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

@Slf4j
@Service
public class ExecutionService extends ServiceUtil {

    public ResponseEntity<String> exec() {
        log.info("exec:: started");

        // Uso i thread della pool per creare n TASK e catchare l'exception in caso di fallimento di ciascuno di essi
        List<CompletableFuture<TaskResult>> futures = IntStream.range(0, this.THREAD_POOL_SIZE)
                .mapToObj(i -> this.task(i, this.executor)
                        .exceptionally(ex -> {
                            log.warn("exec:: [WARNING] threadNum:{} failed with error: {}", i, ex.getMessage());
                            return new TaskResult(false, 0); // Fallimento -> restituisco 0
                        }))
                .toList();

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allOf.join(); // Ora join() non lancerà eccezioni perché tutti i futures gestiscono gli errori

        long successCount = futures.stream()
                .map(CompletableFuture::join) // Ora join() è sicuro perché tutti i task hanno catturato gli errori
                .filter(TaskResult::isSuccess)
                .count();

        long totalWaitTime = futures.stream()
                .map(CompletableFuture::join)
                .mapToInt(TaskResult::getExecutionTime)
                .sum();

        log.info("exec:: all tasks completed. Success count: {}, Total wait time: {} seconds", successCount, totalWaitTime);
        return ResponseEntity.ok("Completed. Success count: " + successCount + ", Total wait time: " + totalWaitTime + " seconds");
    }
}
