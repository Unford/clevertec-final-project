package ru.clevertec.banking.currency.configuration;

import net.datafaker.Faker;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.clevertec.banking.currency.util.CurrencyDataFactory;

@TestConfiguration
public class DataFakerConfiguration {

    @Bean
    public Faker faker() {
        return new Faker();
    }

    @Bean
    public CurrencyDataFactory currencyDataFactory(Faker faker){
        return new CurrencyDataFactory(faker);
    }


}
