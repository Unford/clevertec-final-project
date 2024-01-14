package ru.clevertec.banking.customer.consumer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import ru.clevertec.banking.customer.dto.message.CustomerMessage;
import ru.clevertec.banking.customer.producer.CustomerProducer;
import ru.clevertec.banking.customer.service.CustomerService;

@Service
@AllArgsConstructor
@Slf4j
public class CustomerConsumer {

    private final CustomerService customerService;
    private final CustomerProducer customerProducer;

    @RabbitListener(queues = {"${clevertec.rabbit.config.queue-name}"})
    public void consumeCustomerMessageAndSendForward(CustomerMessage message) {
        log.info("Received message from queue: {}", message);
        customerService.saveOrUpdateFromMessage(message.getPayload());
        customerProducer.produceAuthMessage(message.getPayload());
    }
}
