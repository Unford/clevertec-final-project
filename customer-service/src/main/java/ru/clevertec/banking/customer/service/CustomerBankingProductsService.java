package ru.clevertec.banking.customer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.banking.customer.client.AccountAndLinkedCardsClient;
import ru.clevertec.banking.customer.client.CreditClient;
import ru.clevertec.banking.customer.client.DepositClient;
import ru.clevertec.banking.customer.dto.response.CustomerBankingProductsResponse;
import ru.clevertec.banking.customer.dto.response.client.account.AccountWithCardResponse;
import ru.clevertec.banking.customer.dto.response.client.credit.CreditResponse;
import ru.clevertec.banking.customer.dto.response.client.deposit.DepositResponse;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomerBankingProductsService {

    private final DelegatingSecurityContextAsyncTaskExecutor executor;
    private final AccountAndLinkedCardsClient accountAndLinkedCardsClient;
    private final CreditClient creditClient;
    private final DepositClient depositClient;

    public CompletableFuture<List<AccountWithCardResponse>> getAccountsAndCards(UUID customerId) {
        return CompletableFuture.supplyAsync(() -> accountAndLinkedCardsClient.findAllByCustomerId(customerId), executor);
    }

    public CompletableFuture<List<CreditResponse>> getCredits(UUID customerId) {
        return CompletableFuture.supplyAsync(() -> creditClient.findAllByCustomerId(customerId), executor);
    }

    public CompletableFuture<List<DepositResponse>> getDeposits(UUID customerId) {
        return CompletableFuture.supplyAsync(() -> depositClient.findAllByCustomerId(customerId), executor);
    }

    public CustomerBankingProductsResponse getCustomerBankingProducts(UUID customerId) {
        CompletableFuture<List<AccountWithCardResponse>> accountsAndCardsFuture = getAccountsAndCards(customerId);
        CompletableFuture<List<CreditResponse>> creditsFuture = getCredits(customerId);
        CompletableFuture<List<DepositResponse>> depositsFuture = getDeposits(customerId);

        return CompletableFuture.allOf(accountsAndCardsFuture, creditsFuture, depositsFuture)
                                .thenApplyAsync(ignored -> {
                                    List<AccountWithCardResponse> accountsAndCardsResponse = accountsAndCardsFuture.join();
                                    List<CreditResponse> creditsResponse = creditsFuture.join();
                                    List<DepositResponse> depositsResponse = depositsFuture.join();

                                    return new CustomerBankingProductsResponse(accountsAndCardsResponse, creditsResponse, depositsResponse);
                                }).join();
    }
}