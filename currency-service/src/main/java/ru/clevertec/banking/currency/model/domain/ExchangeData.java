package ru.clevertec.banking.currency.model.domain;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Accessors(chain = true)
@Table(name = "exchange_dates")
public class ExchangeData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private OffsetDateTime startDt;
    private OffsetDateTime createdAt;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "exchange_date_id", nullable = false)
    @ToString.Exclude
    private List<ExchangeRate> exchangeRates = new ArrayList<>();


    @PrePersist
    private void onPrePersist() {
        this.createdAt = OffsetDateTime.now();
    }
}
