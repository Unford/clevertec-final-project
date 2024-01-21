package ru.clevertec.banking.customer.testutil.builders;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.clevertec.banking.customer.dto.response.client.account.AccountWithCardResponse;
import ru.clevertec.banking.customer.dto.response.client.account.CardResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class AccountWithCardResponseTestBuilder implements TestBuilder<AccountWithCardResponse> {

    private String name = "Account";
    private String iban = "AABBCCCDDDDEEEEEEEEEEEEEEEE";
    private String ibanReadable = "AABB CCC DDDD EEEE EEEE EEEE EEEE";
    private BigDecimal amount = BigDecimal.valueOf(2100);
    private String currencyCode = "BYN";
    private LocalDate openDate = LocalDate.of(2024, 1, 1);
    private boolean mainAcc = true;
    private String customerId = "1a72a05f-4b8f-43c5-a889-1ebc6d9dc729";
    private String customerType = "LEGAL";
    private BigDecimal rate = BigDecimal.valueOf(0.01);
    private List<CardResponse> cards = Arrays.asList(
            new CardResponse("1111111111111111", "1111 1111 1111 1111",
                             "AABBCCCDDDDEEEEEEEEEEEEEEEE", "1124145", "LEGAL",
                             "CARDHOLDER NAME", "ACTIVE"),
            new CardResponse("2222222222222222", "2222 2222 2222 2222",
                             "BBAADDDCCCCEEEEEEEEEEEEEEEE", "2224145",
                             "PHYSIC", "CARDHOLDER2 NAME", "ACTIVE")
    );

    @Override
    public AccountWithCardResponse build() {
        return new AccountWithCardResponse(name, iban, ibanReadable, amount, currencyCode, openDate, mainAcc,
                                           customerId, customerType, rate, cards);
    }
}
