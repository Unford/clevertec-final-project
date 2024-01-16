package ru.clevertec.banking.deposit.model.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import ru.clevertec.banking.deposit.model.DepositType;
import ru.clevertec.banking.deposit.model.TermScale;

import java.math.BigDecimal;
import java.time.LocalDate;

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
