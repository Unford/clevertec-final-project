package ru.clevertec.banking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "card")
@SQLDelete(sql = "UPDATE {h-schema}card SET deleted = true WHERE card_number=?")
@SQLRestriction(value = "deleted = false")
public class Card {
    @Id
    private String cardNumber;
    private String iban;
    private UUID customerId;
    private String customerType;
    private String cardholder;
    private String cardStatus;
    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "iban", referencedColumnName = "iban", insertable = false, updatable = false)
    private Account account;
    private boolean deleted = Boolean.FALSE;
}
