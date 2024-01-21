package ru.clevertec.banking.customer.dto.response.client.deposit;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import lombok.experimental.Accessors;
import ru.clevertec.banking.customer.entity.CustomerType;

import java.util.UUID;

@Getter
@Setter
@Accessors(chain = true)
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

