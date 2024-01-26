package ru.clevertec.banking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "account")
@SQLDelete(sql = "UPDATE {h-schema}account SET deleted = true WHERE iban=?")
@SQLRestriction(value = "deleted = false")
public class Account {
    private String name;
    @Id
    private String iban;
    private BigDecimal amount;
    private String currencyCode;
    private LocalDate openDate;
    private boolean mainAcc;
    private UUID customerId;
    private String customerType;
    private BigDecimal rate;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "account")
    private List<Card> cards;
    private boolean deleted = Boolean.FALSE;
}
