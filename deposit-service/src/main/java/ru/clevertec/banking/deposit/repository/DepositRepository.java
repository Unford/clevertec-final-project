package ru.clevertec.banking.deposit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.clevertec.banking.deposit.model.domain.Deposit;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DepositRepository extends JpaRepository<Deposit, Long>, JpaSpecificationExecutor<Deposit> {
    Optional<Deposit> findByAccInfoAccIban(String iban);
    void deleteByAccInfoAccIban(String iban);

    List<Deposit> findAllByCustomerId(UUID customerId);

    boolean existsByAccInfoAccIban(String accIban);
}
