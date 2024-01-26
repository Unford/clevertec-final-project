package ru.clevertec.banking.producer;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.rabbitmq.producer.config")
public class ProducerProperty {
    private String forwardExchange;
    private String addresses;
    private String host;
    private int port;
    private String userName;
    private String password;
    private String charsetsName;
}
