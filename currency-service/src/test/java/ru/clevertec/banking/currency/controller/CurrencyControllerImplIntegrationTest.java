package ru.clevertec.banking.currency.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.clevertec.banking.currency.configuration.DataFakerConfiguration;
import ru.clevertec.banking.currency.configuration.PostgresContainerConfiguration;
import ru.clevertec.banking.currency.consumer.CurrencyRateConsumer;
import ru.clevertec.banking.currency.service.ExchangeRateService;
import ru.clevertec.banking.currency.util.CurrencyDataFactory;
import ru.clevertec.banking.currency.util.SpringUnitCompositeTest;

import java.time.OffsetDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("integration")
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {PostgresContainerConfiguration.class, DataFakerConfiguration.class})
@EnableAutoConfiguration(exclude = {RabbitAutoConfiguration.class})
@SpringUnitCompositeTest
class CurrencyControllerImplIntegrationTest {
    @MockBean
    CurrencyRateConsumer consumer;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    CurrencyDataFactory dataFactory;
    @Autowired
    ObjectMapper objectMapper;

    @SpyBean
    ExchangeRateService service;

    @Captor
    private ArgumentCaptor<OffsetDateTime> argumentCaptor;

    @Test
    void shouldReturnNotFoundStatusWhenFindLatestCurrenciesByDate() throws Exception {

        mockMvc.perform(get("/api/v1/currencies")
                        .param("dateTime", "2024-01-03T13:56:51.604498616Z"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
        ;
    }

    @Test
    void shouldReturnRatesWhenFindLatestCurrenciesByDate() throws Exception {
        String expected = "2024-01-23T12:45:00Z";
        int expectedSize = 4;

        mockMvc.perform(get("/api/v1/currencies")
                        .param("dateTime", "2024-01-23T13:56:51.604498616Z"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.startDt").value(expected))
                .andExpect(jsonPath("$.exchangeRates.size()").value(expectedSize))
        ;
    }


    @Test
    void shouldReturnRatesWhenFindLatestCurrenciesByNow() throws Exception {
        OffsetDateTime now = OffsetDateTime.now();

        Mockito.doCallRealMethod()
                .when(service)
                .findLastExchangesByDate(Mockito.any());


        mockMvc.perform(get("/api/v1/currencies"))
                .andDo(print());

        Mockito.verify(service).findLastExchangesByDate(argumentCaptor.capture());
        OffsetDateTime actual = argumentCaptor.getValue();
        Assertions.assertThat(actual).isAfterOrEqualTo(now);


    }

}
