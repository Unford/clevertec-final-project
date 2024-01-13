package ru.clevertec.banking.currency.model.dto.message;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class ExchangeRateDto {
    private BigDecimal buyRate;
    private BigDecimal sellRate;
    private String srcCurr;
    private String reqCurr;
}
