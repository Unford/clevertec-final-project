package ru.clevertec.banking.dto.account;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record AccountRequestForUpdate(
        @NotNull(message = "The account iban cannot be empty")
        String iban,
        String name,
        Boolean main_acc,
        @Pattern(regexp = "LEGAL|PHYSIC",
                message = "Acceptable customer_type are only: LEGAL or PHYSIC")
        String customer_type) {
}
