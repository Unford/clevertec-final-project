package ru.clevertec.banking.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.clevertec.banking.dto.card.CardCurrencyResponse;
import ru.clevertec.banking.dto.card.CardRequest;
import ru.clevertec.banking.dto.card.CardRequestForUpdate;
import ru.clevertec.banking.dto.card.CardResponse;
import ru.clevertec.banking.entity.Card;

import java.util.List;
import java.util.UUID;

public interface CardService {

    void saveOrUpdate(CardRequest request);
    Page<CardResponse> findAll(Pageable pageable);

    CardResponse save(CardRequest request);

    CardResponse update(CardRequestForUpdate request);

    List<CardResponse> findByCustomer(UUID uuid);

    Page<CardResponse> findByIban(String iban, Pageable pageable);

    CardCurrencyResponse findByCardNumber(String cardNumber);

    void deleteByCardNumber(String cardNumber);
}
