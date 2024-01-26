package ru.clevertec.banking.currency.consumer;

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
import ru.clevertec.banking.currency.configuration.DataFakerConfiguration;
import ru.clevertec.banking.currency.configuration.PostgresContainerConfiguration;
import ru.clevertec.banking.currency.configuration.RabbitMQContainerConfiguration;
import ru.clevertec.banking.currency.model.domain.ExchangeData;
import ru.clevertec.banking.currency.model.dto.message.CurrencyRateMessage;
import ru.clevertec.banking.currency.repository.ExchangeDataRepository;
import ru.clevertec.banking.currency.service.ExchangeRateService;
import ru.clevertec.banking.currency.util.CurrencyDataFactory;
import ru.clevertec.banking.currency.util.SpringBootCompositeTest;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@SpringBootCompositeTest
@ContextConfiguration(classes = {RabbitMQContainerConfiguration.class, PostgresContainerConfiguration.class,
        DataFakerConfiguration.class})
@ExtendWith(OutputCaptureExtension.class)
@AllArgsConstructor
@Tag("integration")
class CurrencyConsumerIntegrationTest {
    CurrencyDataFactory currencyDataFactory;
    RabbitTemplate rabbitTemplate;
    ExchangeDataRepository repository;

    @SpyBean
    ExchangeRateService service;

    @SpyBean
    CurrencyRateConsumer consumer;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void shouldConsumeMessageFromQueueAndSaveToDatabase(CapturedOutput output) {
        CurrencyRateMessage message = currencyDataFactory.createCurrencyMessage();

        rabbitTemplate.convertAndSend("currency", message);

        awaitForQueueUntil(() -> !repository.findAll().isEmpty());

        Mockito.verify(consumer).consumeCurrencyRateMessage(Mockito.any());
        Mockito.verify(service).saveFromMessage(Mockito.any());

        Assertions.assertThat(output.getErr()).isEmpty();
        Assertions.assertThat(output.getOut()).contains("Received message from queue");
        Optional<ExchangeData> actual = repository.findByStartDt(message.getPayload().getStartDt());

        Assertions.assertThat(actual)
                .isPresent()
                .get()
                .extracting(ExchangeData::getId)
                .isNotNull();
    }

    @Test
    void shouldConsumeMessageFromQueueAndUpdateExistedCurrency(CapturedOutput output) {
        CurrencyRateMessage init = currencyDataFactory.createCurrencyMessage();

        OffsetDateTime startDt = init.getPayload().getStartDt();

        service.saveFromMessage(init.getPayload());

        CurrencyRateMessage message = currencyDataFactory
                .createCurrencyMessage(init.getPayload().getExchangeRates().size() + 1);
        message.getPayload().setStartDt(startDt);


        rabbitTemplate.convertAndSend("currency", message);

        awaitForQueueUntil(() -> repository.findByStartDt(startDt).get().getExchangeRates().size()
                == message.getPayload().getExchangeRates().size());

        Mockito.verify(consumer).consumeCurrencyRateMessage(Mockito.any());
        Mockito.verify(service, Mockito.times(2)).saveFromMessage(Mockito.any());

        Assertions.assertThat(output.getErr()).isEmpty();
        Assertions.assertThat(output.getOut()).contains("Received message from queue");
        Optional<ExchangeData> actual = repository.findByStartDt(startDt);

        Assertions.assertThat(actual).isPresent()
                .get()
                .extracting(ExchangeData::getExchangeRates)
                .extracting(List::size)
                .isEqualTo(message.getPayload().getExchangeRates().size());

        Assertions.assertThat(repository.findAll()).hasSize(1);

    }

    void awaitForQueueUntil(Callable<Boolean> until) {
        await().atMost(20, TimeUnit.SECONDS).until(until);
    }

}
