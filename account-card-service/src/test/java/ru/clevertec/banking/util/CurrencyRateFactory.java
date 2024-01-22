package ru.clevertec.banking.util;

import lombok.experimental.UtilityClass;
import ru.clevertec.banking.dto.currencyRate.ExchangeRateDto;
import ru.clevertec.banking.dto.currencyRate.ExchangeRateResponse;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@UtilityClass
public class CurrencyRateFactory {

    public ExchangeRateResponse getExchangeRateResponse() {
        return new ExchangeRateResponse(
                OffsetDateTime.parse("2024-01-03T13:56:51.604498616+03:00"),
                getExchangeRateDto());
    }

    private List<ExchangeRateDto> getExchangeRateDto() {
        return List.of(
                new ExchangeRateDto(
                        BigDecimal.valueOf(3.33),
                        BigDecimal.valueOf(3.43),
                        "EUR",
                        "BYN"),
                new ExchangeRateDto(
                        BigDecimal.valueOf(3.05),
                        BigDecimal.valueOf(3.15),
                        "USD",
                        "BYN"),
                new ExchangeRateDto(
                        BigDecimal.valueOf(1.075),
                        BigDecimal.valueOf(1.1),
                        "EUR",
                        "USD")
        );
    }
}
