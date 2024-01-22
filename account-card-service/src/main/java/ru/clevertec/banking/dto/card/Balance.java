package ru.clevertec.banking.dto.card;

import java.math.BigDecimal;
import java.util.Map;

public record Balance(String main_currency_card,
                      String balance,
                      Map<String, BigDecimal> in_other_currencies) {
}
