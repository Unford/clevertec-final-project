package ru.clevertec.banking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.clevertec.banking.entity.Credit;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CreditRepository extends PagingAndSortingRepository<Credit, String>, JpaRepository<Credit, String> {
    List<Credit> findCreditsByCustomerId(UUID customerId);

    Credit findCreditByContractNumber(String contractNumber);

    Credit save(Credit credit);

    void deleteByContractNumber(String contractNumber);

    boolean existsCreditByContractNumber(String contractNumber);

    boolean existsCreditByIban(String iban);

    void deleteAll();

    @Query(value = "SELECT c.* FROM {h-schema}credit c WHERE c.contract_number = ?1", nativeQuery = true)
    Optional<Credit> findCreditByContractNumberWithDeleted(String contractNumber);
}
