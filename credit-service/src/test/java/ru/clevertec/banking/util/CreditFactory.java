package ru.clevertec.banking.util;

import ru.clevertec.banking.dto.CreditRequest;
import ru.clevertec.banking.dto.CreditRequestForUpdate;
import ru.clevertec.banking.dto.CreditResponse;
import ru.clevertec.banking.entity.Credit;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class CreditFactory {

    public static CreditRequest getRequest(){
        return new CreditRequest(
                "c5814d79-3e74-4d6b-b527-9fe5108f8e2d",
                "11-0216512-2-0",
                LocalDate.of(2001,3,25),
                BigDecimal.valueOf(100500.500),
                BigDecimal.valueOf(888.88),
                "USD",
                LocalDate.of(2022,11,11),
                BigDecimal.valueOf(11.11),
                "DE98770400440532013000234500",
                true,
        false,
                "PHYSIC"
        );
    }

    public static CreditResponse getResponse(){
        return new CreditResponse(
                UUID.fromString("c5814d79-3e74-4d6b-b527-9fe5108f8e2d"),
                "11-0216512-2-0",
                LocalDate.of(2001,3,25),
                BigDecimal.valueOf(100500.500),
                BigDecimal.valueOf(888.88),
                "USD",
                LocalDate.of(2022,11,11),
                BigDecimal.valueOf(11.11),
                "DE98770400440532013000234500",
                true,
                false,
                "PHYSIC"
        );
    }

    public static Credit getCredit(){
        return new Credit(
                UUID.fromString("c5814d79-3e74-4d6b-b527-9fe5108f8e2d"),
                "11-0216512-2-0",
                LocalDate.of(2001,3,25),
                BigDecimal.valueOf(100500.500),
                BigDecimal.valueOf(888.88),
                "USD",
                LocalDate.of(2022,11,11),
                BigDecimal.valueOf(11.11),
                "DE98770400440532013000234500",
                true,
                false,
                "PHYSIC",
                false);
    }

    public static CreditRequestForUpdate getRequestForUpdate(){
        return new CreditRequestForUpdate(
                "11-0216512-2-0",
                LocalDate.of(2030,11,11),
                BigDecimal.valueOf(22.11),
                false,
                false,
                "LEGAL");
    }
}
