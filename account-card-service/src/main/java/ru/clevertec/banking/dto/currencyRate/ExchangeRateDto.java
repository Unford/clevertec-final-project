package ru.clevertec.banking.dto.currencyRate;

import java.math.BigDecimal;
import java.util.Objects;

public record ExchangeRateDto(BigDecimal buyRate,
                              BigDecimal sellRate,
                              String srcCurr,
                              String reqCurr) {

    public ExchangeRateDto(BigDecimal buyRate, BigDecimal sellRate, String srcCurr, String reqCurr) {
        this.buyRate = Objects.requireNonNullElse(buyRate, BigDecimal.ONE);
        this.sellRate = Objects.requireNonNullElse(sellRate, BigDecimal.ONE);
        this.srcCurr = Objects.requireNonNullElse(srcCurr, "");
        this.reqCurr = Objects.requireNonNullElse(reqCurr, "");
    }
}
