package ru.clevertec.banking.util;

import lombok.experimental.UtilityClass;
import ru.clevertec.banking.dto.card.CardRequest;
import ru.clevertec.banking.dto.card.CardResponse;
import ru.clevertec.banking.entity.Account;
import ru.clevertec.banking.entity.Card;

import java.util.UUID;

@UtilityClass
public class CardFactory {

    public CardRequest getCardRequest(){
        return new CardRequest("5200000000001096",
                "5200 0000 0000 1096",
                "AABBCCCDDDDEEEEEEEEEEEEEEEEE",
                "1a72a05f-4b8f-43c5-a889-1ebc6d9dc729",
                "LEGAL",
                "CARDHOLDER NAME",
                "ACTIVE");
    }

    public CardResponse getCardResponse(){
        return new CardResponse("5200000000001096",
                "5200 0000 0000 1096",
                "AABBCCCDDDDEEEEEEEEEEEEEEEEE",
                UUID.fromString("1a72a05f-4b8f-43c5-a889-1ebc6d9dc729"),
                "LEGAL",
                "CARDHOLDER NAME",
                "ACTIVE");
    }

    public Card getCard(Account account, boolean isDelete){
        return new Card("5200000000001096",
                "AABBCCCDDDDEEEEEEEEEEEEEEEEE",
                UUID.fromString("1a72a05f-4b8f-43c5-a889-1ebc6d9dc729"),
                "LEGAL",
                "CARDHOLDER NAME",
                "ACTIVE",
                account,
                isDelete);
    }
}
