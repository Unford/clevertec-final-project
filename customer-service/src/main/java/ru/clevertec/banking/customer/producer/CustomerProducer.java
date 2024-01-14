package ru.clevertec.banking.customer.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.clevertec.banking.customer.dto.CustomerMapper;
import ru.clevertec.banking.customer.dto.message.AuthMessage;
import ru.clevertec.banking.customer.dto.message.AuthMessagePayload;
import ru.clevertec.banking.customer.dto.message.CustomerMessagePayload;
import ru.clevertec.banking.customer.dto.response.CustomerResponse;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class CustomerProducer {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final CustomerMapper customerMapper;

    @Value("${clevertec.rabbit.config.name-forward-exchange}")
    private String forwardExchange;

    public void prepareAndProduceForward(CustomerResponse customer) {
        Optional.of(customer)
                .map(customerMapper::toCustomerPayloadFromResponse)
                .ifPresent(this::produceAuthMessage);
    }

    @Async
    @SneakyThrows
    public void produceAuthMessage(CustomerMessagePayload payload) {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);

        Optional.of(payload)
                .map(this::getConvertedAuthMessPayload)
                .map(this::toJsonMessage)
                .map(String::getBytes)
                .map(bytes -> new Message(bytes, messageProperties))
                .ifPresent(msg -> rabbitTemplate.send(forwardExchange, "auth", msg));
    }

    private AuthMessage getConvertedAuthMessPayload(CustomerMessagePayload customerPayload) {
        AuthMessagePayload authPayload = new AuthMessagePayload()
                .setEmail(customerPayload.getEmail())
                .setId(customerPayload.getId());
        AuthMessage.MessageHeader header = new AuthMessage.MessageHeader().setMessageType("auth");
        return new AuthMessage(header, authPayload);
    }

    @SneakyThrows
    private <T> String toJsonMessage(T t) {
        return objectMapper.writeValueAsString(t);
    }
}