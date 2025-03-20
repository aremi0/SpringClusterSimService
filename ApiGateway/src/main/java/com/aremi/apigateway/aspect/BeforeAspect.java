package com.aremi.apigateway.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * Classe che implementa gli aspetti Before (AOP)
 */

@Slf4j
@Aspect
@Component
public class BeforeAspect {

    /**
     * Aspetto per il logging dell'inizio dei metodi specificati, compresi parametri input
     *
     * @param joinPoint
     */
    @Before("execution(* com.aremi.requesthandlerproxy.controller.*.*(..)) &&" +
            "!execution(* com.aremi.requesthandlerproxy.controller.*.prometheus*(..))")
    public void logMethodStart(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();

        // Logga il nome del metodo e i parametri
        log.info("{} iniziato con i parametri: {}", methodName, args);
    }
}
