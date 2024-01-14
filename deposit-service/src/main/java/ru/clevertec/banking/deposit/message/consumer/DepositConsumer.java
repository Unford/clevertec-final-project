package ru.clevertec.banking.deposit.message.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import ru.clevertec.banking.deposit.model.dto.message.DepositMessage;
import ru.clevertec.banking.deposit.service.DepositService;


@Service
@Slf4j
@RequiredArgsConstructor
public class DepositConsumer {
    private final DepositService depositService;


    @RabbitListener(queues = {"${clevertec.rabbit.queue.deposit}"})
    public void consumeDepositMessage(DepositMessage message) {
        log.info("Received message from queue: {}", message);
        depositService.saveFromMessage(message.getPayload());
    }


}
