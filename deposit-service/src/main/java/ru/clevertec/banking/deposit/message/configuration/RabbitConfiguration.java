package ru.clevertec.banking.deposit.message.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties({RabbitProperty.class})
@Configuration
public class RabbitConfiguration {
    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
