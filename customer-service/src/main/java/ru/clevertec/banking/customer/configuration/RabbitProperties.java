package ru.clevertec.banking.customer.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "clevertec.rabbit.config")
public class RabbitProperties {
    private String queueName;
    private String nameForwardExchange;
}
