package com.aremi.apigateway.aspect;

import com.aremi.apigateway.annotation.Retryable;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Classe che implementa gli aspetti Around (AOP)
 */

@Slf4j
@Aspect
@Component
public class AroundAspect {

    /**
     * Aspetto per il logging della durata dei metodi specificati, comprese exception sollevate
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("execution(* com.aremi.apigateway.controller.*.*(..)) &&" +
            "!execution(* com.aremi.apigateway.controller.*.prometheus*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().toShortString();

        try {
            Object result = joinPoint.proceed();

            long executionTime = System.currentTimeMillis() - startTime;
            log.info("{} eseguito in {}ms.", methodName, executionTime);

            return result;
        } catch (Throwable throwable) {
            // Log in caso di errore
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("[ERROR] {} errore durante l'esecuzione, tempo di esecuzione: {}ms.", methodName, executionTime);
            throw throwable; // Rilancia l'eccezione
        }
    }

    /**
     * Aspetto per applicazione della logica dei retry per l'annotazione @Retryable
     *
     * @param joinPoint
     * @param retryable
     * @return
     * @throws Throwable
     */
    @Around("@annotation(retryable)")
    public Object handleRetry(ProceedingJoinPoint joinPoint, Retryable retryable) throws Throwable {
        int maxAttempts = retryable.maxAttempts();
        Class<? extends Throwable>[] retryOnExceptions = retryable.retryOnExceptions();

        int attempt = 1;

        while (true) {
            try {
                return joinPoint.proceed();
            } catch (Throwable e) {
                if (attempt >= maxAttempts || !shouldRetryOnException(e, retryOnExceptions)) {
                    log.error("[ERROR] Superato il numero massimo di tentativi ({}). Eccezione: {}, Messaggio: {}",
                            maxAttempts, e.getClass().getSimpleName(), e.getMessage());
                    throw e;
                }

                log.warn("[WARN] Tentativo {} fallito. Classe errore: {}, Messaggio: {}. Riprovo...",
                        attempt, e.getClass().getSimpleName(), e.getMessage());
                attempt++;
            }
        }
    }

    /**
     * Metodo di utilità alla "handleRetry" che cerca l'Exception generata a runtime con la lista di exception per
     * cui si deve fare il retry
     *
     * @param e Eccezione generata a runtime
     * @param retryOnExceptions Lista di eccezioni per cui si effettua logica 'retry'
     * @return Ritorna true se trova l'Exception nella lista
     */
    private boolean shouldRetryOnException(Throwable e, Class<? extends Throwable>[] retryOnExceptions) {
        for (var exceptionClass : retryOnExceptions) {
            if (exceptionClass.isAssignableFrom(e.getClass())) {
                return true;
            }
        }
        return false;
    }
}