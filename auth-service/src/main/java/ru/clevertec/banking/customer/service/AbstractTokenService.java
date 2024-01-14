package ru.clevertec.banking.customer.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import ru.clevertec.banking.config.TokenConfig;
import ru.clevertec.banking.customer.entity.Role;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@RequiredArgsConstructor
public abstract class AbstractTokenService {

    protected final TokenConfig tokenConfig;

    protected String generateToken(Map<String, Object> extraClaims, Long userId, long expirationMs) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    protected Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(tokenConfig.getSecretKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    protected Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    protected List<Role> extractAuthorities(String token) {
        @SuppressWarnings("unchecked")
        List<String> roles = extractClaim(token, claims -> claims.get("authorities", List.class));
        return roles.stream()
                    .map(Role::valueOf)
                    .toList();
    }

    public Long extractId(String token) {
        return Long.parseLong(extractClaim(token, Claims::getSubject));
    }
    protected  <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    protected Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                   .setSigningKey(getSignInKey())
                   .build()
                   .parseClaimsJws(token)
                   .getBody();
    }
}
