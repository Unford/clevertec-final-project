package ru.clevertec.banking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import ru.clevertec.banking.validator.CreditValidation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@CreditValidation
public record CreditRequest(
        @NotNull
        @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
                message = "Invalid customer_id format")
        @JsonProperty("customer_id")
        String customer_id,
        @NotNull
        @JsonProperty("contractNumber")
        String contractNumber,
        @NotNull(message = "The contractStartDate cannot be empty")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
        @JsonProperty("contractStartDate")
        LocalDate contractStartDate,
        @Positive
        @JsonProperty("totalDebt")
        BigDecimal totalDebt,
        @Positive
        @JsonProperty("currentDebt")
        BigDecimal currentDebt,
        @NotNull(message = "The currency cannot be empty")
        @JsonProperty("currency")
        String currency,
        @NotNull(message = "The repaymentDate cannot be empty")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
        @JsonProperty("repaymentDate")
        LocalDate repaymentDate,
        @Positive
        @JsonProperty("rate")
        BigDecimal rate,
        @NotNull(message = "The iban cannot be empty")
        @JsonProperty("iban")
        String iban,
        @JsonProperty("possibleRepayment")
        Boolean possibleRepayment,
        @NotNull
        @JsonProperty("isClosed")
        Boolean isClosed,
        @NotNull
        @Pattern(regexp = "LEGAL|PHYSIC", message = "Acceptable customer_type are only: LEGAL or PHYSIC")
        @JsonProperty("customer_type")
        String customer_type) {
}
