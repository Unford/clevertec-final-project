package ru.clevertec.banking.dto.account;

import java.util.Map;

public record AccountMessage(
        Map<String, String> header,
        AccountRequest payload) {
}
