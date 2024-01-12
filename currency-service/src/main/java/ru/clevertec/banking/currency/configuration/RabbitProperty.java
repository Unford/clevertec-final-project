package ru.clevertec.banking.currency.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "clevertec.rabbit.queue")
public class RabbitProperty {
    private String currency;
}
