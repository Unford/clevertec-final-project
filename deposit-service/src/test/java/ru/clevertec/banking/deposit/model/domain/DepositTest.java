package ru.clevertec.banking.deposit.model.domain;

import lombok.AllArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.clevertec.banking.deposit.util.RandomDepositFactory;
import ru.clevertec.banking.deposit.util.SpringUnitCompositeTest;

import java.math.BigDecimal;
import java.time.LocalDate;

@SpringUnitCompositeTest
@AllArgsConstructor
@Tag("unit")

class DepositTest {
    RandomDepositFactory depositFactory;
    @Test
    void shouldSetDatesToNowIfNull() {
        Deposit deposit = depositFactory.createDeposit();
        LocalDate now = LocalDate.now();
        deposit.getAccInfo().setAccOpenDate(null);
        deposit.getDepInfo().setExpDate(null);
        LocalDate expectedExp = now.plus(deposit.getDepInfo().getTermVal(),
                deposit.getDepInfo().getTermScale().getTemporalUnit());

        deposit.onPrePersist();

        Assertions.assertThat(deposit)
                .extracting(d -> d.getAccInfo().getAccOpenDate(),
                d -> d.getDepInfo().getExpDate())
                .containsExactly(now, expectedExp);
    }

    @Test
    void shouldSetExpDateUsingOpenDatePlusTermIfNull() {
        Deposit deposit = depositFactory.createDeposit();
        deposit.getDepInfo().setExpDate(null);

        LocalDate expectedExp = deposit.getAccInfo().getAccOpenDate().plus(deposit.getDepInfo().getTermVal(),
                deposit.getDepInfo().getTermScale().getTemporalUnit());

        deposit.onPrePersist();

        Assertions.assertThat(deposit)
                .extracting(d -> d.getDepInfo().getExpDate())
                .isEqualTo(expectedExp)
        ;

    }


    @Test
    void shouldSetAmountToZeroIfNull() {
        Deposit deposit = depositFactory.createDeposit();
        deposit.getAccInfo().setCurrAmount(null);
        deposit.onPrePersist();

        Assertions.assertThat(deposit)
                .extracting(d -> d.getAccInfo().getCurrAmount())
                .isEqualTo(BigDecimal.ZERO)
        ;

    }
}
