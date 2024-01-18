package ru.clevertec.banking.deposit.configuration;

import net.datafaker.Faker;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.clevertec.banking.deposit.util.RandomDepositFactory;

@TestConfiguration
public class DataFakerConfiguration {

    @Bean
    public Faker faker() {
        return new Faker();
    }

    @Bean
    public RandomDepositFactory randomDepositFactory(Faker faker){
        return new RandomDepositFactory(faker);
    }


}
