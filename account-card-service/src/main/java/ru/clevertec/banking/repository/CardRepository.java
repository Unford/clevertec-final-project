package ru.clevertec.banking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.clevertec.banking.entity.Card;

import java.util.Optional;
import java.util.UUID;

public interface CardRepository extends PagingAndSortingRepository<Card, String>, JpaSpecificationExecutor<Card> {

    Card save(Card card);

    Optional<Card> findCardByCardNumber(String cardNumber);

    Page<Card> findCardsByCustomerId(UUID customerId, Pageable pageable);

    Page<Card> findCardsByIban(String iban, Pageable pageable);

    void deleteCardByCardNumber(String cardNumber);

    boolean existsByCardNumber(String cardNumber);
}
