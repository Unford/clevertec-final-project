package ru.clevertec.banking.security.model;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;

import java.util.Optional;

@RequiredArgsConstructor
public class AuthTokenProvider {
    private final HttpServletRequest httpServletRequest;

    public Optional<String> getAuthorizationHeader() {
        return Optional.ofNullable(httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION));
    }

    public Optional<String> getType() {
        return getAuthorizationHeader().map(s -> s.substring(0, s.indexOf(' ')));
    }

    public Optional<String> getToken() {
        return getAuthorizationHeader().map(s -> s.substring(s.indexOf(' ') + 1));
    }
}
