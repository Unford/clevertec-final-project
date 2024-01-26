package ru.clevertec.banking.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.banking.advice.exception.ResourceNotFoundException;
import ru.clevertec.banking.dto.card.CardCurrencyResponse;
import ru.clevertec.banking.dto.card.CardRequest;
import ru.clevertec.banking.dto.card.CardRequestForUpdate;
import ru.clevertec.banking.dto.card.CardResponse;
import ru.clevertec.banking.entity.Card;
import ru.clevertec.banking.exception.ResourceCreateException;
import ru.clevertec.banking.exception.RestApiServerException;
import ru.clevertec.banking.mapper.CardMapper;
import ru.clevertec.banking.repository.CardRepository;
import ru.clevertec.banking.repository.specifications.FilterSpecifications;
import ru.clevertec.banking.service.CardService;
import ru.clevertec.banking.util.CardBalanceUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@CacheConfig(cacheNames = "card")
public class CardServiceImpl implements CardService {
    private final CardRepository repository;
    private final CardMapper mapper;
    private final CardBalanceUtils balanceUtils;
    private final FilterSpecifications<Card> specifications;

    @Override
    public List<CardResponse> findByCustomer(UUID uuid) {
        return repository.findAll(specifications.filter(uuid)).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public Page<CardResponse> findByIban(String iban, Pageable pageable) {
        return repository.findAll(specifications.filter(null, iban), pageable)
                .map(mapper::toResponse);
    }

    @Override
    @Cacheable(key = "#cardNumber")
    public CardCurrencyResponse findByCardNumber(String cardNumber) {
        return repository.findCardByCardNumber(cardNumber)
                .map(card -> mapper.toCardWithBalance(card, balanceUtils.getBalance(card)))
                .orElseThrow(() -> new ResourceNotFoundException("Card with card_number: %s not found"
                        .formatted(cardNumber)));
    }

    @Override
    public Page<CardResponse> findAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toResponse);
    }

    @Override
    @Transactional
    @CachePut(key = "#result.card_number()")
    public CardResponse save(CardRequest request) {
        return Optional.of(request)
                .map(mapper::fromRequest)
                .map(repository::save)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceCreateException("Failed to create card"));
    }

    @Override
    @Transactional
    @CachePut(key = "#request.card_number()")
    public CardResponse update(CardRequestForUpdate request) {
        return Optional.of(request)
                .map(CardRequestForUpdate::card_number)
                .map(repository::findCardByCardNumber)
                .map(o -> o.orElseThrow(() -> new ResourceNotFoundException("Card with card_number: %s not found"
                        .formatted(request.card_number()))))
                .map(card -> mapper.updateFromRequest(request, card))
                .map(repository::save)
                .map(mapper::toResponse)
                .orElseThrow(() -> new RestApiServerException("Failed update card with card_number: %s".
                        formatted(request.card_number())));
    }

    @Override
    @Transactional
    @CacheEvict(key = "#cardNumber")
    public void deleteByCardNumber(String cardNumber) {
        repository.deleteCardByCardNumber(cardNumber);
    }

    @Override
    @Transactional
    @CacheEvict(key = "#request.card_number()")
    public void saveOrUpdate(CardRequest request) {
        Optional.of(request)
                .map(CardRequest::card_number)
                .map(repository::findCardByCardNumberWithDeleted)
                .flatMap(o -> o)
                .ifPresentOrElse(card -> repository.save(mapper.updateFromMessage(request, card)),
                        () -> repository.save(mapper.fromRequest(request)));

    }
}
