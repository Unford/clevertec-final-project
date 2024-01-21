package ru.clevertec.banking.customer.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import ru.clevertec.banking.customer.client.AccountAndLinkedCardsClient;
import ru.clevertec.banking.customer.client.CreditClient;
import ru.clevertec.banking.customer.client.DepositClient;
import ru.clevertec.banking.customer.dto.response.CustomerBankingProductsResponse;
import ru.clevertec.banking.customer.dto.response.client.account.AccountWithCardResponse;
import ru.clevertec.banking.customer.dto.response.client.credit.CreditResponse;
import ru.clevertec.banking.customer.dto.response.client.deposit.DepositResponse;
import ru.clevertec.banking.customer.testutil.builders.AccountWithCardResponseTestBuilder;
import ru.clevertec.banking.customer.testutil.builders.CreditResponseTestBuilder;
import ru.clevertec.banking.customer.testutil.builders.DepositResponseTestBuilder;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CustomerBankingProductsServiceTest {

    @Spy
    private ConcurrentTaskExecutor executor = new ConcurrentTaskExecutor(Executors.newSingleThreadExecutor());
    @Mock
    private AccountAndLinkedCardsClient accountAndLinkedCardsClient;
    @Mock
    private CreditClient creditClient;
    @Mock
    private DepositClient depositClient;

    @InjectMocks
    private CustomerBankingProductsService customerBankingProductsService;


    @Test
    @DisplayName("should return accounts and cards from client in future")
    void testGetAccountsAndCardsFromClient() {
        // given
        UUID customerId = UUID.fromString("1a72a05f-4b8f-43c5-a889-1ebc6d9dc729");
        List<AccountWithCardResponse> accountWithCardResponses = buildAccountsWithCardsResponse();

        // when
        when(accountAndLinkedCardsClient.findAllByCustomerId(customerId))
                .thenReturn(accountWithCardResponses);

        // then
        CompletableFuture<List<AccountWithCardResponse>> future =
                customerBankingProductsService.getAccountsAndCards(customerId);
        List<AccountWithCardResponse> result = future.join();

        assertThat(result, containsInAnyOrder(accountWithCardResponses.toArray()));
        verify(executor).execute(any(Runnable.class));
    }

    @Test
    @DisplayName("should return credits from client in future")
    void testGetCreditsFromClient() {
        // given
        UUID customerId = UUID.fromString("1a72a05f-4b8f-43c5-a889-1ebc6d9dc729");
        List<CreditResponse> creditResponses = buildCreditsResponse();

        // when
        when(creditClient.findAllByCustomerId(customerId))
                .thenReturn(creditResponses);

        // then
        CompletableFuture<List<CreditResponse>> future = customerBankingProductsService.getCredits(customerId);
        List<CreditResponse> result = future.join();

        assertThat(result, containsInAnyOrder(creditResponses.toArray()));
        verify(executor).execute(any(Runnable.class));
    }

    @Test
    @DisplayName("should return deposits from client in future")
    void testGetDepositsFromClient() {
        // given
        UUID customerId = UUID.fromString("1a72a05f-4b8f-43c5-a889-1ebc6d9dc729");
        List<DepositResponse> depositResponses = buildDepositsResponse();

        // when
        when(depositClient.findAllByCustomerId(customerId))
                .thenReturn(depositResponses);

        // then
        CompletableFuture<List<DepositResponse>> future = customerBankingProductsService.getDeposits(customerId);
        List<DepositResponse> result = future.join();

        assertThat(result, containsInAnyOrder(depositResponses.toArray()));
        verify(executor).execute(any(Runnable.class));
    }

    @Test
    @DisplayName("should return combined customer banking products response")
    void getCustomerBankingProducts() {
        //given
        UUID customerId = UUID.randomUUID();
        List<AccountWithCardResponse> accountWithCardResponses = buildAccountsWithCardsResponse();
        List<CreditResponse> creditResponses = buildCreditsResponse();
        List<DepositResponse> depositResponses = buildDepositsResponse();

        //when
        doReturn(accountWithCardResponses)
                .when(accountAndLinkedCardsClient)
                .findAllByCustomerId(customerId);

        doReturn(creditResponses)
                .when(creditClient)
                .findAllByCustomerId(customerId);

        doReturn(depositResponses)
                .when(depositClient)
                .findAllByCustomerId(customerId);

        //then
        CustomerBankingProductsResponse result = customerBankingProductsService.getCustomerBankingProducts(customerId);
        assertNotNull(result);
        assertEquals(accountWithCardResponses, result.getAccountsWithCardsResponse());
        assertEquals(creditResponses, result.getCreditsResponse());
        assertEquals(depositResponses, result.getDepositsResponse());
    }

    private List<DepositResponse> buildDepositsResponse() {
        DepositResponse deposit = new DepositResponseTestBuilder().build();
        DepositResponse deposit2 = new DepositResponseTestBuilder().build();
        deposit2.setId(2L);

        return List.of(deposit, deposit2);
    }

    private List<CreditResponse> buildCreditsResponse() {
        CreditResponse credit = new CreditResponseTestBuilder().build();
        CreditResponse credit2 = new CreditResponseTestBuilder().build();
        credit2.setIban("BBAADDDEEEEOOOOOOOOOOOOOOOO")
               .setContractNumber("11-0216444-3-0");

        return List.of(credit, credit2);
    }

    private List<AccountWithCardResponse> buildAccountsWithCardsResponse() {
        AccountWithCardResponse account = new AccountWithCardResponseTestBuilder().build();
        AccountWithCardResponse account2 = new AccountWithCardResponseTestBuilder().build();
        account2.setName("Account2")
                .setIban("BBAADDDEEEEOOOOOOOOOOOOOOOO");
        return List.of(account, account2);
    }
}

