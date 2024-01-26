package ru.clevertec.banking.dto.account;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import ru.clevertec.banking.dto.validator.AccountValidation;

import java.math.BigDecimal;
import java.time.LocalDate;

@AccountValidation
public record AccountRequest(
        @NotNull(message = "The name account cannot be empty")
        @JsonProperty("name")
        String name,
        @NotNull(message = "The iban cannot be empty")
        @JsonProperty("iban")
        String iban,
        @Positive
        @JsonProperty("amount")
        BigDecimal amount,
        @NotNull(message = "The currency_code cannot be empty")
        @JsonProperty("currency_code")
        String currency_code,
        @NotNull(message = "The open_date cannot be empty")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
        @JsonProperty("open_date")
        LocalDate open_date,
        @NotNull(message = "The main_acc property cannot be empty")
        @JsonProperty("main_acc")
        Boolean main_acc,
        @NotNull
        @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
                message = "Invalid customer_id format")
        @JsonProperty("customer_id")
        String customer_id,
        @NotNull
        @Pattern(regexp = "LEGAL|PHYSIC", message = "Acceptable customer_type are only: LEGAL or PHYSIC")
        @JsonProperty("customer_type")
        String customer_type,
        @Positive
        @JsonProperty("rate")
        BigDecimal rate) {
}
