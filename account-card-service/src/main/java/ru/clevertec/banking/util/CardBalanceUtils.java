package ru.clevertec.banking.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.clevertec.banking.dto.card.Balance;
import ru.clevertec.banking.dto.currencyRate.ExchangeRateDto;
import ru.clevertec.banking.dto.currencyRate.ExchangeRateResponse;
import ru.clevertec.banking.entity.Card;
import ru.clevertec.banking.exception.RestApiServerException;
import ru.clevertec.banking.feign.CurrencyRateClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class CardBalanceUtils {
    private final CurrencyRateClient currencyRateApi;

    public Balance getBalance(Card card) {
        ExchangeRateResponse rateResponse = currencyRateApi.getCurrency();

        if (card.getAccount() == null) {
            return new Balance("account not found", "account not found", new HashMap<>());
        } else {
            String mainCurrencyCard = getCurrencyCode(card.getAccount().getCurrencyCode());
            BigDecimal accountAmount = card.getAccount().getAmount();

            Map<String, BigDecimal> args = getBalanceArgs(accountAmount, mainCurrencyCard, rateResponse);

            return new Balance(mainCurrencyCard, accountAmount.setScale(2, RoundingMode.DOWN).toString(), args);
        }
    }

    private String getCurrencyCode(String curr) {
        if (StringUtils.isNumeric(curr)) {
            int currNumeric = Integer.parseInt(curr);
            return Currency.getAvailableCurrencies().stream()
                    .filter(currency -> currency.getNumericCode() == currNumeric)
                    .findAny()
                    .map(Currency::getCurrencyCode)
                    .orElseThrow(() -> new RestApiServerException("Unknown currency type",
                            HttpStatus.INTERNAL_SERVER_ERROR));
        } else return curr;
    }

    private Map<String, BigDecimal> getBalanceArgs(BigDecimal accountAmount, String mainCurrencyCard, ExchangeRateResponse response) {
        List<ExchangeRateDto> currencyList = response.exchangeRates();

        Map<String, BigDecimal> args = new HashMap<>();

        for (ExchangeRateDto rateDto : currencyList) {
            if (rateDto.reqCurr().equals(mainCurrencyCard)) {
                args.put(rateDto.srcCurr(),
                        convert(accountAmount, rateDto.sellRate(), true));
            }
            if (rateDto.srcCurr().equals(mainCurrencyCard)) {
                args.put(rateDto.reqCurr(),
                        convert(accountAmount, rateDto.buyRate(), false));
            }
        }
        return args;
    }

    private BigDecimal convert(BigDecimal amount, BigDecimal curRate, boolean reqCur) {
        return reqCur ? amount.divide(curRate, 2, RoundingMode.DOWN) : amount.multiply(curRate)
                .setScale(2, RoundingMode.DOWN);
    }
}
