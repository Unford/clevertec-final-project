package ru.clevertec.banking.dto.card;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CardRequestForUpdate(
        @NotNull(message = "The card_number cannot be empty")
        String card_number,
        String iban,
        @Pattern(regexp = "LEGAL|PHYSIC",
                message = "Acceptable customer_type are only: LEGAL or PHYSIC")
        String customer_type,
        @Pattern(regexp = "ACTIVE|INACTIVE|BLOCKED|NEW",
                message = "Acceptable card_status are only: ACTIVE, INACTIVE, BLOCKED or NEW")
        String card_status) {
}
