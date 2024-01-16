package ru.clevertec.banking.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import ru.clevertec.banking.dto.account.AccountMessage;
import ru.clevertec.banking.service.AccountService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountQueueConsumer {
    private final AccountService service;

    @RabbitListener(queues = "${clevertec.rabbit.consumer.queue.account-queue}")
    public void readMessageFromQueue(AccountMessage message) {
        Optional.of(message.payload())
                .map(service::save);
    }
}
