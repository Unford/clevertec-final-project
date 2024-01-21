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
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Entity
@NamedEntityGraph(name = "ExchangeData.exchangeRates",
        attributeNodes = @NamedAttributeNode("exchangeRates"))
@Table(name = "exchange_dates")
public class ExchangeData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    @Column(nullable = false, unique = true)
    private OffsetDateTime startDt;


    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    @JoinColumn(name = "exchange_date_id", nullable = false)
    @ToString.Exclude
    private List<ExchangeRate> exchangeRates = new ArrayList<>();


}
