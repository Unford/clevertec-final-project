package ru.clevertec.banking.currency.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import ru.clevertec.banking.currency.model.dto.message.CurrencyRateMessage;
import ru.clevertec.banking.currency.service.ExchangeRateService;

@Service
@Slf4j
@RequiredArgsConstructor
public class CurrencyRateConsumer {
    private final ExchangeRateService exchangeRateService;


    @RabbitListener(queues = {"${clevertec.rabbit.queue.currency}"})
    public void consumeCurrencyRateMessage(CurrencyRateMessage currencyRateMessage) {
        log.info("Received message from queue: {}", currencyRateMessage);
        exchangeRateService.saveFromMessage(currencyRateMessage.getPayload());
    }


}
