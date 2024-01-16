package ru.clevertec.banking.customer.client.configuration;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import ru.clevertec.banking.security.model.AuthTokenProvider;

@RequiredArgsConstructor
public class AuthorizationRequestInterceptor implements RequestInterceptor {
    private final AuthTokenProvider tokenProvider;

    @Override
    public void apply(RequestTemplate template) {
        tokenProvider.getAuthorizationHeader()
                .ifPresent(h -> template.header(HttpHeaders.AUTHORIZATION, h));
    }
}
