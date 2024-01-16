package ru.clevertec.banking.deposit.model.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.clevertec.banking.deposit.model.DepositType;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateDepositRequest {
    @Valid
    @NotNull
    private UpdateDepositInfoRequest depInfo;


    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class UpdateDepositInfoRequest {
        private DepositType depType;
        private Boolean autoRenew;
    }
}
