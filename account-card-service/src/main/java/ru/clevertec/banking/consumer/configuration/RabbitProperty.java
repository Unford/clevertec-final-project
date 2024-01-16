package ru.clevertec.banking.consumer.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "clevertec.rabbit.consumer.queue")
public class RabbitProperty {
    private String accountQueue;
    private String cardQueue;
}
