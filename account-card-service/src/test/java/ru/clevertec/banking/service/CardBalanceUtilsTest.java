package ru.clevertec.banking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.banking.dto.card.Balance;
import ru.clevertec.banking.dto.currencyRate.ExchangeRateResponse;
import ru.clevertec.banking.entity.Account;
import ru.clevertec.banking.entity.Card;
import ru.clevertec.banking.exception.RestApiServerException;
import ru.clevertec.banking.feign.CurrencyRateClient;
import ru.clevertec.banking.mapper.CardMapper;
import ru.clevertec.banking.util.AccountFactory;
import ru.clevertec.banking.util.CardBalanceUtils;
import ru.clevertec.banking.util.CardFactory;
import ru.clevertec.banking.util.CurrencyRateFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
public class CardBalanceUtilsTest {
    @Mock
    private CurrencyRateClient client;

    @Spy
    private CardMapper mapper = Mappers.getMapper(CardMapper.class);

    @InjectMocks
    private CardBalanceUtils cardBalanceUtils;

    private static final ExchangeRateResponse rateResponse = CurrencyRateFactory.getExchangeRateResponse();
    private Account account;
    private Card card;

    @BeforeEach
    void init() {
        account = AccountFactory.getAccountWithCards(new ArrayList<>(), false);
        card = CardFactory.getCard(account, false);
        account.setCards(List.of(card));
    }


    @Test
    @DisplayName("test should return expected response")
    void getBalanceTest() {
        doReturn(rateResponse)
                .when(client).getCurrency();

        Balance actual = cardBalanceUtils.getBalance(card);
        Balance expected = new Balance("BYN", "2100.00", Map.of("EUR", BigDecimal.valueOf(612.24)
                , "USD", BigDecimal.valueOf(666.66)));

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("test should return expected response")
    void getBalanceSecondTest() {
        doReturn(rateResponse)
                .when(client).getCurrency();

        card.getAccount().setCurrencyCode("EUR");

        Balance actual = cardBalanceUtils.getBalance(card);
        Balance expected = new Balance("EUR", "2100.00", Map.of(
                "BYN", BigDecimal.valueOf(6993.00).setScale(2, RoundingMode.DOWN)
                , "USD", BigDecimal.valueOf(2257.50).setScale(2, RoundingMode.DOWN)));

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("test should return response with null account")
    void getBalanceWithNullAccountTest() {
        doReturn(rateResponse)
                .when(client).getCurrency();

        card.setAccount(null);

        Balance actual = cardBalanceUtils.getBalance(card);

        assertThat(actual.balance()).isEqualTo("account not found");
        assertThat(actual.main_currency_card()).isEqualTo("account not found");
        assertThat(actual.in_other_currencies()).isEmpty();
    }

    @Test
    @DisplayName("test should throw RestApiServerException")
    void getBalanceThrowExceptionTest() {
        doReturn(rateResponse)
                .when(client).getCurrency();

        card.getAccount().setCurrencyCode("12498");

        assertThatThrownBy(() -> cardBalanceUtils.getBalance(card))
                .isInstanceOf(RestApiServerException.class);
    }
}
