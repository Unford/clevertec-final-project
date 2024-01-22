package ru.clevertec.banking.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import ru.clevertec.banking.dto.CreditMessage;
import ru.clevertec.banking.service.CreditService;

@RequiredArgsConstructor
@Service
public class CreditConsumer {
    private final CreditService service;

    @RabbitListener(queues = "${clevertec.rabbit.consumer.queue.credit-queue}")
    public void readMessageFromQueue(CreditMessage message) {
        service.saveOrUpdate(message.payload());
    }
}
