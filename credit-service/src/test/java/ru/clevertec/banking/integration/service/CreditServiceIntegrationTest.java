package ru.clevertec.banking.integration.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestConstructor;
import ru.clevertec.banking.advice.exception.ResourceNotFoundException;
import ru.clevertec.banking.configuration.PostgreSQLContainerConfig;
import ru.clevertec.banking.dto.CreditRequest;
import ru.clevertec.banking.dto.CreditRequestForUpdate;
import ru.clevertec.banking.dto.CreditResponse;
import ru.clevertec.banking.mapper.CreditMapper;
import ru.clevertec.banking.service.CreditService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static ru.clevertec.banking.util.CreditFactory.getRequest;
import static ru.clevertec.banking.util.CreditFactory.getResponse;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@ActiveProfiles("test")
@RequiredArgsConstructor
@ContextConfiguration(classes = {PostgreSQLContainerConfig.class})
@Tag("integration")
public class CreditServiceIntegrationTest {
    private final CreditService creditService;
    private final CreditMapper mapper;
    private CreditResponse response;

    @BeforeEach
    void init() {
        response = new CreditResponse(
                UUID.fromString("fbd6a92b-9e43-4c88-aa2b-6e1fe788d9c4"),
                "11-0216671-1-0",
                LocalDate.of(2023, 1, 11),
                BigDecimal.valueOf(8888.99),
                BigDecimal.valueOf(325.99),
                "USD",
                LocalDate.of(2026, 01, 16),
                BigDecimal.valueOf(22.8),
                "GB29NWBK60161331926819012345",
                true, false, "LEGAL");
    }

    @Test
    @DisplayName("test should return expected responses")
    void saveTest() {
        CreditResponse expected = getResponse();
        CreditRequest request = getRequest();

        CreditResponse actual = creditService.save(request);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("test should return List with expected response")
    void findByCustomerTest() {
        CreditResponse expected = response;

        List<CreditResponse> actual = creditService.findByCustomer(expected.customer_id());

        assertThat(actual).contains(expected);
    }

    @Test
    @DisplayName("test should return empty List")
    void findByCustomerEmptyResultTest() {
        List<CreditResponse> actual = creditService.findByCustomer(UUID.randomUUID());

        assertThat(actual).isEmpty();
    }

    @Test
    @DisplayName("test should return expected response")
    void findByContractNumberTest() {
        CreditResponse expected = response;

        CreditResponse actual = creditService.findByContractNumber(response.contractNumber());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("test should throw ResourceNotFoundException")
    void findByContractNumberNotFoundTest() {
        assertThatThrownBy(() -> creditService.findByContractNumber("nope"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("test should return Page with expected response")
    void getAllTest() {
        CreditResponse expected = response;

        Page<CreditResponse> actual = creditService.getAll(Pageable.ofSize(20));

        assertThat(actual).contains(expected);
    }

    @Test
    @DisplayName("test should return expected response after update")
    void updateTest() {
        CreditRequestForUpdate request = new CreditRequestForUpdate(
                "11-0216133-2-0",
                LocalDate.of(2030, 11, 11),
                BigDecimal.valueOf(22.11),
                false,
                false,
                "LEGAL"
        );

        CreditResponse actual = creditService.update(request);

        assertThat(actual.isClosed()).isEqualTo(request.isClosed());
        assertThat(actual.customer_type()).isEqualTo(request.customer_type());
        assertThat(actual.possibleRepayment()).isEqualTo(request.possibleRepayment());
        assertThat(actual.repaymentDate()).isEqualTo(request.repaymentDate());
        assertThat(actual.rate()).isEqualTo(request.rate());
    }

    @Test
    @DisplayName("test should throw ResourceNotFoundException")
    void updateNotFoundTest() {
        CreditRequestForUpdate request = new CreditRequestForUpdate(
                "1231241251251", null, null, null, null, null
        );

        assertThatThrownBy(() -> creditService.update(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("test should return an exception after trying to retrieve an Card that has been deleted")
    void delete() {
        CreditResponse actualSavedCredit = creditService.save(getRequest());

        assertThat(actualSavedCredit).isNotNull();

        creditService.delete(actualSavedCredit.contractNumber());

        assertThatThrownBy(() -> creditService.findByContractNumber(actualSavedCredit.contractNumber()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

}
