package ru.clevertec.banking.dto.currencyRate;

import java.math.BigDecimal;

public record ExchangeRateDto(BigDecimal buyRate,
                              BigDecimal sellRate,
                              String srcCurr,
                              String reqCurr) {
}
