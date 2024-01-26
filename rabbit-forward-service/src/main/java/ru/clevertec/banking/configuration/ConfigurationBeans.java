package ru.clevertec.banking.configuration;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import ru.clevertec.banking.producer.ProducerProperty;

import java.net.URI;

@Configuration
public class ConfigurationBeans {

    @Bean("consumerConnectionFactory")
    public ConnectionFactory connectionFactoryConsumer(RabbitProperties property) {
        CachingConnectionFactory connectionFactory;
        if (!property.getAddresses().isEmpty()) {
            connectionFactory = new CachingConnectionFactory(URI.create(property.getAddresses()));
        } else {
            connectionFactory = new CachingConnectionFactory(property.getHost(), property.getPort());
            connectionFactory.setUsername(property.getUsername());
            connectionFactory.setPassword(property.getPassword());
        }
        return connectionFactory;
    }

    @Bean("consumerFactory")
    public RabbitListenerContainerFactory containerFactory(@Qualifier("consumerConnectionFactory")ConnectionFactory connectionFactory){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        return factory;
    }

    @Primary
    @Bean("forwardConnectionFactory")
    public ConnectionFactory connectionFactoryProducer(ProducerProperty property) {
        CachingConnectionFactory connectionFactory;
        if (!property.getAddresses().isEmpty()) {
            connectionFactory = new CachingConnectionFactory(URI.create(property.getAddresses()));
        } else {
            connectionFactory = new CachingConnectionFactory(property.getHost(), property.getPort());
            connectionFactory.setUsername(property.getUserName());
            connectionFactory.setPassword(property.getPassword());
        }
        return connectionFactory;
    }

    @Bean("forwardTemplate")
    public RabbitTemplate rabbitTemplate(@Qualifier("forwardConnectionFactory") ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }
}
