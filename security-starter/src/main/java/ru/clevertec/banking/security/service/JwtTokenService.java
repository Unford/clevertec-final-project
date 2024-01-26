package ru.clevertec.banking.security.service;

import com.auth0.jwt.JWT;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class JwtTokenService {
    private static final String SUB = "sub";
    private static final String AUTHORITIES = "authorities";


    public Optional<String> extractSub(String token) {
        return Optional.ofNullable(JWT.decode(token).getClaim(SUB).asString());
    }

    public List<String> extractAuthorities(String token) {
        List<String> list = JWT.decode(token).getClaim(AUTHORITIES).asList(String.class);
        return Objects.requireNonNullElse(list, List.of());
    }
}
