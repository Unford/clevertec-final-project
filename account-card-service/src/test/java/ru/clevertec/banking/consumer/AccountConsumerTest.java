package ru.clevertec.banking.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestConstructor;
import ru.clevertec.banking.configuration.PostgreSQLContainerConfig;
import ru.clevertec.banking.configuration.RabbitMQContainerConfig;
import ru.clevertec.banking.repository.AccountRepository;
import ru.clevertec.banking.service.AccountService;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;
import static ru.clevertec.banking.util.FileReaderUtils.*;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@ActiveProfiles("test")
@RequiredArgsConstructor
@ContextConfiguration(classes = {PostgreSQLContainerConfig.class, RabbitMQContainerConfig.class})
@Tag("integration")
public class AccountConsumerTest {
    private final RabbitTemplate rabbitTemplate;
    @SpyBean
    private final AccountService accountService;
    private final AccountRepository repository;
    private final ObjectMapper objectMapper;
    @SpyBean
    private final AccountQueueConsumer accountQueueConsumer;
    @Value("${spring.rabbitmq.template.exchange}")
    private String exchange;

    @BeforeEach
    void init() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("test shows whether the listener received the message and whether it was saved in the database")
    void readMessageFromQueue() throws IOException {
        JsonNode message = objectMapper.readTree(readFile("/account-message.json"));
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
        messageProperties.setHeaders(getHeaders(message));

        Optional.of(message)
                .map(JsonNode::toString)
                .map(String::getBytes)
                .map(bytes -> new Message(bytes, messageProperties))
                .ifPresent(msg ->
                        rabbitTemplate.send(exchange, "", msg));

        waitMessage();

        Mockito.verify(accountQueueConsumer).readMessageFromQueue(Mockito.any());
        Mockito.verify(accountService).saveOrUpdate(Mockito.any());
        assertThat(accountService.findByIban("1234567890123456789012345678")).isNotNull();
    }

    private Map<String, Object> getHeaders(JsonNode node) throws IOException {
        TypeReference<HashMap<String, Object>> typeRef
                = new TypeReference<HashMap<String, Object>>() {
        };
        return objectMapper.readValue(node.get("header").toString(), typeRef);
    }

    private void waitMessage(){
        await()
                .atMost(Duration.ofSeconds(10))
                .with()
                .pollInterval(Duration.ofMillis(100))
                .timeout(Duration.ofSeconds(10))
                .until(() -> !(accountService.getAll(Pageable.ofSize(10)).isEmpty()));
    }
}
