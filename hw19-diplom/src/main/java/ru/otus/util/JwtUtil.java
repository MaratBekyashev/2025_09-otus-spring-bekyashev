package ru.otus.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final long expiration;

    private final Key key;

    public JwtUtil(JwtProperties jwtProps) {
        String secretKey = jwtProps.getSecretKey();
        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        expiration = 1000 * 60 * 60 * jwtProps.getJwtTokenExpirationHours();
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }

    public String extractUsername(String token) {
        var result = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        return result;
    }

}