package ru.clevertec.banking.deposit.model.dto.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.clevertec.banking.deposit.model.CustomerType;
import ru.clevertec.banking.deposit.model.DepositType;
import ru.clevertec.banking.deposit.model.TermScale;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;



@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Accessors(chain = true)
public class DepositMessagePayload {
    private UUID customerId;
    private CustomerType customerType;
    private MessageAccountInfo accInfo;
    private MessageDepositInfo depInfo;

    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class MessageAccountInfo {
        private String accIban;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
        private LocalDate accOpenDate;
        private BigDecimal currAmount;
        private String currAmountCurrency;
    }

    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class MessageDepositInfo {
        private BigDecimal rate;

        private Integer termVal;

        private TermScale termScale;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
        private LocalDate expDate;

        private DepositType depType;

        private Boolean autoRenew;
    }

}
