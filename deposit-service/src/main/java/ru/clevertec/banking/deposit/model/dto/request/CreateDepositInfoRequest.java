package ru.clevertec.banking.deposit.model.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import ru.clevertec.banking.deposit.model.DepositType;
import ru.clevertec.banking.deposit.model.TermScale;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateDepositInfoRequest {
    @NotNull
    @Positive
    private BigDecimal rate;
    @NotNull
    @Positive
    private Integer termVal;
    @NotNull
    private TermScale termScale;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private LocalDate expDate;
    @NotNull
    private DepositType depType;
    @NotNull
    private Boolean autoRenew;
}
