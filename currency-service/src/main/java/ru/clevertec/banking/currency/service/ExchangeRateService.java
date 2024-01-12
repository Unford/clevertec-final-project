package ru.clevertec.banking.currency.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.banking.advice.exception.ResourceNotFoundException;
import ru.clevertec.banking.currency.mapper.ExchangeDataMapper;
import ru.clevertec.banking.currency.model.dto.message.CurrencyRateMessagePayload;
import ru.clevertec.banking.currency.model.dto.response.ExchangeRateResponse;
import ru.clevertec.banking.currency.repository.ExchangeDataRepository;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExchangeRateService {
    private final ExchangeDataRepository exchangeDataRepository;
    private final ExchangeDataMapper exchangeDataMapper;


    @Transactional
    public void saveFromMessage(CurrencyRateMessagePayload messagePayload) {
        Optional.of(exchangeDataMapper.toExchangeData(messagePayload))
                .ifPresent(exchangeDataRepository::save);
    }

    public ExchangeRateResponse findLastExchangesByDate(OffsetDateTime dateTime) {
        return exchangeDataRepository.findFirstByStartDtIsLessThanEqualOrderByStartDtDescCreatedAtDesc(dateTime)
                .map(exchangeDataMapper::toExchangeResponse)
                .orElseThrow(() -> new ResourceNotFoundException("exchange rates are empty"));
    }



}
