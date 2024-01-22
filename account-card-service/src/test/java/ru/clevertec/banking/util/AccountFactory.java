package ru.clevertec.banking.util;


import lombok.experimental.UtilityClass;
import ru.clevertec.banking.dto.account.AccountRequest;
import ru.clevertec.banking.dto.account.AccountResponse;
import ru.clevertec.banking.dto.account.AccountWithCardResponse;
import ru.clevertec.banking.dto.card.CardResponse;
import ru.clevertec.banking.entity.Account;
import ru.clevertec.banking.entity.Card;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@UtilityClass
public class AccountFactory {

    public AccountRequest getAccountRequest(){
        return new AccountRequest("Название счёта",
                "AABBCCCDDDDEEEEEEEEEEEEEEEEE",
                BigDecimal.valueOf(2100.00),
                "933",
                LocalDate.of(2010,11,5),
                true,
                "1a72a05f-4b8f-43c5-a889-1ebc6d9dc729",
                "LEGAL",
                BigDecimal.valueOf(0.01));
    }

    public Account getAccountWithCards(List<Card> cardList,boolean isDelete){
        return new Account("Название счёта",
                "AABBCCCDDDDEEEEEEEEEEEEEEEEE",
                BigDecimal.valueOf(2100.00),
                "933",
                LocalDate.of(2010,11,5),
                true,
                UUID.fromString("1a72a05f-4b8f-43c5-a889-1ebc6d9dc729"),
                "LEGAL",
                BigDecimal.valueOf(0.01),
                cardList,
                isDelete);
    }

    public AccountResponse getAccountResponse(){
        return new AccountResponse("Название счёта",
                "AABBCCCDDDDEEEEEEEEEEEEEEEEE",
                "AABB CCCD DDDE EEEE EEEE EEEE EEEE",
                BigDecimal.valueOf(2100.00),
                "933",
                LocalDate.of(2010,11,5),
                true,
                UUID.fromString("1a72a05f-4b8f-43c5-a889-1ebc6d9dc729"),
                "LEGAL",
                BigDecimal.valueOf(0.01));
    }

    public AccountWithCardResponse getAccountWithCardsResponse(List<CardResponse> cards){
        return new AccountWithCardResponse("Название счёта",
                "AABBCCCDDDDEEEEEEEEEEEEEEEEE",
                "AABB CCCD DDDE EEEE EEEE EEEE EEEE",
                BigDecimal.valueOf(2100.00),
                "933",
                LocalDate.of(2010,11,5),
                true,
                UUID.fromString("1a72a05f-4b8f-43c5-a889-1ebc6d9dc729"),
                "LEGAL",
                BigDecimal.valueOf(0.01),
                cards);
    }
}
