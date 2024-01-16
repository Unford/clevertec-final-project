package ru.clevertec.banking.dto.card;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Pattern;
import ru.clevertec.banking.dto.validator.CardValidation;

import java.util.Map;

@CardValidation
public record CardRequest(
        @NotNull(message = "The card_number cannot be empty")
        @JsonProperty("card_number")
        String card_number,
        @JsonProperty("card_number_readable")
        String card_number_readable,
        @NotNull(message = "The iban cannot be empty")
        @JsonProperty("iban")
        String iban,
        @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
                message = "Invalid customer_id format")
        @JsonProperty("customer_id")
        String customer_id,
        @Pattern(regexp = "LEGAL|PHYSIC", message = "Acceptable customer_type are only: LEGAL or PHYSIC")
        @JsonProperty("customer_type")
        String customer_type,
        @NotNull(message = "The cardholder cannot be empty")
        @JsonProperty("cardholder")
        String cardholder,
        @Pattern(regexp = "ACTIVE|INACTIVE|BLOCKED|NEW",
                message = "Acceptable card_status are only: ACTIVE, INACTIVE, BLOCKED or NEW")
        @JsonProperty("card_status")
        String card_status) {
}
