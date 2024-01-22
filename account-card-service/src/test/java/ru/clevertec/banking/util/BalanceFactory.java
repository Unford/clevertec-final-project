package ru.clevertec.banking.util;

import lombok.experimental.UtilityClass;
import ru.clevertec.banking.dto.card.Balance;

import java.math.BigDecimal;
import java.util.HashMap;

@UtilityClass
public class BalanceFactory {
    public Balance getBalance(){
        return new Balance("USD",
                "25000.00",
                new HashMap<>());
    }

    public Balance getBalance(String main_currency, String actualAmount,HashMap<String,BigDecimal> inOtherCurrency){
        return new Balance(main_currency,actualAmount,inOtherCurrency);
    }
}
