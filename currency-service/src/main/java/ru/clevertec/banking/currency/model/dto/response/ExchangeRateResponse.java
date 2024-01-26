package ru.clevertec.banking.currency.model.dto.response;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.clevertec.banking.currency.model.dto.message.ExchangeRateDto;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Accessors(chain = true)
public class ExchangeRateResponse{
    private OffsetDateTime startDt;
    private List<ExchangeRateDto> exchangeRates;

}
