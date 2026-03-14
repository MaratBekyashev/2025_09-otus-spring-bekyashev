package ru.otus.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.security.AuthTokenProcessResult;
import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtProperties jwtProps;
    private long expiration;

    private Key key;
    @PostConstruct
    private void init () {
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

    public AuthTokenProcessResult valudateToken (String jwtToken) {
        if (jwtToken == null) {
            return AuthTokenProcessResult.EMPTY_TOKEN;
        }

        try {
            Jwts.parser().setSigningKey(key).parse(jwtToken);
            return AuthTokenProcessResult.SUCCESS;
        }
        catch (ExpiredJwtException ex ){
            return AuthTokenProcessResult.EXPIRED;
        }
        catch (MalformedJwtException ex ){
            return AuthTokenProcessResult.MALFORMED;
        }




    }
}