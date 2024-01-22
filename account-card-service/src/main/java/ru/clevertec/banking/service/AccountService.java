package ru.clevertec.banking.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.clevertec.banking.dto.account.AccountRequest;
import ru.clevertec.banking.dto.account.AccountRequestForUpdate;
import ru.clevertec.banking.dto.account.AccountResponse;
import ru.clevertec.banking.dto.account.AccountWithCardResponse;
import ru.clevertec.banking.entity.Account;

import java.util.List;
import java.util.UUID;

public interface AccountService {
    AccountResponse save(AccountRequest request);

    Page<AccountWithCardResponse> getAll(Pageable pageable);

    List<AccountWithCardResponse> findByCustomer(UUID uuid);

    AccountResponse findByIban(String iban);

    AccountResponse update(AccountRequestForUpdate request);

    void deleteByIban(String iban);

    void saveOrUpdate(AccountRequest request);
}
