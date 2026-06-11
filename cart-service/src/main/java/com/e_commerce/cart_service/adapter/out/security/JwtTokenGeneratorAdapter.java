package com.e_commerce.cart_service.adapter.out.security;

import com.e_commerce.cart_service.domain.port.out.TokenGeneratorPort;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;


import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtTokenGeneratorAdapter implements TokenGeneratorPort {

    private static final String SECRET = "super-secret-key-super-secret-key";
    private static final long EXPIRATION_MS = 86400000;


    @Override
    public String generateToken(String userId, String email) {

        return Jwts.builder()
                .setSubject(userId).claim("email", email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }
}
