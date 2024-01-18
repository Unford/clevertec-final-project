package ru.clevertec.banking.deposit.util;

import org.springframework.boot.test.context.SpringBootTest;

import java.lang.annotation.*;

@SpringBootTest
@SpringUnitCompositeTest
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface SpringBootCompositeTest {
}
