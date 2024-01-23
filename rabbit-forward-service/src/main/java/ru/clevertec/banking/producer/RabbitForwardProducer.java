package ru.clevertec.banking.producer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class RabbitForwardProducer {
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final ApplicationContext context;
    @Value("${spring.rabbitmq.producer.config.forward-exchange}")
    private String forwardExchange;


    public RabbitForwardProducer(@Qualifier("forwardTemplate") RabbitTemplate rabbitTemplate, ObjectMapper objectMapper,ApplicationContext context){
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
        this.context = context;
    }


    /**
     * Продюсер копирует из сообщения header, добавляет его к MessageProperties и отправляет в другой instance кролика
     * @param message Сообщение пришедшее из кроля
     */
    public void produceMessageWithHeaders(String message) {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
        messageProperties.setHeaders(getHeaders(message));

        Optional.of(message)
                .map(String::getBytes)
                .map(bytes -> new Message(bytes, messageProperties))
                .ifPresent(msg ->
                    rabbitTemplate.send(forwardExchange, "routingKey", msg)
                );
    }

    /**
     *
     * @param mess - сообщение, из которого достаются хидеры
     * @return мапа с хидерами, которые будут добавлены в MessageProperties
     */
    @SneakyThrows
    private Map<String, Object> getHeaders(String mess) {
        JsonNode node = objectMapper.readTree(mess);
        TypeReference<HashMap<String, Object>> typeRef
                = new TypeReference<HashMap<String, Object>>() {
        };
        return objectMapper.readValue(node.get("header").toString(), typeRef);
    }
}
