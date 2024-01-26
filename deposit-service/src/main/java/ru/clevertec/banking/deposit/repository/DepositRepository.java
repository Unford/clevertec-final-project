package ru.clevertec.banking.deposit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ru.clevertec.banking.deposit.model.domain.Deposit;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DepositRepository extends JpaRepository<Deposit, Long>, JpaSpecificationExecutor<Deposit> {
    Optional<Deposit> findByAccInfoAccIban(String iban);
    void deleteByAccInfoAccIban(String iban);

    List<Deposit> findAllByCustomerId(UUID customerId);

    boolean existsByAccInfoAccIban(String accIban);

    @Query(value = "SELECT d.* FROM  {h-schema}deposits d WHERE d.acc_Iban = ?1", nativeQuery = true)
    Optional<Deposit> findByAccInfoAccIbanWithDeleted(String iban);
}
