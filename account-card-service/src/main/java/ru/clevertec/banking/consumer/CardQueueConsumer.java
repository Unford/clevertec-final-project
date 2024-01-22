package ru.clevertec.banking.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import ru.clevertec.banking.dto.card.CardMessage;
import ru.clevertec.banking.service.CardService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CardQueueConsumer {
    private final CardService service;

    @RabbitListener(queues = "${clevertec.rabbit.consumer.queue.card-queue}")
    public void readMessageFromQueue(CardMessage message) {
        service.saveOrUpdate(message.payload());
    }
}
