package ru.clevertec.banking.currency.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.clevertec.banking.currency.model.domain.ExchangeData;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface ExchangeDataRepository extends JpaRepository<ExchangeData, Long> {



    @EntityGraph(value = "ExchangeData.exchangeRates")
    Optional<ExchangeData> findFirstByStartDtIsLessThanEqualOrderByStartDtDesc(OffsetDateTime offsetDateTime);
    @EntityGraph(value = "ExchangeData.exchangeRates")
    Optional<ExchangeData> findByStartDt(OffsetDateTime offsetDateTime);

}
