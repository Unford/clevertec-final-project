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

/* todo
{
    "header": {
        "message_type": "deposit_info"
    },
    "payload": {
        "customer_id": "1a72a05f-4b8f-43c5-a889-1ebc6d9dc729",
        "customer_type" : "LEGAL/PHYSIC",
        "acc_info": {
            "acc_iban": "AABBCCCDDDDEEEEEEEEEEEEEEEE",
            "acc_open_date": "dd.MM.yyyy",
            "curr_amount": 3000.00,
            "curr_amount_currency": "BYN"
        },
        "dep_info": {
            "rate": 14.50,
            "term_val": 24,
            "term_scale": "M/D",
            "exp_date": "dd.MM.yyyy",
            "dep_type": "REVOCABLE/IRREVOCABLE",
            "auto_renew": true
        }
    }
}

 */

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
