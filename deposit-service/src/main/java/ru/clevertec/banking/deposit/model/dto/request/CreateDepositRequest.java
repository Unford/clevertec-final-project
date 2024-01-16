package ru.clevertec.banking.deposit.model.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.clevertec.banking.deposit.model.CustomerType;
import ru.clevertec.banking.deposit.validation.OpenDateBeforeExpiration;
import ru.clevertec.banking.deposit.validation.UniqueAccountIban;

import java.util.UUID;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@OpenDateBeforeExpiration
@UniqueAccountIban
public class CreateDepositRequest {
    @NotNull
    private UUID customerId;
    @NotNull
    private CustomerType customerType;
    @Valid
    @NotNull
    private CreateAccountInfoRequest accInfo;
    @Valid
    @NotNull
    private CreateDepositInfoRequest depInfo;

}
