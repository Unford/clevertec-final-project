package ru.clevertec.banking.currency.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.clevertec.banking.currency.model.domain.ExchangeData;
import ru.clevertec.banking.currency.model.domain.ExchangeRate;
import ru.clevertec.banking.currency.model.dto.response.ExchangeRateResponse;
import ru.clevertec.banking.currency.service.ExchangeRateService;

import java.util.List;

@RestController
@RequestMapping("api/v1/currencies")
@RequiredArgsConstructor
public class CurrencyController {
    private final ExchangeRateService rateService;
    @GetMapping
    public ExchangeRateResponse findLatestCurrencies() {
        return rateService.findLastExchangeData();
    }

}
