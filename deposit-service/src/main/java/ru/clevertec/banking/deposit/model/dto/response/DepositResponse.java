package ru.clevertec.banking.deposit.model.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.clevertec.banking.deposit.model.CustomerType;

import java.util.UUID;

@Data
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DepositResponse {
    private Long id;
    private UUID customerId;
    private CustomerType customerType;
    private AccountInfoResponse accInfo;
    private DepositInfoResponse depInfo;
}
