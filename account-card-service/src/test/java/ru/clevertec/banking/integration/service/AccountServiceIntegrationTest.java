package ru.clevertec.banking.integration.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestConstructor;
import ru.clevertec.banking.advice.exception.ResourceNotFoundException;
import ru.clevertec.banking.dto.account.AccountRequest;
import ru.clevertec.banking.dto.account.AccountRequestForUpdate;
import ru.clevertec.banking.dto.account.AccountResponse;
import ru.clevertec.banking.dto.account.AccountWithCardResponse;
import ru.clevertec.banking.configuration.PostgreSQLContainerConfig;
import ru.clevertec.banking.mapper.AccountMapper;
import ru.clevertec.banking.service.AccountService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@ActiveProfiles("test")
@RequiredArgsConstructor
@ContextConfiguration(classes = {PostgreSQLContainerConfig.class})
@Tag("integration")
public class AccountServiceIntegrationTest {
    private final AccountService accountService;
    private final AccountMapper mapper;
    private AccountResponse response;
    private AccountRequest request;

    @BeforeEach
    void init() {
        response = new AccountResponse(
                "Название счёта №2",
                "RU42100203003212345678012345",
                "RU42 1002 0300 3212 3456 7801 2345",
                BigDecimal.valueOf(3100.00).setScale(2, RoundingMode.DOWN),
                "USD",
                LocalDate.of(2022, 2, 17),
                true,
                UUID.fromString("a3e8c29f-7c16-4a5d-b1c1-2d32f31a8d71"),
                "PHYSIC",
                BigDecimal.valueOf(0.01));

        request = new AccountRequest(
                "Название счёта №10",
                "EN42100203003212345678012345",
                BigDecimal.valueOf(3100.00).setScale(2, RoundingMode.DOWN),
                "USD",
                LocalDate.of(2022, 3, 17),
                true,
                "a3e8c29f-7c16-4a5d-b1c1-2d32f31a8d72",
                "LEGAL",
                BigDecimal.valueOf(0.02));


    }

    @Test
    @DisplayName("test should throw ResourceNotFountException")
    void findByIbanNotFoundTest() {

        assertThatThrownBy(() -> accountService.findByIban("123456789123456")).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("test should return expected response")
    void findByIbanTest() {
        AccountResponse expected = response;

        AccountResponse actual = accountService.findByIban(expected.iban());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("test should return expected response")
    void saveTest() {
        AccountResponse expected = Optional.of(request)
                .map(mapper::fromRequest)
                .map(mapper::toResponse)
                .orElseThrow();

        AccountResponse actual = accountService.save(request);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("test should return List with expected response")
    void findByCustomerTest() {
        AccountResponse a = accountService.save(request);
        AccountWithCardResponse expected = new AccountWithCardResponse(
                a.name(), a.iban(), a.iban_readable(),
                a.amount(), a.currency_code(), a.open_date(),
                a.main_acc(), a.customer_id(), a.customer_type(),
                a.rate(), new ArrayList<>());

        List<AccountWithCardResponse> actual = accountService.findByCustomer(expected.customer_id());

        assertThat(actual).contains(expected);
    }

    @Test
    @DisplayName("test should return not empty Page with responses and pageSize eq 1")
    void getAllTest() {
        Pageable pageable = PageRequest.of(0, 1);

        Page<AccountWithCardResponse> actual = accountService.getAll(pageable);

        assertThat(actual).isNotEmpty().hasSize(1);
    }

    @Test
    @DisplayName("test should return expected response after update")
    void updateTest() {
        AccountRequestForUpdate requestForUpdate = new AccountRequestForUpdate(response.iban(),
                "New Name Account",
                false,
                "LEGAL");

        AccountResponse actual = accountService.update(requestForUpdate);

        assertThat(actual.name()).isEqualTo(requestForUpdate.name());
        assertThat(actual.main_acc()).isEqualTo(requestForUpdate.main_acc());
        assertThat(actual.customer_type()).isEqualTo(requestForUpdate.customer_type());
    }

    @Test
    @DisplayName("test should return an exception after trying to retrieve an Account that has been deleted")
    void deleteByIbanTest() {
        accountService.save(request);

        accountService.deleteByIban(request.iban());

        assertThatThrownBy(() -> accountService.findByIban(request.iban())).isInstanceOf(ResourceNotFoundException.class);
    }
}
