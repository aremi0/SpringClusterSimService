package com.aremi.springclustersimservice.util;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public class ServiceUtil {

    protected final int THREAD_POOL_SIZE = 10;
    protected final ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    protected CompletableFuture<TaskResult> task(int threadNum, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("task::thread:{} - started", threadNum);
            int randomWait = ThreadLocalRandom.current().nextInt(16);
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
