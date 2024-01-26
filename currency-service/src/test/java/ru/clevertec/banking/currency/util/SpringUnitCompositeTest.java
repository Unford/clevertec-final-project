package ru.clevertec.banking.currency.util;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.clevertec.banking.currency.configuration.DataFakerConfiguration;

import java.lang.annotation.*;

@ContextConfiguration(classes = DataFakerConfiguration.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@DisplayNameGeneration(CamelCaseAndUnderscoreNameGenerator.class)
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface SpringUnitCompositeTest {
}
