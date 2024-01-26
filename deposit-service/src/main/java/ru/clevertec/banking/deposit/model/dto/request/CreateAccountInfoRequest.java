package ru.clevertec.banking.deposit.model.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.clevertec.banking.deposit.validation.CurrencyString;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateAccountInfoRequest {
    @NotBlank
    @Size(min = 5, max = 50)
    private String accIban;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    @PastOrPresent
    private LocalDate accOpenDate;
    @Positive
    private BigDecimal currAmount;
    @NotNull
    @CurrencyString
    private String currAmountCurrency;
}
