package org.sanosysalvos.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.util.Date;

/** Utilidades de test para firmar tokens JWT con el mismo secreto que usa {@link JwtFilter}. */
public final class JwtTestSupport {

    /** Base64 de una clave de 32+ bytes (HS256). Solo para tests. */
    public static final String SECRET = "dGVzdC1zZWNyZXQta2V5LXBhcmEtcHJ1ZWJhcy1qd3QtMjAyNi1zYW5vc3lzYWx2b3M=";

    private JwtTestSupport() {
    }

    public static String validToken() {
        return token(SECRET, 60_000);
    }

    public static String expiredToken() {
        return token(SECRET, -60_000);
    }

    public static String tokenSignedWith(String otherBase64Secret) {
        return token(otherBase64Secret, 60_000);
    }

    private static String token(String base64Secret, long ttlMillis) {
        return Jwts.builder()
                .subject("usuario-test")
                .issuedAt(new Date(System.currentTimeMillis() - 120_000))
                .expiration(new Date(System.currentTimeMillis() + ttlMillis))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret)))
                .compact();
    }
}
