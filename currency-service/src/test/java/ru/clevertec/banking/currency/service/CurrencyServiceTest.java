package ru.clevertec.banking.currency.service;

import lombok.AllArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ContextConfiguration;
import ru.clevertec.banking.advice.exception.ResourceNotFoundException;
import ru.clevertec.banking.currency.configuration.CurrencyServiceUnitTestConfiguration;
import ru.clevertec.banking.currency.configuration.DataFakerConfiguration;
import ru.clevertec.banking.currency.configuration.PostgresContainerConfiguration;
import ru.clevertec.banking.currency.mapper.ExchangeDataMapper;
import ru.clevertec.banking.currency.model.domain.ExchangeData;
import ru.clevertec.banking.currency.model.dto.message.CurrencyRateMessagePayload;
import ru.clevertec.banking.currency.model.dto.response.ExchangeRateResponse;
import ru.clevertec.banking.currency.repository.ExchangeDataRepository;
import ru.clevertec.banking.currency.util.CurrencyDataFactory;
import ru.clevertec.banking.currency.util.SpringBootCompositeTest;
import ru.clevertec.banking.currency.util.SpringUnitCompositeTest;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@SpringUnitCompositeTest
@AllArgsConstructor
class CurrencyServiceTest {
    CurrencyDataFactory dataFactory;

    @Nested
    @AllArgsConstructor
    @ExtendWith(MockitoExtension.class)
    @ContextConfiguration(classes = {CurrencyServiceUnitTestConfiguration.class})
    @Tag("unit")
    class CurrencyUnitTest {
        @Autowired
        ExchangeRateService service;
        @MockBean
        ExchangeDataRepository repository;
        @SpyBean
        ExchangeDataMapper mapper;


        @Test
        void shouldThrowNotFoundExceptionWhenFindByDate() {
            OffsetDateTime dateTime = OffsetDateTime.parse("2024-01-03T13:56:51.604498616Z");


            Mockito.when(repository.findFirstByStartDtIsLessThanEqualOrderByStartDtDesc(Mockito.any()))
                    .thenReturn(Optional.empty());


            Assertions.assertThatThrownBy(() -> service.findLastExchangesByDate(dateTime))
                    .isInstanceOf(ResourceNotFoundException.class);

            Mockito.verify(repository).findFirstByStartDtIsLessThanEqualOrderByStartDtDesc(Mockito.any());

        }

        @Test
        void shouldReturnLatestExchangeRatesByDate() {
            ExchangeData exchangeData = dataFactory.createExchangeData();

            Mockito.when(repository.findFirstByStartDtIsLessThanEqualOrderByStartDtDesc(Mockito.any()))
                    .thenReturn(Optional.of(exchangeData));

            ExchangeRateResponse actual = service.findLastExchangesByDate(OffsetDateTime.now());

            Assertions.assertThat(actual).extracting(ExchangeRateResponse::getStartDt)
                    .isEqualTo(exchangeData.getStartDt());

            Assertions.assertThat(actual).extracting(ExchangeRateResponse::getExchangeRates)
                    .isNotNull()
                    .extracting(List::size)
                    .isEqualTo(exchangeData.getExchangeRates().size());

            Mockito.verify(repository).findFirstByStartDtIsLessThanEqualOrderByStartDtDesc(Mockito.any());
        }


        @Test
        void shouldReturnSavedCurrencies() {
            CurrencyRateMessagePayload messagePayload = dataFactory.createCurrencyMessagePayload();
            ExchangeData expected = dataFactory.createExchangeData();

            Mockito.when(repository.findByStartDt(Mockito.any())).thenReturn(Optional.empty());
            Mockito.when(repository.save(Mockito.any())).thenReturn(expected);

            ExchangeData actual = service.saveFromMessage(messagePayload);

            Assertions.assertThat(actual).isEqualTo(expected);
            Mockito.verify(repository).findByStartDt(Mockito.any());
            Mockito.verify(repository).save(Mockito.any());

        }

        @Test
        void shouldReturnUpdatedCurrencies() {
            CurrencyRateMessagePayload messagePayload = dataFactory.createCurrencyMessagePayload();
            ExchangeData expected = dataFactory.createExchangeData();

            Mockito.when(repository.findByStartDt(Mockito.any())).thenReturn(Optional.of(expected));
            Mockito.when(repository.save(Mockito.any())).thenReturn(expected);

            ExchangeData actual = service.saveFromMessage(messagePayload);

            Assertions.assertThat(actual).isEqualTo(expected);
            Mockito.verify(repository).findByStartDt(Mockito.any());
            Mockito.verify(repository).save(Mockito.any());
        }

    }


    @Nested
    @SpringBootCompositeTest
    @ContextConfiguration(classes = {PostgresContainerConfiguration.class, DataFakerConfiguration.class})
    @AllArgsConstructor
    @Tag("integration")
    class CurrencyServiceIntegrationTest {
        ExchangeRateService service;
        ExchangeDataRepository repository;


        @Test
        void shouldThrowNotFoundExceptionWhenFindByDate() {
            OffsetDateTime dateTime = OffsetDateTime.parse("2024-01-03T13:56:51.604498616Z");
            Assertions.assertThatThrownBy(() -> service.findLastExchangesByDate(dateTime))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        void shouldReturnLatestExchangeRatesByDate() {
            OffsetDateTime expectedDateTime = OffsetDateTime.parse("2024-01-22T09:30:00Z");
            int expectedSize = 4;

            OffsetDateTime dateTime = OffsetDateTime.of(2024, 1, 23, 12, 44,
                    0, 0, ZoneOffset.UTC);
            ExchangeRateResponse actual = service.findLastExchangesByDate(dateTime);

            Assertions.assertThat(actual).extracting(ExchangeRateResponse::getStartDt)
                    .isEqualTo(expectedDateTime);

            Assertions.assertThat(actual).extracting(ExchangeRateResponse::getExchangeRates)
                    .isNotNull()
                    .extracting(List::size)
                    .isEqualTo(expectedSize);
        }


        @Test
        void shouldReturnSavedCurrencies() {
            CurrencyRateMessagePayload messagePayload = dataFactory.createCurrencyMessagePayload();

            ExchangeData actual = service.saveFromMessage(messagePayload);

            Assertions.assertThat(actual)
                    .extracting(ExchangeData::getId)
                    .isNotNull();
            Assertions.assertThat(repository.findAll()).hasSizeGreaterThan(5);
        }

        @Test
        void shouldReturnUpdatedCurrencies() {
            CurrencyRateMessagePayload initMessagePayload = dataFactory.createCurrencyMessagePayload();
            OffsetDateTime startDt = initMessagePayload.getStartDt();
            service.saveFromMessage(initMessagePayload);

            CurrencyRateMessagePayload payload = dataFactory
                    .createCurrencyMessagePayload(initMessagePayload.getExchangeRates().size() + 1)
                    .setStartDt(startDt);

            ExchangeData actual = service.saveFromMessage(payload);

            Assertions.assertThat(actual)
                    .extracting(ExchangeData::getExchangeRates)
                    .extracting(List::size)
                    .isEqualTo(payload.getExchangeRates().size());



        }

    }

}
