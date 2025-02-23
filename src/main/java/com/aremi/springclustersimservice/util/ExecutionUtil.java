package com.aremi.springclustersimservice.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.concurrent.*;

@Slf4j
public class ExecutionUtil {
    @Value("${app.execution.thread.maxWaitTimeSec}")
    private long UPPER_BOUND_THREAD_WAITING_TIME;

    /**
     * Metodo generico che crea un task per ogni executor, ciscuno dei quali attende un tempo arbitrario prima di terminare.
     * Se i secondi di wait sono dispari il thread fallisce.
     * @param threadNum Id arbitrario del thread della pool.
     * @param executor Thread della pool.
     * @return Ritorna un task-result sotto forma di CompletableFuture.
     */
    protected CompletableFuture<TaskResult> task(int threadNum, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("task::thread:{} - started", threadNum);
            long randomWait = ThreadLocalRandom.current().nextLong(UPPER_BOUND_THREAD_WAITING_TIME);
            log.info("task::thread:{} - randomWait:{} going to sleep...", threadNum, randomWait);
            try {
                if (randomWait % 2 == 0) {
                    Thread.sleep(randomWait * 1000);
                    log.info("task::thread:{} - task completed! Returning to main", threadNum);
                    return new TaskResult(true, randomWait);
                } else {
                    throw new RuntimeException("Random wait is odd");
                }
            } catch (InterruptedException e) {
                log.warn("task::thread:{} [ERROR] - InterruptedException", threadNum, e);
                throw new RuntimeException(e);
            }
        }, executor);
    }
}
