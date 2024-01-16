package ru.clevertec.banking.deposit.model.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import ru.clevertec.banking.deposit.model.DepositType;
import ru.clevertec.banking.deposit.model.TermScale;

import java.math.BigDecimal;
import java.time.LocalDate;

@Embeddable
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class DepositInfo {
    @Column(nullable = false)
    private BigDecimal rate;

    @Column(nullable = false)
    private Integer termVal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TermScale termScale;

    @Column(nullable = false)
    private LocalDate expDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DepositType depType;

    @Column(nullable = false)
    private Boolean autoRenew;
}
