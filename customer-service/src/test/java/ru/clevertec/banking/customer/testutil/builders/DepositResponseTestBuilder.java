package ru.clevertec.banking.customer.testutil.builders;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.clevertec.banking.customer.dto.response.client.deposit.*;
import ru.clevertec.banking.customer.entity.CustomerType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class DepositResponseTestBuilder implements TestBuilder<DepositResponse> {

    private Long id = 1L;
    private UUID customerId = UUID.fromString("1a72a05f-4b8f-43c5-a889-1ebc6d9dc729");
    private CustomerType customerType = CustomerType.LEGAL;
    private AccountInfoResponse accInfo = new AccountInfoResponse(
            "AABBCCCDDDDEEEEEEEEEEEEEEEE", LocalDate.of(2024, 1, 1)
            , BigDecimal.valueOf(2400), "BYN"
    );
    private DepositInfoResponse depInfo = new DepositInfoResponse(
            BigDecimal.valueOf(14.5), 24, TermScale.M,
            LocalDate.of(2024, 1, 1), DepositType.REVOCABLE, true
    );

    @Override
    public DepositResponse build() {
        return new DepositResponse(id, customerId, customerType, accInfo, depInfo);
    }
}
