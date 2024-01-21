package ru.clevertec.banking.customer.entity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.clevertec.banking.customer.testutil.builders.CustomerTestBuilder;

import java.time.LocalDate;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class CustomerTest {

    @Test
    void shouldSetRegisterDateToNowIfNull() {
        Customer customer = new CustomerTestBuilder().build();
        customer.setRegisterDate(null);
        customer.onPrePersist();
        LocalDate now = LocalDate.now();
        Assertions.assertThat(customer.getRegisterDate()).isEqualTo(now);
    }

    @Test
    void shouldSetUnpToNullIfCustomerTypeIsPhysic() {
        Customer customer = new CustomerTestBuilder().build();
        customer.setCustomerType(CustomerType.PHYSIC);
        customer.onPrePersist();
        Assertions.assertThat(customer.getUnp()).isNull();
    }
}