package ru.clevertec.banking.customer.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "customer", indexes = {
        @Index(name = "idx_customer_email_unq", columnList = "email", unique = true),
        @Index(name = "idx_customer_unp_unq", columnList = "unp", unique = true)
})
@SQLDelete(sql = "UPDATE {h-schema}customer SET deleted = true WHERE id=?")
@SQLRestriction("deleted = false")
public class Customer {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    private CustomerType customerType;

    @Column(unique = true)
    private String unp;

    @DateTimeFormat(pattern = "dd.MM.yyyy")
    private LocalDate registerDate;

    @Column(unique = true)
    private String email;

    private String phoneCode;

    private String phoneNumber;

    private String customerFullname;

    private boolean deleted = Boolean.FALSE;

    @PrePersist
    public void onPrePersist() {
        if (Objects.isNull(registerDate)) {
            registerDate = LocalDate.now();
        }
        if (this.customerType == CustomerType.PHYSIC) {
            this.unp = null;
        }
    }
}