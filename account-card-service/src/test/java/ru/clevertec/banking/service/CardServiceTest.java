package ru.clevertec.banking.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import ru.clevertec.banking.advice.exception.ResourceNotFoundException;
import ru.clevertec.banking.dto.card.*;
import ru.clevertec.banking.entity.Card;
import ru.clevertec.banking.exception.ResourceCreateException;
import ru.clevertec.banking.exception.RestApiServerException;
import ru.clevertec.banking.mapper.CardMapper;
import ru.clevertec.banking.repository.CardRepository;
import ru.clevertec.banking.repository.specifications.FilterSpecifications;
import ru.clevertec.banking.service.impl.CardServiceImpl;
import ru.clevertec.banking.util.AccountFactory;
import ru.clevertec.banking.util.CardBalanceUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static ru.clevertec.banking.util.BalanceFactory.*;
import static ru.clevertec.banking.util.CardFactory.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
public class CardServiceTest {

    @Mock
    private CardRepository repository;

    @Mock
    private CardBalanceUtils balanceUtils;

    @Spy
    private CardMapper cardMapper = Mappers.getMapper(CardMapper.class);

    @Spy
    private FilterSpecifications<Card> specifications;

    @InjectMocks
    private CardServiceImpl cardService;

    @Test
    @DisplayName("test should return expected response")
    @SuppressWarnings("unchecked")
    void findByCustomerTest() {
        List<Card> expected = List.of(getCard(null, false), getCard(null, false));

        doReturn(expected)
                .when(repository).findAll(Mockito.any(Specification.class));

        List<CardResponse> actual = cardService.findByCustomer(UUID.randomUUID());

        assertThat(actual).containsExactlyInAnyOrderElementsOf(cardMapper.toListResponse(expected));
    }

    @Test
    @DisplayName("test should return expected page with responses")
    @SuppressWarnings("unchecked")
    void findByIbanTest() {
        Pageable pageable = Pageable.ofSize(3);
        Page<Card> expected = new PageImpl<>(List.of(getCard(null, false), getCard(null, false))
                , pageable, 2);

        doReturn(expected)
                .when(repository).findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class));

        Page<CardResponse> actual = cardService.findByIban("IBAN", pageable);

        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected
                .map(cardMapper::toResponse));
    }

    @Test
    @DisplayName("test should return expected response with balance card")
    void findByCardNumberTest() {
        Card card = getCard(AccountFactory.getAccountWithCards(null, false), false);
        Balance balance = getBalance();

        doReturn(Optional.of(card))
                .when(repository).findCardByCardNumber(Mockito.any(String.class));

        doReturn(balance)
                .when(balanceUtils).getBalance(Mockito.any(Card.class));

        CardCurrencyResponse actual = cardService.findByCardNumber("CARD NUMBER");
        CardCurrencyResponse expected = cardMapper.toCardWithBalance(card, balance);

        assertThat(actual).isEqualTo(expected);
    }


    @Test
    @DisplayName("test should throw ResourceNotFountException")
    void findByCardNumberNotFoundTest() {

        doReturn(Optional.empty())
                .when(repository).findCardByCardNumber(Mockito.any());

        assertThatThrownBy(() -> cardService.findByCardNumber("CARD NUMBER"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("test should return expected page with responses")
    void findAllTest() {
        Pageable pageable = Pageable.ofSize(3);
        Page<Card> expectedPage = new PageImpl<>(List.of(getCard(null, false), getCard(null, false))
                , pageable, 2);

        doReturn(expectedPage)
                .when(repository).findAll(pageable);

        Page<CardResponse> actual = cardService.findAll(pageable);
        Page<CardResponse> expected = expectedPage.map(cardMapper::toResponse);

        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    @DisplayName("test should return expected response")
    void saveTest() {
        CardRequest cardRequest = getCardRequest();
        Card expected = cardMapper.fromRequest(cardRequest);

        doReturn(expected)
                .when(repository).save(expected);

        CardResponse actual = cardService.save(cardRequest);

        assertThat(actual).isEqualTo(cardMapper.toResponse(expected));
    }

    @Test
    @DisplayName("test should throw ResourceCreateException")
    void saveExceptionTest() {
        CardRequest cardRequest = getCardRequest();
        Card expected = cardMapper.fromRequest(cardRequest);

        doReturn(null)
                .when(repository).save(expected);

        assertThatThrownBy(() -> cardService.save(cardRequest))
                .isInstanceOf(ResourceCreateException.class);
    }

    @Test
    @DisplayName("test should correct update and return expected response")
    void updateTest() {
        Card card = getCard(null, false);
        CardRequestForUpdate requestForUpdate = new CardRequestForUpdate(card.getCardNumber(),
                "NEW IBAN", "LEGAL", "NEW");

        CardResponse expected = Optional.of(card)
                .map(c -> cardMapper.updateFromRequest(requestForUpdate, c))
                .map(cardMapper::toResponse)
                .orElseThrow();

        doReturn(Optional.of(card))
                .when(repository).findCardByCardNumber(Mockito.any());

        doAnswer(o -> o.getArguments()[0])
                .when(repository).save(Mockito.any(Card.class));

        CardResponse actual = cardService.update(requestForUpdate);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("the test should check whether the method throws an error if the entity being updated is not found")
    void updateNotFoundExceptionTest() {
        CardRequestForUpdate requestForUpdate = new CardRequestForUpdate("CARD NOT FOUND NUMBER",
                "NEW IBAN", "LEGAL", "NEW");

        doReturn(Optional.empty())
                .when(repository).findCardByCardNumber(Mockito.any());

        assertThatThrownBy(() -> cardService.update(requestForUpdate))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("test should throw RestApiServerException")
    void updateExceptionTest() {
        Card card = getCard(null, false);
        CardRequestForUpdate requestForUpdate = new CardRequestForUpdate(card.getCardNumber(),
                "NEW IBAN", "LEGAL", "NEW");

        doReturn(Optional.of(card))
                .when(repository).findCardByCardNumber(Mockito.any());

        doReturn(null)
                .when(repository).save(Mockito.any(Card.class));

        assertThatThrownBy(() -> cardService.update(requestForUpdate))
                .isInstanceOf(RestApiServerException.class);
    }

    @Test
    @DisplayName("the test should check whether the method is called")
    void deleteByCardNumberTest() {
        cardService.deleteByCardNumber("12344125512");

        verify(repository, times(1)).deleteCardByCardNumber(Mockito.any());
    }
}
