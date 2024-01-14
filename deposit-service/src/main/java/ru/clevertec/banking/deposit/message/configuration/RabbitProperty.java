package ru.clevertec.banking.deposit.message.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "clevertec.rabbit.queue")
public class RabbitProperty {
    private String deposit;
}
