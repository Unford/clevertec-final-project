package ru.clevertec.banking.dto;

import java.util.Map;

public record CreditMessage(Map<String, String> header,
                            CreditRequest payload) {
}
