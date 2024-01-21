package ru.clevertec.banking.customer.dto.response.client.account;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AccountWithCardResponse {

    private String name;

    private String iban;

    private String ibanReadable;

    private BigDecimal amount;

    private String currencyCode;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private LocalDate openDate;

    private boolean mainAcc;

    private String customerId;

    private String customerType;

    private BigDecimal rate;

    private List<CardResponse> cards;
}
