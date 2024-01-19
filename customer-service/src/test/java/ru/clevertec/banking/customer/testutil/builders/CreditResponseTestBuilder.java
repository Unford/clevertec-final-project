package ru.clevertec.banking.customer.testutil.builders;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.clevertec.banking.customer.dto.response.client.credit.CreditResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class CreditResponseTestBuilder implements TestBuilder<CreditResponse> {
    private UUID customerId = UUID.fromString("1a72a05f-4b8f-43c5-a889-1ebc6d9dc729");
    private String contractNumber = "11-0216444-2-0";
    private LocalDate contractStartDate = LocalDate.of(2022, 1, 1);
    private BigDecimal totalDebt = new BigDecimal("8113.00");
    private BigDecimal currentDebt = new BigDecimal("361.00");
    private String currency = "BYN";
    private LocalDate repaymentDate = LocalDate.of(2022, 2, 1);
    private BigDecimal rate = new BigDecimal("0.01");
    private String iban = "BY11 1111 1111 1111 1111 1111 111";
    private Boolean possibleRepayment = true;
    private Boolean isClosed = false;
    private String customerType = "PHYSIC";


    @Override
    public CreditResponse build() {
        return new CreditResponse(customerId, contractNumber, contractStartDate, totalDebt, currentDebt, currency,
                                  repaymentDate, rate, iban, possibleRepayment, isClosed, customerType);
    }
}
