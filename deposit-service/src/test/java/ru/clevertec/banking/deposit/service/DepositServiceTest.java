package ru.clevertec.banking.deposit.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import ru.clevertec.banking.advice.exception.ResourceNotFoundException;
import ru.clevertec.banking.deposit.client.CustomerClient;
import ru.clevertec.banking.deposit.mapper.DepositMapper;
import ru.clevertec.banking.deposit.model.dto.request.CreateDepositRequest;
import ru.clevertec.banking.deposit.repository.DepositRepository;
import ru.clevertec.banking.deposit.util.RandomDepositFactory;
import ru.clevertec.banking.deposit.util.SpringUnitCompositeTest;

@SpringUnitCompositeTest
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class DepositServiceTest {
    @Autowired
    RandomDepositFactory depositFactory;

    @InjectMocks
    DepositService depositService;

    @Mock
    DepositRepository depositRepository;
    @Mock
    DepositMapper depositMapper;
    @Mock
    CustomerClient customerClient;

    @Test
    void shouldThrowNotFoundExceptionWhenSaveAfterFallback() {
        CreateDepositRequest depositRequest = depositFactory.createDepositRequest();
        Assertions.assertThatThrownBy(() -> depositService.save(depositRequest))
                .isInstanceOf(ResourceNotFoundException.class);

    }


}
