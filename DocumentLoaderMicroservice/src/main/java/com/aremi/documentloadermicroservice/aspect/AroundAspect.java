package com.aremi.documentloadermicroservice.aspect;

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
    @Around("execution(* com.aremi.documentloadermicroservice.controller.*.*(..))")
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
}
