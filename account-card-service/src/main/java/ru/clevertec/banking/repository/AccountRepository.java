package ru.clevertec.banking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.clevertec.banking.entity.Account;

import java.util.Optional;

public interface AccountRepository extends PagingAndSortingRepository<Account, String>, JpaSpecificationExecutor<Account>, JpaRepository<Account, String> {

    Account save(Account account);

    Optional<Account> findAccountByIban(String iban);

    void deleteAccountByIban(String iban);

    boolean existsAccountByIban(String iban);

    void deleteAll();

    @Query(value = "SELECT a.* FROM account a WHERE a.iban = ?1", nativeQuery = true)
    Optional<Account> findAccountByIbanWithDeleted(String iban);

}
