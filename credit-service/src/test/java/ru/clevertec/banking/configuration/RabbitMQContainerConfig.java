package ru.clevertec.banking.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.utility.MountableFile;

@TestConfiguration(proxyBeanMethods = false)
public class RabbitMQContainerConfig {

    @Bean
    @ServiceConnection
    public RabbitMQContainer rabbitMQContainer() {
        RabbitMQContainer rabbitMQContainer = new RabbitMQContainer("rabbitmq:3.10.7-management-alpine")
                .withRabbitMQConfig(MountableFile.forClasspathResource("rabbit/rabbitmq.conf"))
                .withCopyFileToContainer(MountableFile.forClasspathResource("rabbit/definitions.json"),
                        "/etc/rabbitmq/definitions.json");
        return rabbitMQContainer;
    }
}
