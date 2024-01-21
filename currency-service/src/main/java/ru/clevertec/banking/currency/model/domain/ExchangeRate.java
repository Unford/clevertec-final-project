package ru.clevertec.banking.currency.model.domain;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Accessors(chain = true)
@Table(name = "exchange_rates")
public class ExchangeRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    @Column(length = 3, nullable = false)
    private String srcCurr;
    @Column(length = 3, nullable = false)
    private String reqCurr;
    @Column(nullable = false)
    private BigDecimal buyRate;
    @Column(nullable = false)
    private BigDecimal sellRate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "exchange_date_id", nullable = false, insertable = false, updatable = false)
    @ToString.Exclude
    private ExchangeData exchangeData;
}
