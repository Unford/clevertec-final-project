package ru.clevertec.banking.customer.dto.response.client.account;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CardResponse {

    private String cardNumber;

    private String cardNumberReadable;

    private String iban;

    private String customerId;

    private String customerType;

    private String cardholder;

    private String cardStatus;
}
