package ru.clevertec.banking.customer.dto.response.client.deposit;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.clevertec.banking.customer.entity.CustomerType;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DepositResponse {
    private Long id;
    private UUID customerId;
    private CustomerType customerType;
    private AccountInfoResponse accInfo;
    private DepositInfoResponse depInfo;
}

