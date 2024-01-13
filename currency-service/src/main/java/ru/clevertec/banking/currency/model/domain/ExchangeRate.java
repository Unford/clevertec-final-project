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
@Entity
@Accessors(chain = true)
@Table(name = "exchange_rates")
public class ExchangeRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 3)
    private String srcCurr;
    @Column(length = 3)
    private String reqCurr;
    private BigDecimal buyRate;
    private BigDecimal sellRate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "exchange_date_id", insertable = false, updatable = false)
    @ToString.Exclude
    private ExchangeData exchangeData;
}
