package ru.clevertec.banking.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {
    public static final List<String> openApiEndpoints = List.of(
            "api/v1/auth/signing",
            "api/v1/auth/token-refreshing",
            "/eureka"
    );

    private final Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));

    public Predicate<ServerHttpRequest> isSecured() {
        return isSecured;
    }
}
