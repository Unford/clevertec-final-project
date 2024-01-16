package ru.clevertec.banking.customer.client.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.clevertec.banking.security.model.AuthTokenProvider;

@Configuration
public class FeignConfiguration {
    @Bean
    public AuthorizationRequestInterceptor authorizationRequestInterceptor(AuthTokenProvider tokenProvider){
        return new AuthorizationRequestInterceptor(tokenProvider);
    }
}
