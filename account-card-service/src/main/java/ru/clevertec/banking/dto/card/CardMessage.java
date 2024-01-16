package ru.clevertec.banking.dto.card;

import java.util.Map;

public record CardMessage(
        Map<String, String> header,
        CardRequest payload
) {
}
