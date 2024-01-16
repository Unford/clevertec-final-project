package ru.clevertec.banking.deposit.model.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.clevertec.banking.deposit.validation.CurrencyString;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateAccountInfoRequest {
    @NotBlank
    @Size(max = 50)
    private String accIban;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private LocalDate accOpenDate;
    @Positive
    private BigDecimal currAmount;
    @NotNull
    @CurrencyString
    private String currAmountCurrency;
}
