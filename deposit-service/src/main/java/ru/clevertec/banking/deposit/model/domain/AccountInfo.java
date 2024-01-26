package ru.clevertec.banking.deposit.model.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;

@Embeddable
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class AccountInfo {
    @Column(unique = true, nullable = false)
    private String accIban;
    @Column(nullable = false)
    private LocalDate accOpenDate;
    @Column(nullable = false)
    private BigDecimal currAmount;
    @Column(length = 3, nullable = false)
    private String currAmountCurrency;
}
