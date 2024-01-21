package ru.clevertec.banking.deposit.service;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import feign.FeignException;
import lombok.AllArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ContextConfiguration;
import ru.clevertec.banking.advice.exception.ResourceNotFoundException;
import ru.clevertec.banking.deposit.configuration.DataFakerConfiguration;
import ru.clevertec.banking.deposit.configuration.PostgresContainerConfiguration;
import ru.clevertec.banking.deposit.model.domain.Deposit;
import ru.clevertec.banking.deposit.model.dto.request.CreateDepositRequest;
import ru.clevertec.banking.deposit.model.dto.request.UpdateDepositRequest;
import ru.clevertec.banking.deposit.model.dto.response.AccountInfoResponse;
import ru.clevertec.banking.deposit.model.dto.response.DepositInfoResponse;
import ru.clevertec.banking.deposit.model.dto.response.DepositResponse;
import ru.clevertec.banking.deposit.repository.DepositRepository;
import ru.clevertec.banking.deposit.util.RandomDepositFactory;
import ru.clevertec.banking.deposit.util.SpringBootCompositeTest;
import ru.clevertec.banking.security.model.Role;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static ru.clevertec.banking.deposit.util.FileReaderUtil.readFile;

@SpringBootCompositeTest
@ContextConfiguration(classes = {PostgresContainerConfiguration.class,
        DataFakerConfiguration.class})
@AllArgsConstructor
@Tag("integration")
class DepositServiceIntegrationTest {
    DepositService depositService;
    DepositRepository depositRepository;
    RandomDepositFactory depositFactory;

    @Nested
    @WireMockTest(httpPort = 7777)
    class CustomerServiceTest {
        @Test
        void shouldGetCustomerFromClientAndSave() throws Exception {
            CreateDepositRequest expected = depositFactory.createDepositRequest();

            WireMock.stubFor(WireMock.get("/api/v1/customers/" + expected.getCustomerId().toString())
                    .willReturn(WireMock.okJson(readFile("/customer/get-customer-by-id-response.json"))
                            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));


            DepositResponse saved = depositService.save(expected);

            Assertions.assertThat(saved)
                    .extracting(DepositResponse::getId)
                    .isNotNull();
            Assertions.assertThat(saved)
                    .extracting(DepositResponse::getCustomerId)
                    .isEqualTo(expected.getCustomerId());
        }

        @Test
        void shouldNotFoundCustomerFromClientAndThrowException() throws Exception {
            CreateDepositRequest expected = depositFactory.createDepositRequest();

            WireMock.stubFor(WireMock.get("/api/v1/customers/" + expected.getCustomerId().toString())
                    .willReturn(WireMock.jsonResponse(readFile("/customer/get-customer-by-id-not-found-response.json"),
                                    HttpStatus.NOT_FOUND.value())
                            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));


            Assertions.assertThatThrownBy(() -> depositService.save(expected))
                    .isInstanceOf(FeignException.NotFound.class);
        }
    }

    @Test
    void shouldThrowNotFoundExceptionWhenFindByIban() {
        Assertions.assertThatThrownBy(() -> depositService.findByAccountIban("I am not exist"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldFindDepositByIban() {
        String expected = "123456789";
        DepositResponse actual = depositService.findByAccountIban(expected);

        Assertions.assertThat(actual)
                .extracting(DepositResponse::getAccInfo)
                .extracting(AccountInfoResponse::getAccIban)
                .isEqualTo(expected);
    }


    @Test
    void shouldReturnTrueAfterFindDepositByIban() {
        String iban = "123456789";
        boolean actual = depositService.isDepositExistByIban(iban);
        Assertions.assertThat(actual).isTrue();

    }

    @Test
    void shouldReturnFalseAfterFindDepositByIban() {
        String iban = "I am not exist";
        boolean actual = depositService.isDepositExistByIban(iban);
        Assertions.assertThat(actual).isFalse();

    }

    @Test
    void shouldDeleteDepositByIban() {
        String iban = "183456789";
        depositService.deleteByAccountIban(iban);
        Optional<Deposit> actual = depositRepository.findByAccInfoAccIban(iban);
        Assertions.assertThat(actual).isEmpty();
    }

    @Test
    void shouldReturnListOfDepositsByCustomer() {
        UUID customerId = UUID.randomUUID();
        List<Deposit> deposits = Stream.generate(depositFactory::createDeposit)
                .limit(5)
                .map(d -> d.setId(null))
                .map(d -> d.setCustomerId(customerId))
                .toList();
        depositRepository.saveAllAndFlush(deposits);

        List<DepositResponse> actual = depositService.findAllByCustomerId(customerId);
        Assertions.assertThat(actual).hasSize(deposits.size())
                .allSatisfy(d -> Assertions.assertThat(d.getCustomerId()).isEqualTo(customerId));
    }

    @Test
    void shouldReturnPageOfDepositsByCustomerWithRoleUser() {
        int expectedSize = 5;
        UUID customerId = UUID.randomUUID();
        List<Deposit> deposits = Stream.generate(depositFactory::createDeposit)
                .limit(expectedSize)
                .map(d -> d.setId(null))
                .map(d -> d.setCustomerId(customerId))
                .toList();
        depositRepository.saveAllAndFlush(deposits);


        Page<DepositResponse> actual = depositService.findPageByRole(Pageable.ofSize(expectedSize),
                new UsernamePasswordAuthenticationToken(customerId, null, List.of(Role.USER.toAuthority())));

        Assertions.assertThat(actual)
                .hasSize(expectedSize)
                .allSatisfy(d -> Assertions.assertThat(d.getCustomerId()).isEqualTo(customerId));
        Assertions.assertThat(actual.getTotalElements()).isEqualTo(expectedSize);
    }

    @Test
    void shouldReturnPageOfAllDepositsWithRoleAdmin() {
        int expectedSize = 10;
        Page<DepositResponse> actual = depositService.findPageByRole(Pageable.ofSize(expectedSize),
                new UsernamePasswordAuthenticationToken(UUID.randomUUID(), null, List.of(Role.ADMIN.toAuthority())));

        long uniqueCount = actual.getContent().stream().map(DepositResponse::getCustomerId).distinct().count();
        Assertions.assertThat(actual).hasSize(expectedSize);
        Assertions.assertThat(uniqueCount).isNotEqualTo(1);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUpdate() {
        String iban = "I am not exist";
        UpdateDepositRequest request = depositFactory.createUpdateDepositRequest();
        Assertions.assertThatThrownBy(() -> depositService.update(iban, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldReturnUpdatedDepositWhenUpdate() {
        String iban = "944454324";
        UpdateDepositRequest expected = depositFactory.createUpdateDepositRequest();
        DepositResponse actual = depositService.update(iban, expected);

        Assertions.assertThat(actual)
                .extracting(DepositResponse::getDepInfo)
                .extracting(DepositInfoResponse::getAutoRenew)
                .isEqualTo(expected.getDepInfo().getAutoRenew());
        Assertions.assertThat(actual)
                .extracting(DepositResponse::getDepInfo)
                .extracting(DepositInfoResponse::getDepType)
                .isEqualTo(expected.getDepInfo().getDepType());
    }


}
