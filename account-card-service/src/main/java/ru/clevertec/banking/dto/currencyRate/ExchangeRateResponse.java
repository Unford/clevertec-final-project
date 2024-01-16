package ru.clevertec.banking.dto.currencyRate;

import java.time.OffsetDateTime;
import java.util.List;

public record ExchangeRateResponse(OffsetDateTime startDt,
                                   List<ExchangeRateDto> exchangeRates) {
}
