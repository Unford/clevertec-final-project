package ru.clevertec.banking.customer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.clevertec.banking.customer.entity.Customer;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID>, JpaSpecificationExecutor<Customer> {
    Optional<Customer> findByUnpAndUnpNotNull(String unp);

    Optional<Customer> findByIdOrEmailOrUnp(UUID id, String email, String unp);

    Optional<Customer> findByIdOrEmail(UUID id, String email);

    boolean existsByIdOrEmailOrUnp(UUID id, String email, String unp);

    boolean existsByIdOrEmail(UUID id, String email);
}