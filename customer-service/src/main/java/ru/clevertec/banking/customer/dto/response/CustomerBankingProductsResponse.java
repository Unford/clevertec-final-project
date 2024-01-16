package ru.clevertec.banking.customer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.clevertec.banking.customer.dto.response.client.account.AccountWithCardResponse;
import ru.clevertec.banking.customer.dto.response.client.credit.CreditResponse;
import ru.clevertec.banking.customer.dto.response.client.deposit.DepositResponse;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerBankingProductsResponse {
    private List<AccountWithCardResponse> accountsWithCardsResponse;
    private List<CreditResponse> creditsResponse;
    private List<DepositResponse> depositsResponse;
}
