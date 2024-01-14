package ru.clevertec.banking.deposit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.clevertec.banking.deposit.model.domain.Deposit;

import java.util.Optional;

public interface DepositRepository extends JpaRepository<Deposit, Long> {
    Optional<Deposit> findByAccInfoAccIban(String iban);
}
