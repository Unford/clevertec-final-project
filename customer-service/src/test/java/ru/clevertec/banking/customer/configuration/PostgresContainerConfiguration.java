package ru.clevertec.banking.customer.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;

@TestConfiguration(proxyBeanMethods = false)
public class PostgresContainerConfiguration {
    @Bean
    @ServiceConnection
    public PostgreSQLContainer<?> postgreSQLContainer() {
        PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15.2-alpine")
                .withDatabaseName("clevertec-banking")
                .withUsername("user")
                .withPassword("pass");
        return postgreSQLContainer;
    }
}
