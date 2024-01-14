package ru.clevertec.banking.customer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
@SQLDelete(sql = "UPDATE customer SET deleted = true WHERE id=?")
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id) && customerType == customer.customerType &&
               Objects.equals(unp, customer.unp) &&
               Objects.equals(registerDate, customer.registerDate) &&
               Objects.equals(email, customer.email) && Objects.equals(phoneCode, customer.phoneCode) &&
               Objects.equals(phoneNumber, customer.phoneNumber) &&
               Objects.equals(customerFullname, customer.customerFullname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customerType, unp, registerDate, email, phoneCode, phoneNumber, customerFullname);
    }
}