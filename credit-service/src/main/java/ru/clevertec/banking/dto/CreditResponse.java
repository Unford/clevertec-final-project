package ru.clevertec.banking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreditResponse(
        UUID customer_id,
        String contractNumber,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
        LocalDate contractStartDate,
        BigDecimal totalDebt,
        BigDecimal currentDebt,
        String currency,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
        LocalDate repaymentDate,
        BigDecimal rate,
        String iban,
        Boolean possibleRepayment,
        Boolean isClosed,
        String customer_type
) {
}
