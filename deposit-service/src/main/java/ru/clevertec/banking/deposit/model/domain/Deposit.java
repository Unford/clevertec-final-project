package ru.clevertec.banking.deposit.model.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import ru.clevertec.banking.deposit.model.CustomerType;

import java.util.UUID;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = "deposits")
@SQLRestriction("deleted = false")
@SQLDelete(sql = "UPDATE deposit.deposits SET deleted = true WHERE id=?")
public class Deposit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private UUID customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerType customerType;

    @Embedded
    private AccountInfo accInfo;

    @Embedded
    private DepositInfo depInfo;

    private Boolean deleted;

    @PrePersist
    public void onPrePersist() {
        this.deleted = false;
    }

}
