package ru.clevertec.banking.customer.dto.response.client.credit;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class CreditResponse {
    @JsonProperty("customer_id")
    private UUID customerId;

    private String contractNumber;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private LocalDate contractStartDate;

    private BigDecimal totalDebt;

    private BigDecimal currentDebt;

    private String currency;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private LocalDate repaymentDate;

    private BigDecimal rate;

    private String iban;

    private Boolean possibleRepayment;

    private Boolean isClosed;

    @JsonProperty("customer_type")
    private String customerType;
}