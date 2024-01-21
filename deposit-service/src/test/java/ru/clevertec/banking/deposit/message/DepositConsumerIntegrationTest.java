package ru.clevertec.banking.deposit.message;


import lombok.AllArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.context.ContextConfiguration;
import ru.clevertec.banking.deposit.configuration.DataFakerConfiguration;
import ru.clevertec.banking.deposit.configuration.PostgresContainerConfiguration;
import ru.clevertec.banking.deposit.configuration.RabbitMQContainerConfiguration;
import ru.clevertec.banking.deposit.message.consumer.DepositConsumer;
import ru.clevertec.banking.deposit.model.domain.Deposit;
import ru.clevertec.banking.deposit.model.dto.message.DepositMessage;
import ru.clevertec.banking.deposit.repository.DepositRepository;
import ru.clevertec.banking.deposit.service.DepositService;
import ru.clevertec.banking.deposit.util.RandomDepositFactory;
import ru.clevertec.banking.deposit.util.SpringBootCompositeTest;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@SpringBootCompositeTest
@ContextConfiguration(classes = {RabbitMQContainerConfiguration.class, PostgresContainerConfiguration.class,
        DataFakerConfiguration.class})
@ExtendWith(OutputCaptureExtension.class)
@AllArgsConstructor
@Tag("integration")
class DepositConsumerIntegrationTest {
    RandomDepositFactory randomDepositFactory;
    RabbitTemplate rabbitTemplate;
    DepositRepository depositRepository;

    @SpyBean
    DepositService service;

    @SpyBean
    DepositConsumer depositConsumer;


    @BeforeEach
    void setUp() {
        depositRepository.deleteAll();
    }

    @Test
    void shouldConsumeMessageFromQueueAndSaveToDatabase(CapturedOutput output) {
        DepositMessage depositMessage = randomDepositFactory.createDepositMessage();

        rabbitTemplate.convertAndSend("deposit", depositMessage);
        awaitForQueueUntil(() -> !depositRepository.findAll().isEmpty());

        Mockito.verify(depositConsumer).consumeDepositMessage(Mockito.any());
        Mockito.verify(service).saveFromMessage(Mockito.any());

        Assertions.assertThat(output.getErr()).isEmpty();
        Assertions.assertThat(output.getOut()).contains("Received message from queue", "insert");
        Assertions.assertThat(depositRepository.findByAccInfoAccIban(depositMessage.getPayload()
                        .getAccInfo()
                        .getAccIban()))
                .isPresent()
                .get()
                .extracting(Deposit::getId)
                .isNotNull();
    }

    @Test
    void shouldConsumeMessageFromQueueAndUpdateExistedDeposit(CapturedOutput output) {
        DepositMessage depositMessage = randomDepositFactory.createDepositMessage();
        String iban = depositMessage.getPayload().getAccInfo().getAccIban();
        service.saveFromMessage(depositMessage.getPayload());

        DepositMessage message = randomDepositFactory.createDepositMessage();
        message.getPayload().getAccInfo().setAccIban(iban);

        rabbitTemplate.convertAndSend("deposit", message);

        awaitForQueueUntil(() -> depositRepository.findByAccInfoAccIban(iban).get().getCustomerId()
                .equals(message.getPayload().getCustomerId()));

        Mockito.verify(depositConsumer).consumeDepositMessage(Mockito.any());
        Mockito.verify(service, Mockito.times(2)).saveFromMessage(Mockito.any());

        Assertions.assertThat(output.getErr()).isEmpty();
        Assertions.assertThat(output.getOut()).contains("Received message from queue", "update");

        Assertions.assertThat(depositRepository.findAll()).hasSize(1);

    }

    void awaitForQueueUntil(Callable<Boolean> until) {
        await().atMost(20, TimeUnit.SECONDS).until(until);
    }

}
