package ru.clevertec.banking.currency.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.clevertec.banking.currency.controller.CurrencyController;
import ru.clevertec.banking.currency.model.dto.response.ExchangeRateResponse;
import ru.clevertec.banking.currency.service.ExchangeRateService;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("api/v1/currencies")
@RequiredArgsConstructor
public class CurrencyControllerImpl implements CurrencyController {
    private final ExchangeRateService rateService;

    @Override
    @GetMapping
    public ExchangeRateResponse findLatestCurrencies(@RequestParam(name = "dateTime",
            defaultValue = "#{T(java.time.OffsetDateTime).now()}") OffsetDateTime dateTime) {
        return rateService.findLastExchangesByDate(dateTime);
    }

}
