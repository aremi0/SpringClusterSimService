package com.aremi.documentloadermicroservice.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Classe di configurazione di un CacheableInterceptor implementato tramite AOP
 */

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class CacheLoggerAspect {
    private final CacheManager cacheManager;

    /**
     * Intercetta tutti i metodi annotati con la notazione specificata e che accettano l'argomento "userId", dopodichè
     * ottiene la cache "documents" usata dal servizio di cui si viene verificare il cache-hit o cache-miss
     *
     * @param userId
     * @param result
     */
    @AfterReturning(value = "@annotation(org.springframework.cache.annotation.Cacheable) && args(userId)", returning = "result", argNames = "userId,result")
    public void firstPagesCacheState(Long userId, Object result) {
        Cache cache = cacheManager.getCache("documents");

        if (!Objects.isNull(cache) && !Objects.isNull(cache.get(userId))) {
            log.info("firstPagesCacheState:: [Cache HIT]");
        } else {
            log.info("firstPagesCacheState:: [Cache MISS]");
        }
    }
}
