package ru.clevertec.banking.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.clevertec.banking.dto.account.AccountRequest;
import ru.clevertec.banking.dto.account.AccountRequestForUpdate;
import ru.clevertec.banking.dto.account.AccountResponse;
import ru.clevertec.banking.dto.account.AccountWithCardResponse;

import java.util.List;

public interface AccountService {
    AccountResponse save(AccountRequest request);

    Page<AccountWithCardResponse> getAll(Pageable pageable);

    List<AccountWithCardResponse> findByCustomer(String uuid);

    AccountResponse findByIban(String iban);

    AccountResponse update(AccountRequestForUpdate request);

    void deleteByIban(String iban);
}
