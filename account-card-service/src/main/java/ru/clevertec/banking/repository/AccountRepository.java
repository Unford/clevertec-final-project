package ru.clevertec.banking.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.clevertec.banking.entity.Account;

import java.util.Optional;

public interface AccountRepository extends PagingAndSortingRepository<Account, String>, JpaSpecificationExecutor<Account> {

    Account save(Account account);

    Optional<Account> findAccountByIban(String iban);

    void deleteAccountByIban(String iban);

    boolean existsAccountByIban(String iban);

    void deleteAll();
}
