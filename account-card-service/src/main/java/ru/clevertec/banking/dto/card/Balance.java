package ru.clevertec.banking.dto.card;

import java.util.Map;

public record Balance(String main_currency_card,
                      String balance,
                      Map<String, String> in_other_currencies) {
}
