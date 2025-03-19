package com.aremi.requesthandlerproxy.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Classe AOP che centralizza le metriche Around
 */

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AroundAspect {
    private final HttpServletRequest request;


    /**
     * Aspetto che centralizza i log {inizio, completato con successo, completato con Exception, durata} di tutti
     * i metodi di tutti dei @Controller eccetto quelle delle metriche (includendo la sessionId della richiesta,
     * ammesso che il FE crei la sessione)
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("execution(* com.aremi.requesthandlerproxy.controller.*.*(..)) &&" +
            "!execution(* com.aremi.requesthandlerproxy.controller.*.prometheus*(..))")
    public Object controllerLogsAndMetrics(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().toShortString();
        String sessionId = Objects.isNull(request.getSession(false)) ? "N/A" : request.getSession(false).getId();

        // Recupera i parametri del metodo
        Object[] methodArgs = joinPoint.getArgs();
        log.info("[{}] {} partito con parametri: {}", sessionId, methodName, methodArgs);

        try {
            Object result = joinPoint.proceed();
            log.info("[{}] [SUCCESS] {}", sessionId, methodName);
            return result;
        } catch (Throwable ex) {
            log.error("{}] [ERROR] {}  {}: {}", sessionId, methodName, ex.getClass().getSimpleName(), ex.getMessage());
            throw ex;
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            log.info("[{}] {} terminato in {}ms", sessionId, methodName, duration);
        }
    }

    /**
     * Aspetto che centralizza una logica di retry a 3 tentativi, includendo dei log.
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("execution(* com.aremi.requesthandlerproxy.controller.*.get*(..))")
    public Object handleRetryAndLogs(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().toShortString();
        String sessionId = Objects.isNull(request.getSession(false)) ? "N/A" : request.getSession(false).getId();

        int maxAttempts = 3;
        Throwable lastException = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                log.info("[{}] {} tentativo:{}", sessionId, methodName, attempt);
                Object result = joinPoint.proceed(); // Esegui il metodo originale
                log.info("[{}] [SUCCESS] {} tentativo:{}", sessionId, methodName, attempt);
                return result; // Restituisci il risultato se tutto va bene
            } catch (Throwable ex) {
                lastException = ex;
                log.error("[{}] [ERROR] {} tentativo:{}, {}: {}", sessionId, methodName, attempt,
                        ex.getClass().getSimpleName(), ex.getMessage());
                if (attempt == maxAttempts) {
                    log.error("[{}] [ERROR] {} Raggiunto il massimo numero di tentativi", sessionId, methodName);
                    throw lastException; // Propaga l'ultima eccezione
                }
            } finally {
                long duration = System.currentTimeMillis() - startTime;
                log.info("[{}] {} terminato in {}ms", sessionId, methodName, duration);
            }
        }
        return null; // Questo non viene mai raggiunto grazie al throw
    }
}
