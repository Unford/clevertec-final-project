package ru.clevertec.banking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.clevertec.banking.entity.Card;

import java.util.Optional;

public interface CardRepository extends PagingAndSortingRepository<Card, String>, JpaSpecificationExecutor<Card>, JpaRepository<Card, String> {

    Optional<Card> findCardByCardNumber(String cardNumber);

    void deleteCardByCardNumber(String cardNumber);

    boolean existsByCardNumber(String cardNumber);

    void deleteAll();

    @Query(value = "SELECT c.* FROM {h-schema}card c WHERE c.card_number = ?1", nativeQuery = true)
    Optional<Card> findCardByCardNumberWithDeleted(String cardNumber);
}
