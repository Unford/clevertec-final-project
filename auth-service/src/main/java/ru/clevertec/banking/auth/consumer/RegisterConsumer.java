package ru.clevertec.banking.auth.consumer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import ru.clevertec.banking.auth.dto.message.RegisterMessage;
import ru.clevertec.banking.auth.service.AuthenticationService;

@Service
@AllArgsConstructor
@Slf4j
public class RegisterConsumer {

    private final AuthenticationService authenticationService;

    @RabbitListener(queues = {"${clevertec.rabbit.config.queue-name}"})
    public void consumeCustomerMessageAndSendForward(RegisterMessage message) {
        log.info("Received message from queue: {}", message);
        authenticationService.registerAsync(message.getPayload());
    }

}