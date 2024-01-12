package ru.clevertec.banking.currency.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.clevertec.banking.currency.model.domain.ExchangeData;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface ExchangeDataRepository extends JpaRepository<ExchangeData, Long> {

    Optional<ExchangeData> findFirstByStartDtIsLessThanEqualOrderByStartDtDescCreatedAtDesc(OffsetDateTime offsetDateTime);


}
