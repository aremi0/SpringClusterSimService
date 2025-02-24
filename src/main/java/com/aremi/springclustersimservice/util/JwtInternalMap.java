package com.aremi.springclustersimservice.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Singleton statico che mantiene in RAM una mappa interna con le associazioni username-token
 */

@Slf4j
@Component
public class JwtInternalMap {
    private static final Map<String, String> internalJwtMap = new HashMap<>();

    public static void addOrUpdateValue(String username, String token) {
        var previousValue = internalJwtMap.put(username, token);
        if(Objects.isNull(previousValue)) {
            log.info("addOrUpdateValue:: new pair added. Username:{}, token:{}", username, token);
        } else {
            log.info("addOrUpdateValue:: pair updated. Username:{}, oldToken:{}, newToken{}", username, previousValue, token);
        }
    }

    public static void removeValue(String username) {
        var value = internalJwtMap.remove(username);
        if(Objects.isNull(value)) {
            log.warn("removeValue:: [WARNING] username not found! username:{}", username);
        } else {
            log.info("removeValue:: pair removed. username:{}", username);
        }
    }

    public static String getTokenByUsername(String username) {
        return Optional.ofNullable(internalJwtMap.get(username))
                .orElseThrow(() -> {
                    log.warn("getTokenByUsername:: username not found! username:{}", username);
                    return new IllegalArgumentException("Username not found in internal map");
                });
    }
}
