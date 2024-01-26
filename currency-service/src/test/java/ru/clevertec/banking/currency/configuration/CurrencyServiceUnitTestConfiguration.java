package ru.clevertec.banking.currency.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import ru.clevertec.banking.currency.mapper.ExchangeDataMapper;
import ru.clevertec.banking.currency.repository.ExchangeDataRepository;
import ru.clevertec.banking.currency.service.ExchangeRateService;

@TestConfiguration
@ComponentScan(basePackageClasses = {ExchangeDataMapper.class})
public class CurrencyServiceUnitTestConfiguration {


    @Bean
    public ExchangeRateService exchangeRateService(ExchangeDataRepository exchangeRateRepository,
                                                   ExchangeDataMapper dataMapper) {
        return new ExchangeRateService(exchangeRateRepository, dataMapper);
    }

}
