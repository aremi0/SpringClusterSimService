package com.aremi.authenticationmicroservice.aspect;

import io.micrometer.core.instrument.MeterRegistry;
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
    private final MeterRegistry meterRegistry;
    private final HttpServletRequest request;

    /**
     * Metodo che centralizza i log/metrics {inizio, completato con successo, completato con Exception, durata} di tutti
     * i metodi di tutti i @Controller (includendo la sessionId della richiesta, ammesso che il FE crei la sessione)
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("execution(* com.aremi.authenticationmicroservice.controller.*.*(..))")
    public Object controllerLogsAndMetrics(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().toShortString();
        String sessionId = Objects.isNull(request.getSession(false)) ? "N/A" : request.getSession(false).getId();

        // Recupera i parametri del metodo
        Object[] methodArgs = joinPoint.getArgs();
        log.info("[{}] {} partito con parametri: {}", sessionId, methodName, methodArgs);
        meterRegistry.counter("controller.calls", "endpoint", methodName).increment();

        try {
            Object result = joinPoint.proceed();
            log.info("[{}] [SUCCESS] {}", sessionId, methodName);
            meterRegistry.counter("controller.success", "endpoint", methodName).increment();
            return result;
        } catch (Throwable ex) {
            log.error("{}] [ERROR] {}  {}: {}", sessionId, methodName, ex.getClass().getSimpleName(), ex.getMessage());
            meterRegistry.counter("controller.errors", "endpoint", methodName).increment();
            throw ex;
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            log.info("[{}] {} terminato in {}ms", sessionId, methodName, duration);
            meterRegistry.timer("controller.execution.time", "endpoint", methodName).record(duration, java.util.concurrent.TimeUnit.MILLISECONDS);
        }
    }

    @Around("execution(* com.aremi.authenticationmicroservice.repository.*.*(..))")
    public Object databaseLogsAndMetrics(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        String sessionId = Objects.isNull(request.getSession(false)) ? "N/A" : request.getSession(false).getId();
        long startTime = System.currentTimeMillis();

        meterRegistry.counter("database.calls", "method", methodName).increment();

        try {
            Object result = joinPoint.proceed();
            meterRegistry.counter("database.success", "method", methodName).increment();
            return result;
        } catch (Throwable ex) {
            meterRegistry.counter("database.errors", "method", methodName).increment();
            log.error("[{}] [ERROR] {} {}: {}", sessionId, methodName, ex.getClass().getSimpleName(), ex.getMessage());
            throw ex;
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            meterRegistry.timer("database.execution.time", "method", methodName).record(duration, java.util.concurrent.TimeUnit.MILLISECONDS);
            log.info("[{}] {} terminato in {}ms", sessionId, methodName, duration);
        }
    }

    @Around("execution(* com.aremi.authenticationmicroservice.service.MessageProducer.send*(..))")
    public Object rabbitLogsAndMetrics(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        String sessionId = Objects.isNull(request.getSession(false)) ? "N/A" : request.getSession(false).getId();
        long startTime = System.currentTimeMillis();

        meterRegistry.counter("rabbitmq.calls", "method", methodName).increment();

        try {
            Object result = joinPoint.proceed();
            meterRegistry.counter("rabbitmq.success", "method", methodName).increment();
            return result;
        } catch (Throwable ex) {
            meterRegistry.counter("rabbitmq.errors", "method", methodName).increment();
            log.error("[{}] [ERROR] {} {}: {}", sessionId, methodName, ex.getClass().getSimpleName(), ex.getMessage());
            throw ex;
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            meterRegistry.timer("rabbitmq.execution.time", "method", methodName).record(duration, java.util.concurrent.TimeUnit.MILLISECONDS);
            log.info("[{}] {} terminato in {}ms", sessionId, methodName, duration);
        }
    }
}
