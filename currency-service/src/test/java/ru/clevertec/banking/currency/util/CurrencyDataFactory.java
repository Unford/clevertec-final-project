package ru.clevertec.banking.currency.util;

import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import ru.clevertec.banking.currency.model.domain.ExchangeData;
import ru.clevertec.banking.currency.model.domain.ExchangeRate;
import ru.clevertec.banking.currency.model.dto.message.CurrencyRateMessage;
import ru.clevertec.banking.currency.model.dto.message.CurrencyRateMessagePayload;
import ru.clevertec.banking.currency.model.dto.message.ExchangeRateDto;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class CurrencyDataFactory {
    private final Faker faker;


    public CurrencyRateMessage createCurrencyMessage() {
        return new CurrencyRateMessage()
                .setHeader(new CurrencyRateMessage.MessageHeader().setMessageType("currency-rate"))
                .setPayload(createCurrencyMessagePayload());
    }

    public CurrencyRateMessage createCurrencyMessage(int ratesCount) {
        return new CurrencyRateMessage()
                .setHeader(new CurrencyRateMessage.MessageHeader().setMessageType("currency-rate"))
                .setPayload(createCurrencyMessagePayload(ratesCount));
    }

    public CurrencyRateMessagePayload createCurrencyMessagePayload(int ratesCount) {
        return new CurrencyRateMessagePayload()
                .setExchangeRates(Stream.generate(this::createExchangeRateDto)
                        .limit(ratesCount)
                        .toList())
                .setStartDt(faker.date().future(100, TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC));

    }

    public CurrencyRateMessagePayload createCurrencyMessagePayload() {
        return createCurrencyMessagePayload(faker.number().numberBetween(1, 10));
    }

    public ExchangeRateDto createExchangeRateDto() {
        return new ExchangeRateDto()
                .setBuyRate(BigDecimal.valueOf(faker.number().randomDouble(2, 1, 10)))
                .setSellRate(BigDecimal.valueOf(faker.number().randomDouble(2, 1, 10)))
                .setReqCurr(faker.currency().code())
                .setSrcCurr(faker.currency().code());
    }

    public ExchangeData createExchangeData() {
        return new ExchangeData().setId((long) faker.number().positive())
                .setStartDt(faker.date().birthday().toInstant().atOffset(ZoneOffset.UTC))
                .setExchangeRates(Stream.generate(this::createExchangeRate)
                        .limit(faker.number().numberBetween(1, 10))
                        .collect(Collectors.toList()));
    }

    public ExchangeRate createExchangeRate() {
        return new ExchangeRate()
                .setBuyRate(BigDecimal.valueOf(faker.number().randomDouble(2, 1, 10)))
                .setSellRate(BigDecimal.valueOf(faker.number().randomDouble(2, 1, 10)))
                .setReqCurr(faker.currency().code())
                .setSrcCurr(faker.currency().code());
    }
}
