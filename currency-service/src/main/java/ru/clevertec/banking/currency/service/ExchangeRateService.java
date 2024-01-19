package ru.clevertec.banking.currency.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.banking.advice.exception.ResourceNotFoundException;
import ru.clevertec.banking.currency.mapper.ExchangeDataMapper;
import ru.clevertec.banking.currency.model.domain.ExchangeData;
import ru.clevertec.banking.currency.model.dto.message.CurrencyRateMessagePayload;
import ru.clevertec.banking.currency.model.dto.response.ExchangeRateResponse;
import ru.clevertec.banking.currency.repository.ExchangeDataRepository;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExchangeRateService {
    private final ExchangeDataRepository exchangeDataRepository;
    private final ExchangeDataMapper exchangeDataMapper;


    @Transactional
    public ExchangeData saveFromMessage(CurrencyRateMessagePayload messagePayload) {
        ExchangeData exchangeData = exchangeDataRepository.findByStartDt(messagePayload.getStartDt())
                .map(c -> exchangeDataMapper.updateExchangeData(messagePayload, c))
                .orElseGet(() -> exchangeDataMapper.toExchangeData(messagePayload));
        return exchangeDataRepository.save(exchangeData);
    }

    public ExchangeRateResponse findLastExchangesByDate(OffsetDateTime dateTime) {
        return exchangeDataRepository.findFirstByStartDtIsLessThanEqualOrderByStartDtDesc(dateTime)
                .map(exchangeDataMapper::toExchangeResponse)
                .orElseThrow(() -> new ResourceNotFoundException("exchange rates are empty"));
    }


}
