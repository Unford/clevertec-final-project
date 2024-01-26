package ru.clevertec.banking.customer.client.configuration;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@RequiredArgsConstructor
public class AuthorizationRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        Authentication authentication = SecurityContextHolder.getContext()
                                                             .getAuthentication();

        String details =  authentication.getDetails().toString();

        Optional.ofNullable(details)
                        .ifPresent(h -> template.header(HttpHeaders.AUTHORIZATION, h));
    }
}
