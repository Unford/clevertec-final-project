package ru.clevertec.banking.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import ru.clevertec.banking.producer.RabbitForwardProducer;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitConsumer {
    private final RabbitForwardProducer producer;
    private final ApplicationContext context;

    @RabbitListener(containerFactory = "consumerFactory", bindings = @QueueBinding(
            value = @Queue("${spring.rabbitmq.consumer.config.queue-name}"),
            exchange = @Exchange("${spring.rabbitmq.consumer.config.exchange-name}"),
            key = "${spring.rabbitmq.consumer.config.key}"))
    public void consumeDirectOneQueueMessage(String message) {
        context.getId();
        producer.produceMessageWithHeaders(message);
    }
}
