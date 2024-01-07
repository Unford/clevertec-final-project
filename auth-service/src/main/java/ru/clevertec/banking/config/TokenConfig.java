package ru.clevertec.banking.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import ru.clevertec.banking.appYamlReader.YamlPropertySourceFactory;

@Data
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "token") //TODO без кастомной фабрики как он может читать yaml файл отличный от application...
@PropertySource(value = "classpath:defaults/token.yml", factory = YamlPropertySourceFactory.class)
public class TokenConfig {
    private String secretKey;
    private long refreshTokenExpirationMs;
    private long tokenExpirationMs;
}

