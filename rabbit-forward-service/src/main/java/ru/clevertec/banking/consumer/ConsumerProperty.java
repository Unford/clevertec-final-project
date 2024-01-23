package ru.clevertec.banking.consumer;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.rabbitmq.consumer.config")
public class ConsumerProperty {
    private String queueName;
    private String exchangeName;
    private String key;
}
