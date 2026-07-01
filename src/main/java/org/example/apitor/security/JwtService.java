package org.example.apitor.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.example.apitor.security.config.JwtProperties;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {
    private final JwtProperties jwtProperties;
    public JwtService(JwtProperties jwtProperties){
        this.jwtProperties=jwtProperties;
    }

    private SecretKey getSignKey(){
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }

    public String generateToken(String username){
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+jwtProperties.getExpiration()))
                .signWith(getSignKey())
                .compact();
    }

    public String extractUsername(String token){
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
