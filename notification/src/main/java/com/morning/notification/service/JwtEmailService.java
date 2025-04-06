package com.morning.notification.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Service
public class JwtEmailService {
    @Value("${application.security.jwt.secret-email-verification-key}")
    private String secretEmailVerificationKey;
    @Value("${application.security.jwt.expiration-email-key}")
    private long jwtEmailExpiration;

    public String buildEmailToken(
            Map<String, String> extraClaims,
            String username
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtEmailExpiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretEmailVerificationKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
