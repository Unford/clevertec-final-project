package ru.clevertec.banking.dto.account;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record AccountResponse(String name,
                              String iban,
                              String iban_readable,
                              BigDecimal amount,
                              String currency_code,
                              @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
                              LocalDate open_date,
                              boolean main_acc,
                              UUID customer_id,
                              String customer_type,
                              BigDecimal rate) {
}
