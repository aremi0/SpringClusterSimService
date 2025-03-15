package com.aremi.springclustersimservice.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;

import java.util.Objects;

@Slf4j
public class ServicesUtil {

    /**
     * Controlla che l'header Authorization sia legale
     * @param authorization
     * @return Ritorna il token all'interno dell'header Authorization
     * @throws BadRequestException
     */
    protected String extractTokenFromAuthorization(String authorization) throws BadRequestException {
        if(authorization.startsWith("Bearer ")) {
            var token = authorization.split(" ")[1];

            if(Objects.isNull(token) || token.trim().isEmpty()) {
                log.warn("extractTokenFromAuthorization:: [WARNING] Token is malformed or blank");
                throw new BadRequestException("Token is malformed or blank");
            }
            return token;
        } else {
            log.warn("extractTokenFromAuthorization:: [WARNING] Authorization header does not start with 'Bearer '");
            throw new BadRequestException("Authorization header does not start with 'Bearer '");
        }
    }

    /**
     * Controlla che il token della richiesta contenga i claim necessari per autorizzare la richiesta.
     * Controlla inoltre che il token della richiesta sia uguale a quello salvato nella mappa interna per lo stesso username.
     * @param tokenToCheck
     * @return
     */
    protected boolean checkTokenValidity(String tokenToCheck) {
        boolean result = false;
        try {
            var usernameInsideTokenToCheck = JwtUtil.getSubject(tokenToCheck);
            var tokenInMap = JwtInternalMap.getTokenByUsername(usernameInsideTokenToCheck);
            result = Objects.equals(tokenToCheck, tokenInMap);
            if(!result) {
                log.warn("checkTokenValidity:: [WARNING] Provided token is different from the one saved internally");
            }
        } catch (Throwable e) {
            log.error("checkTokenValidity:: [ERROR] {}::", e.getClass(), e);
        }
        return result;
    }
}
