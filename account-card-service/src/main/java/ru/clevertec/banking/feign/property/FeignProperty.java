package ru.clevertec.banking.feign.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "clevertec.feign.service")
public class FeignProperty {
    private String currencyClient;
    private String currencyGetPath;
}
