package ru.clevertec.banking.currency.model.dto.message;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Accessors(chain = true)
public class CurrencyRateMessagePayload {
    private OffsetDateTime startDt;
    private List<ExchangeRateDto> exchangeRates;
}
