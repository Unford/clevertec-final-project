package ru.clevertec.banking.dto.card;

import java.util.UUID;

public record CardResponse(String card_number,
                           String card_number_readable,
                           String iban,
                           UUID customer_id,
                           String customer_type,
                           String cardholder,
                           String card_status) {
}
