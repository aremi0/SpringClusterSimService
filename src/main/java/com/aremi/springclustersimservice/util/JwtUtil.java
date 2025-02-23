package com.aremi.springclustersimservice.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Date;

public class JwtUtil {
    private static final String SECRET = "secret";
    private static final long EXPIRATION = 86400000;

    /**
     * Genera un token a partire da un subject (in chiaro)
     * @param subject Username da registrare come claim nel token
     * @return Stringa del token appena generato
     */
    public static String generateToken(String subject) {
        return JWT.create()
                .withSubject(subject)
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION))
                .sign(Algorithm.HMAC256(SECRET));
    }
}
