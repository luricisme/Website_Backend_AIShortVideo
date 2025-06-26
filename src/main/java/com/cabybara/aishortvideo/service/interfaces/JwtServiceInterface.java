package com.cabybara.aishortvideo.service.interfaces;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.function.Function;

public interface JwtServiceInterface {
    String generateToken(String email);

    String extractEmail(String token);

    Date extractExpiration(String token);

    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

    Boolean validateToken(String token, UserDetails userDetails);

    void backlistToken(String token, long expirationMs);

    Boolean isTokenBacklisted(String token);
}
