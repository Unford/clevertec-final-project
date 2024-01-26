package ru.clevertec.banking.auth.config;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "token")
public class TokenConfig {
    private String secretKey;
    private long refreshTokenExpirationMs;
    private long tokenExpirationMs;
}