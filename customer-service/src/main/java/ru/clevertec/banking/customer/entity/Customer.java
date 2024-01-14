package ru.clevertec.banking.customer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "customers", indexes = {
        @Index(name = "idx_customer_email_unq", columnList = "email", unique = true)
})
public class Customer {

    @Id
    @Column(updatable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(updatable = false)
    private CustomerType customerType;

    @Column(unique = true, updatable = false)
    private String unp;

    @DateTimeFormat(pattern = "dd.MM.yyyy")
    @Column(updatable = false)
    private LocalDate registerDate;

    @Column(unique = true, updatable = false)
    private String email;

    @Column(updatable = false)
    private String phoneCode;

    @Column(updatable = false)
    private String phoneNumber;

    @Column(updatable = false)
    private String customerFullname;

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

/*
{
    "header": {
        "message_type": "customer"
    },
    "payload": {
        "customer_id": "1a72a05f-4b8f-43c5-a889-1ebc6d9dc729" (приходит с системы - use UUID),
        "customer_type" : "LEGAL/PHYSIC",
        "unp": "Только для LEGAL",
        "register_date": "dd.MM.yyyy",
        "email": "example@email.com",
        "phoneCode": "37529",
        "phoneNumber": "1112233",
        "customer_fullname": "Иванов Иван Иванович"
    }
}
*/
